package com.smartfinanse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartfinanse.data.local.dao.CategoryDao
import com.smartfinanse.data.local.dao.StoreDao
import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.StoreEntity
import com.smartfinanse.data.local.entity.TransactionEntity
import com.smartfinanse.data.local.entity.SubscriptionEntity
import com.smartfinanse.data.local.entity.SubscriptionCategoryEntity
import com.smartfinanse.data.local.dao.SubscriptionDao
import com.smartfinanse.data.local.dao.SubscriptionCategoryDao

@Database(
    entities = [
        CategoryEntity::class,
        StoreEntity::class,
        TransactionEntity::class,
        SubscriptionEntity::class,
        SubscriptionCategoryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class SmartFinanseDatabase : RoomDatabase() {

    abstract val categoryDao: CategoryDao
    abstract val storeDao: StoreDao
    abstract val transactionDao: TransactionDao
    abstract val subscriptionDao: SubscriptionDao
    abstract val subscriptionCategoryDao: SubscriptionCategoryDao

    companion object {
        const val DATABASE_NAME = "smart_finanse_db"
    }
}
