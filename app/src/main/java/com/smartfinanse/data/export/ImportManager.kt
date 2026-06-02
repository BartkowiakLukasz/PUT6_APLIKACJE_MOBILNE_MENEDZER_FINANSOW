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

    fun parseJsonBackup(uri: Uri): BackupData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            
            gson.fromJson(jsonString, BackupData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseCsv(uri: Uri): List<CsvTransaction>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            val lines = reader.readLines()
            reader.close()
            
            if (lines.isEmpty()) return null
            
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
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class CsvTransaction(
    val dateStr: String,
    val categoryName: String,
    val amountStr: String,
    val description: String,
    val isCash: Boolean
)
