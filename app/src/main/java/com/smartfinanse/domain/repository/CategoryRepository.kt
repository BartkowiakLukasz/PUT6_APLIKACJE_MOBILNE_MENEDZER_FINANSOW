package com.smartfinanse.domain.repository

import com.smartfinanse.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(isExpense: Boolean): Flow<List<Category>>
    suspend fun seedCategoriesIfEmpty()
    suspend fun addCategory(category: Category): Long
}
