package com.smartfinanse.data.local.mapper

import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconName = iconName,
    isExpense = isExpense
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    isExpense = isExpense
)
