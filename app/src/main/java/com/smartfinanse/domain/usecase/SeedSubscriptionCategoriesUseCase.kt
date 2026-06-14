package com.smartfinanse.domain.usecase

import com.smartfinanse.data.local.DefaultCategories
import com.smartfinanse.data.local.dao.SubscriptionCategoryDao
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SeedSubscriptionCategoriesUseCase @Inject constructor(
    private val subscriptionCategoryDao: SubscriptionCategoryDao
) {
    suspend operator fun invoke() {
        val current = subscriptionCategoryDao.getAllCategories().firstOrNull()
        if (current.isNullOrEmpty()) {
            subscriptionCategoryDao.insertCategories(DefaultCategories.subscriptionCategories())
        } else {
            current.forEach { category ->
                if (category.iconId == null) {
                    val defaultCat = DefaultCategories.subscriptionCategories().find { it.name == category.name }
                    if (defaultCat != null) {
                        subscriptionCategoryDao.updateCategory(category.copy(iconId = defaultCat.iconId))
                    }
                }
            }
        }
    }
}
