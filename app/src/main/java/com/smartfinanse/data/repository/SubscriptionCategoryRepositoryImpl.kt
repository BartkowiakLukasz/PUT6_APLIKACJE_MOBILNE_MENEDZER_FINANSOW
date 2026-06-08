package com.smartfinanse.data.repository

import com.smartfinanse.data.local.dao.SubscriptionCategoryDao
import com.smartfinanse.data.local.entity.SubscriptionCategoryEntity
import com.smartfinanse.domain.model.SubscriptionCategory
import com.smartfinanse.domain.repository.SubscriptionCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.smartfinanse.data.local.DefaultCategories

class SubscriptionCategoryRepositoryImpl @Inject constructor(
    private val dao: SubscriptionCategoryDao
) : SubscriptionCategoryRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val current = dao.getAllCategories().firstOrNull()
            if (current.isNullOrEmpty()) {
                dao.insertCategories(DefaultCategories.subscriptionCategories())
            }
        }
    }

    override fun getAllCategories(): Flow<List<SubscriptionCategory>> =
        dao.getAllCategories().map { list ->
            list.map { entity ->
                SubscriptionCategory(
                    id = entity.id,
                    name = entity.name,
                    color = entity.color,
                    iconId = entity.iconId
                )
            }
        }

    override suspend fun getCategoryById(id: Long): SubscriptionCategory? =
        dao.getCategoryById(id)?.let { entity ->
            SubscriptionCategory(
                id = entity.id,
                name = entity.name,
                color = entity.color,
                iconId = entity.iconId
            )
        }

    override suspend fun insertCategory(category: SubscriptionCategory): Long =
        dao.insertCategory(
            SubscriptionCategoryEntity(
                id = category.id,
                name = category.name,
                color = category.color,
                iconId = category.iconId
            )
        )

    override suspend fun insertCategories(categories: List<SubscriptionCategory>) {
        dao.insertCategories(categories.map {
            SubscriptionCategoryEntity(
                id = it.id,
                name = it.name,
                color = it.color,
                iconId = it.iconId
            )
        })
    }

    override suspend fun deleteCategory(id: Long) {
        dao.getCategoryById(id)?.let {
            dao.deleteCategory(it)
        }
    }
}
