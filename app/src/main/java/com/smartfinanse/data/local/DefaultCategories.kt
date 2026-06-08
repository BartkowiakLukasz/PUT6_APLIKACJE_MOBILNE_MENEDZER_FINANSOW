package com.smartfinanse.data.local

import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.SubscriptionCategoryEntity
import android.graphics.Color

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

    fun subscriptionCategories(): List<SubscriptionCategoryEntity> = listOf(
        SubscriptionCategoryEntity(name = "VOD i Streaming", iconId = "ic_entertainment", color = Color.parseColor("#E50914").toLong()),
        SubscriptionCategoryEntity(name = "Muzyka i Podcasty", iconId = "ic_music", color = Color.parseColor("#1DB954").toLong()),
        SubscriptionCategoryEntity(name = "Oprogramowanie i Chmura", iconId = "ic_work", color = Color.parseColor("#0078D7").toLong()),
        SubscriptionCategoryEntity(name = "Telekomunikacja", iconId = "ic_phone", color = Color.parseColor("#FF9800").toLong()),
        SubscriptionCategoryEntity(name = "Zdrowie i Sport", iconId = "ic_health", color = Color.parseColor("#E91E63").toLong()),
        SubscriptionCategoryEntity(name = "Gry i Rozrywka", iconId = "ic_game", color = Color.parseColor("#9C27B0").toLong()),
        SubscriptionCategoryEntity(name = "Inne", iconId = "ic_other", color = Color.parseColor("#607D8B").toLong())
    )
}
