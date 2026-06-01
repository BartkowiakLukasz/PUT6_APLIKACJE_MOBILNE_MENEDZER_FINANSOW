package com.smartfinanse.data.local

import com.smartfinanse.data.local.entity.CategoryEntity

object DefaultCategories {

    fun expenseCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Jedzenie", iconName = "ic_food", isExpense = true),
        CategoryEntity(name = "Transport", iconName = "ic_transport", isExpense = true),
        CategoryEntity(name = "Rachunki", iconName = "ic_bills", isExpense = true),
        CategoryEntity(name = "Zdrowie", iconName = "ic_health", isExpense = true),
        CategoryEntity(name = "Rozrywka", iconName = "ic_entertainment", isExpense = true),
        CategoryEntity(name = "Inne", iconName = "ic_other", isExpense = true)
    )

    fun incomeCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Pensja", iconName = "ic_salary", isExpense = false),
        CategoryEntity(name = "Zwrot", iconName = "ic_refund", isExpense = false),
        CategoryEntity(name = "Inne", iconName = "ic_other", isExpense = false)
    )

    fun all(): List<CategoryEntity> = expenseCategories() + incomeCategories()
}
