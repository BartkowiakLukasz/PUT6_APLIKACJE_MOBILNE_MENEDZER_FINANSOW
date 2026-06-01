package com.smartfinanse.domain.model

data class Category(
    val id: Long,
    val name: String,
    val iconName: String,
    val isExpense: Boolean,
    val colorHex: String
)
