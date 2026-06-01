package com.smartfinanse.data.local

import com.smartfinanse.data.local.entity.CategoryEntity

object DefaultCategories {

    fun expenseCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Artykuły spożywcze", iconName = "ic_food", isExpense = true, colorHex = "#E91E63"), // Różowy
        CategoryEntity(name = "Zdrowie", iconName = "ic_health", isExpense = true, colorHex = "#4CAF50"), // Zielony
        CategoryEntity(name = "Rachunki", iconName = "ic_bills", isExpense = true, colorHex = "#2196F3"), // Niebieski
        CategoryEntity(name = "Inne", iconName = "ic_other", isExpense = true, colorHex = "#FF9800"), // Pomarańczowy
        CategoryEntity(name = "Transport", iconName = "ic_transport", isExpense = true, colorHex = "#9C27B0"),
        CategoryEntity(name = "Rozrywka", iconName = "ic_entertainment", isExpense = true, colorHex = "#FFEB3B")
    )

    fun incomeCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Pensja", iconName = "ic_salary", isExpense = false, colorHex = "#4CAF50"),
        CategoryEntity(name = "Zwrot", iconName = "ic_refund", isExpense = false, colorHex = "#00BCD4"),
        CategoryEntity(name = "Inne", iconName = "ic_other", isExpense = false, colorHex = "#FF9800")
    )

    fun all(): List<CategoryEntity> = expenseCategories() + incomeCategories()
}
