package com.smartfinanse.data.local.mapper

import com.smartfinanse.data.local.entity.StoreEntity
import com.smartfinanse.domain.model.Store

fun StoreEntity.toDomain(): Store = Store(
    id = id,
    name = name,
    iconName = iconName
)

fun Store.toEntity(): StoreEntity = StoreEntity(
    id = id,
    name = name,
    iconName = iconName
)
