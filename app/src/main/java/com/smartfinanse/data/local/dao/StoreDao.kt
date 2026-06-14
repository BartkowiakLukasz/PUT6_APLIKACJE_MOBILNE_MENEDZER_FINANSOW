package com.smartfinanse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartfinanse.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity): Long

    @androidx.room.Update
    suspend fun updateStore(store: StoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreEntity>)

    @Query("DELETE FROM stores WHERE id = :storeId")
    suspend fun deleteStore(storeId: Long)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()

    @Query("SELECT * FROM stores ORDER BY name ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Query("""
        SELECT s.* FROM stores s
        LEFT JOIN transactions t ON t.storeId = s.id
        GROUP BY s.id
        ORDER BY MAX(t.date) DESC, s.name ASC
        LIMIT 4
    """)
    fun getRecentStores(): Flow<List<StoreEntity>>
}
