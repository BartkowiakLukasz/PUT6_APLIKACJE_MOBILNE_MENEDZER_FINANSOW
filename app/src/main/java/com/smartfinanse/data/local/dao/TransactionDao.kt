package com.smartfinanse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smartfinanse.data.local.entity.TransactionEntity
import com.smartfinanse.data.local.entity.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllWithDetails(): Flow<List<TransactionWithDetails>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<TransactionWithDetails>>

    @Query("""
        SELECT categories.name AS categoryName, categories.iconName AS categoryIcon, SUM(transactions.amount) AS totalAmount 
        FROM transactions 
        INNER JOIN categories ON transactions.categoryId = categories.id 
        WHERE transactions.date BETWEEN :startDate AND :endDate AND categories.isExpense = 1
        GROUP BY transactions.categoryId
    """)
    fun getCategoryTotalsBetweenDates(startDate: Long, endDate: Long): Flow<List<CategoryTotal>>
}

data class CategoryTotal(
    val categoryName: String,
    val categoryIcon: String,
    val totalAmount: Long
)
