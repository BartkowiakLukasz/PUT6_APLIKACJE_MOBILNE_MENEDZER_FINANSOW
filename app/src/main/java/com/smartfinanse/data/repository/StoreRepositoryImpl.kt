package com.smartfinanse.data.repository

import com.smartfinanse.data.local.dao.StoreDao
import com.smartfinanse.data.local.mapper.toDomain
import com.smartfinanse.data.local.mapper.toEntity
import com.smartfinanse.domain.model.Store
import com.smartfinanse.data.local.entity.StoreEntity
import com.smartfinanse.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val storeDao: StoreDao
) : StoreRepository {

    override suspend fun insertStore(store: Store): Long {
        return storeDao.insertStore(store.toEntity())
    }

    override suspend fun updateStore(store: Store) {
        storeDao.updateStore(store.toEntity())
    }

    override fun getAllStores(): Flow<List<Store>> {
        return storeDao.getAllStores().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecentStores(): Flow<List<Store>> {
        return storeDao.getRecentStores().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun deleteStore(storeId: Long) {
        storeDao.deleteStore(storeId)
    }

    override suspend fun seedStoresIfEmpty() {
        val existing = storeDao.getAllStores().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                StoreEntity(name = "lidl", iconName = "lidl_logo"),
                StoreEntity(name = "biedronka", iconName = "biedronka"),
                StoreEntity(name = "dino", iconName = "dinosvg")
            )
            storeDao.insertStores(defaults)
        }
    }
}
