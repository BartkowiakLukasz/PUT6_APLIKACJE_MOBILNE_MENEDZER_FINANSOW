package com.smartfinanse.data.scanner

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.smartfinanse.BuildConfig

data class ParsedReceipt(
    val sklep: String,
    val kwota: Double,
    val data: String?,
    val kategoria: String
)

class ReceiptParserAi {

    private val gson = Gson()

    suspend fun parseReceiptText(rawText: String, availableCategories: List<String>): ParsedReceipt {
        val categoriesStr = availableCategories.joinToString(", ")
        
        val systemInstruction = """
            Jesteś ekspertem finansowym. Otrzymasz surowy tekst zeskandowany z paragonu. Twoim zadaniem jest wyciągnięcie z niego 4 informacji:
            'sklep' - nazwa sprzedawcy (np. Biedronka, Orlen).
            'kwota' - ostateczna kwota do zapłaty (tylko liczba, kropka jako separator dziesiętny). Zignoruj podatek VAT, resztę, czy podsumowania przed zniżkami. Szukaj 'Suma', 'Do zapłaty', 'Zapłacono'.
            'data' - data zakupu w formacie YYYY-MM-DD. Jeśli nie potrafisz jej znaleźć, zwróć null.
            'kategoria' - przypisz zakup do jednej z kategorii: [$categoriesStr]. Wybierz najbardziej pasującą lub 'Inne' jeśli nie pasuje żadna.
            Zwróć WYŁĄCZNIE poprawny JSON o strukturze: {"sklep": "", "kwota": 0.0, "data": "", "kategoria": ""} i nie dodawaj żadnych innych znaków, znaczników Markdown ani wyjaśnień.
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
            val response = model.generateContent(rawText)
            val jsonText = response.text ?: throw AiParsingException("Pusta odpowiedź od modelu")
            
            gson.fromJson(jsonText, ParsedReceipt::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            throw AiParsingException("Nie udało się przeanalizować danych z paragonu. Upewnij się, że zdjęcie jest wyraźne.", e)
        }
    }
}

class AiParsingException(message: String, cause: Throwable? = null) : Exception(message, cause)
