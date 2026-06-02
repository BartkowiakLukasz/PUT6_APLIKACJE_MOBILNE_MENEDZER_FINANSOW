package com.smartfinanse.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.TransactionEntity
import com.smartfinanse.data.local.entity.TransactionWithDetails
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportManager(private val context: Context) {

    private val gson = Gson()
    private val authority = "${context.packageName}.fileprovider"

    fun exportToJson(categories: List<CategoryEntity>, transactions: List<TransactionEntity>): Uri? {
        val backupData = BackupData(categories, transactions)
        val jsonString = gson.toJson(backupData)

        return try {
            val file = File(context.cacheDir, "smart_finanse_backup_${System.currentTimeMillis()}.json")
            val writer = FileWriter(file)
            writer.write(jsonString)
            writer.close()
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportToCsv(transactions: List<TransactionWithDetails>): Uri? {
        return try {
            val file = File(context.cacheDir, "smart_finanse_export_${System.currentTimeMillis()}.csv")
            val writer = FileWriter(file)
            
            // W nagłówkach CSV używamy separatora przecinkowego
            writer.append("ID,Data,Kategoria,Kwota,Opis,Czy_Gotowka\n")
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            for (t in transactions) {
                val dateStr = dateFormat.format(Date(t.transaction.date))
                val catName = t.category?.name ?: "Brak"
                // Formatujemy kwotę (np. 12050 na 120.50)
                val amountStr = String.format(Locale.US, "%.2f", t.transaction.amount / 100.0)
                
                // Ubezpieczamy się przed przecinkami w opisie
                val safeDescription = t.transaction.description.replace(",", " ")
                
                writer.append("${t.transaction.id},")
                writer.append("$dateStr,")
                writer.append("$catName,")
                writer.append("$amountStr,")
                writer.append("$safeDescription,")
                writer.append("${t.transaction.isCash}\n")
            }
            
            writer.close()
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
