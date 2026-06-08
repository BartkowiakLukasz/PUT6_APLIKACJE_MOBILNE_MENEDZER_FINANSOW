package com.smartfinanse.domain.repository

import com.smartfinanse.domain.model.SubscriptionCategory
import kotlinx.coroutines.flow.Flow

interface SubscriptionCategoryRepository {
    fun getAllCategories(): Flow<List<SubscriptionCategory>>
    suspend fun getCategoryById(id: Long): SubscriptionCategory?
    suspend fun insertCategory(category: SubscriptionCategory): Long
    suspend fun insertCategories(categories: List<SubscriptionCategory>)
    suspend fun deleteCategory(id: Long)
}
