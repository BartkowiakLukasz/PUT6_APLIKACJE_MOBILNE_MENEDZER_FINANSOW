package com.smartfinanse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartfinanse.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Query("SELECT * FROM categories WHERE isExpense = :isExpense ORDER BY name ASC")
    fun getByExpenseType(isExpense: Boolean): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @androidx.room.Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesRaw(): List<CategoryEntity>

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}
