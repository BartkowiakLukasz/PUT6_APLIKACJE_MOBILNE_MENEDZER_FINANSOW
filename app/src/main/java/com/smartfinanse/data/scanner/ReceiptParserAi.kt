package com.smartfinanse.data.scanner

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.smartfinanse.BuildConfig

import android.graphics.Bitmap
import kotlinx.coroutines.delay

data class ParsedReceipt(
    val sklep: String,
    val opis: String,
    val kwota: Double,
    val data: String?,
    val kategoria: String,
    val czyGotowka: Boolean?
)

sealed class ScannerException : Exception() {
    object ServerBusy : ScannerException()
    object NetworkError : ScannerException()
    object InvalidImage : ScannerException()
}

class ReceiptParserAi {

    private val gson = Gson()

    suspend fun parseReceiptImage(bitmap: Bitmap, availableCategories: List<String>): ParsedReceipt {
        val categoriesStr = availableCategories.joinToString(", ")
        
        val systemInstruction = """
            Jesteś ekspertem finansowym. Otrzymasz zdjęcie paragonu. Twoim zadaniem jest wyciągnięcie z niego 6 informacji:
            'sklep' - nazwa sprzedawcy (np. Biedronka, Orlen).
            'opis' - krótki, jedno- lub dwuwyrazowy opis najważniejszych kupionych rzeczy (np. 'Produkty spożywcze', 'Paliwo', 'Chemia domowa').
            'kwota' - ostateczna kwota do zapłaty (tylko liczba, kropka jako separator dziesiętny). Zignoruj podatek VAT, resztę, czy podsumowania przed zniżkami. Szukaj 'Suma', 'Do zapłaty', 'Zapłacono'.
            'data' - data zakupu w formacie YYYY-MM-DD. Jeśli nie potrafisz jej znaleźć, zwróć null.
            'kategoria' - przypisz zakup do jednej z kategorii: [$categoriesStr]. Wybierz najbardziej pasującą lub 'Inne' jeśli nie pasuje żadna.
            'czyGotowka' - true jeśli płatność była gotówką, false jeśli płatność była kartą/blikiem/telefonem. Jeśli nie masz pewności, zwróć null.
            Zwróć WYŁĄCZNIE poprawny JSON o strukturze: {"sklep": "", "opis": "", "kwota": 0.0, "data": "", "kategoria": "", "czyGotowka": null} i nie dodawaj żadnych innych znaków, znaczników Markdown ani wyjaśnień.
        """.trimIndent()

        val model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
            systemInstruction = content { text(systemInstruction) }
        )

        var attempt = 0
        while (true) {
            try {
                val response = model.generateContent(content { image(bitmap) })
                val jsonText = response.text ?: throw ScannerException.InvalidImage
                
                com.smartfinanse.utils.FileLogger.logError("ReceiptParser", "Gemini response JSON: $jsonText")
                
                return gson.fromJson(jsonText, ParsedReceipt::class.java)
            } catch (e: Exception) {
                com.smartfinanse.utils.FileLogger.logError("ReceiptParser", "Błąd analizy danych z paragonu", e)
                e.printStackTrace()
                
                val errorMessage = e.message?.lowercase() ?: ""
                val isNetworkError = e is java.net.UnknownHostException || e is java.net.SocketTimeoutException || errorMessage.contains("network")
                val isServerError = e.javaClass.simpleName.contains("ServerException") || errorMessage.contains("503") || errorMessage.contains("busy") || errorMessage.contains("unavailable")
                
                if (isServerError) {
                    if (attempt < 1) { // 1 retry
                        attempt++
                        delay(1000)
                        continue
                    } else {
                        throw ScannerException.ServerBusy
                    }
                }
                
                if (isNetworkError) {
                    throw ScannerException.NetworkError
                }
                
                throw ScannerException.InvalidImage
            }
        }
    }
}
