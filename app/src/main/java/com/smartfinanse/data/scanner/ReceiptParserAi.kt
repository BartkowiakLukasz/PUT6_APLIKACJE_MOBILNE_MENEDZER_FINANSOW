package com.smartfinanse.data.scanner

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.smartfinanse.BuildConfig

import android.graphics.Bitmap

data class ParsedReceipt(
    val sklep: String,
    val kwota: Double,
    val data: String?,
    val kategoria: String,
    val czyGotowka: Boolean?
)

class ReceiptParserAi {

    private val gson = Gson()

    suspend fun parseReceiptImage(bitmap: Bitmap, availableCategories: List<String>): ParsedReceipt {
        val categoriesStr = availableCategories.joinToString(", ")
        
        val systemInstruction = """
            Jesteś ekspertem finansowym. Otrzymasz zdjęcie paragonu. Twoim zadaniem jest wyciągnięcie z niego 4 informacji:
            'sklep' - nazwa sprzedawcy (np. Biedronka, Orlen).
            'kwota' - ostateczna kwota do zapłaty (tylko liczba, kropka jako separator dziesiętny). Zignoruj podatek VAT, resztę, czy podsumowania przed zniżkami. Szukaj 'Suma', 'Do zapłaty', 'Zapłacono'.
            'data' - data zakupu w formacie YYYY-MM-DD. Jeśli nie potrafisz jej znaleźć, zwróć null.
            'kategoria' - przypisz zakup do jednej z kategorii: [$categoriesStr]. Wybierz najbardziej pasującą lub 'Inne' jeśli nie pasuje żadna.
            'czyGotowka' - true jeśli płatność była gotówką, false jeśli płatność była kartą/blikiem/telefonem. Jeśli nie masz pewności, zwróć null.
            Zwróć WYŁĄCZNIE poprawny JSON o strukturze: {"sklep": "", "kwota": 0.0, "data": "", "kategoria": "", "czyGotowka": null} i nie dodawaj żadnych innych znaków, znaczników Markdown ani wyjaśnień.
        """.trimIndent()

        val model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
            systemInstruction = content { text(systemInstruction) }
        )

        return try {
            val response = model.generateContent(content { image(bitmap) })
            val jsonText = response.text ?: throw AiParsingException("Pusta odpowiedź od modelu")
            
            com.smartfinanse.utils.FileLogger.logError("ReceiptParser", "Gemini response JSON: $jsonText")
            
            gson.fromJson(jsonText, ParsedReceipt::class.java)
        } catch (e: Exception) {
            com.smartfinanse.utils.FileLogger.logError("ReceiptParser", "Błąd analizy danych z paragonu", e)
            e.printStackTrace()
            throw AiParsingException("Nie udało się przeanalizować danych ze zdjęcia. Upewnij się, że paragon jest wyraźny.", e)
        }
    }
}

class AiParsingException(message: String, cause: Throwable? = null) : Exception(message, cause)
