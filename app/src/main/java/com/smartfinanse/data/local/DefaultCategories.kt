package com.smartfinanse.data.local

import com.smartfinanse.data.local.entity.CategoryEntity

object DefaultCategories {

    fun expenseCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Artykuły spożywcze", iconName = "ic_food", isExpense = true, colorHex = "#E91E63"),
        CategoryEntity(name = "Zdrowie", iconName = "ic_health", isExpense = true, colorHex = "#4CAF50"),
        CategoryEntity(name = "Rachunki", iconName = "ic_bills", isExpense = true, colorHex = "#2196F3"),
        CategoryEntity(name = "Inne", iconName = "ic_other", isExpense = true, colorHex = "#FF9800"),
        CategoryEntity(name = "Transport", iconName = "ic_transport", isExpense = true, colorHex = "#9C27B0"),
        CategoryEntity(name = "Rozrywka", iconName = "ic_entertainment", isExpense = true, colorHex = "#FFEB3B")
    )

    fun incomeCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Wynagrodzenie", iconName = "ic_work", isExpense = false, colorHex = "#1B5E20"),
        CategoryEntity(name = "Premia", iconName = "ic_star", isExpense = false, colorHex = "#388E3C"),
        CategoryEntity(name = "Zwrot", iconName = "ic_other", isExpense = false, colorHex = "#009688"),
        CategoryEntity(name = "Inne przychody", iconName = "ic_other", isExpense = false, colorHex = "#607D8B")
    )

    fun all(): List<CategoryEntity> = expenseCategories() + incomeCategories()
}
