package com.smartfinanse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartfinanse.data.local.dao.CategoryDao
import com.smartfinanse.data.local.dao.StoreDao
import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.StoreEntity
import com.smartfinanse.data.local.entity.TransactionEntity

@Database(
    entities = [
        CategoryEntity::class,
        StoreEntity::class,
        TransactionEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SmartFinanseDatabase : RoomDatabase() {

    abstract val categoryDao: CategoryDao
    abstract val storeDao: StoreDao
    abstract val transactionDao: TransactionDao

    companion object {
        const val DATABASE_NAME = "smart_finanse_db"
    }
}
