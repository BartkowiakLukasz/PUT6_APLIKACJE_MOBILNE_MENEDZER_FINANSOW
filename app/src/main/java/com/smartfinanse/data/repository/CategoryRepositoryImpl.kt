package com.smartfinanse.data.repository

import com.smartfinanse.data.local.DefaultCategories
import com.smartfinanse.data.local.dao.CategoryDao
import com.smartfinanse.data.local.mapper.toDomain
import com.smartfinanse.data.local.mapper.toEntity
import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategories(isExpense: Boolean): Flow<List<Category>> =
        categoryDao.getByExpenseType(isExpense).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun seedCategoriesIfEmpty() {
        if (categoryDao.count() == 0) {
            categoryDao.insertAll(DefaultCategories.all())
        }
    }

    override suspend fun seedIncomeCategoriesIfMissing() {
        if (categoryDao.countIncome() == 0) {
            categoryDao.insertAll(DefaultCategories.incomeCategories())
        }
    }

    override suspend fun addCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }
}
