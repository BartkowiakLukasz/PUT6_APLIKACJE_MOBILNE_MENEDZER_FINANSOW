package com.smartfinanse.data.export

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.TransactionEntity
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportManager(private val context: Context) {

    private val gson = Gson()

    fun parseJsonBackup(uri: Uri): BackupData {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Nie można otworzyć pliku")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.readText()
        reader.close()
        
        return gson.fromJson(jsonString, BackupData::class.java)
    }

    fun parseCsv(uri: Uri): List<CsvTransaction> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Nie można otworzyć pliku")
        val reader = BufferedReader(InputStreamReader(inputStream))
        
        val lines = reader.readLines()
        reader.close()
        
        if (lines.isEmpty()) throw IllegalArgumentException("Plik CSV jest pusty")
        
        val result = mutableListOf<CsvTransaction>()
        // Pomiń nagłówek (pierwsza linia)
        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.isBlank()) continue
            
            val parts = line.split(",")
            if (parts.size >= 6) {
                val dateStr = parts[1]
                val catName = parts[2]
                val amountStr = parts[3]
                val desc = parts[4]
                val isCash = parts[5].toBoolean()
                
                result.add(CsvTransaction(dateStr, catName, amountStr, desc, isCash))
            } else {
                throw IllegalArgumentException("Nieprawidłowy format pliku CSV w linii ${i + 1}")
            }
        }
        return result
    }
}

data class CsvTransaction(
    val dateStr: String,
    val categoryName: String,
    val amountStr: String,
    val description: String,
    val isCash: Boolean
)
