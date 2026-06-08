package com.smartfinanse.domain.repository

import com.smartfinanse.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun insertStore(store: Store): Long
    suspend fun updateStore(store: Store)
    fun getAllStores(): Flow<List<Store>>
    fun getRecentStores(): Flow<List<Store>>
    suspend fun deleteStore(storeId: Long)
    suspend fun seedStoresIfEmpty()
}
