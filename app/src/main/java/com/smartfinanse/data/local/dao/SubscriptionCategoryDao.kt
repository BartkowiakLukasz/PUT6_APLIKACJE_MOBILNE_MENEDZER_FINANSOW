package com.smartfinanse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartfinanse.data.local.entity.SubscriptionCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionCategoryDao {
    @Query("SELECT * FROM subscription_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<SubscriptionCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: SubscriptionCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<SubscriptionCategoryEntity>)

    @Update
    suspend fun updateCategory(category: SubscriptionCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: SubscriptionCategoryEntity)

    @Query("DELETE FROM subscription_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM subscription_categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): SubscriptionCategoryEntity?
}
