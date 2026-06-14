package com.smartfinanse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.smartfinanse.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

data class SubscriptionWithCategory(
    val id: Long,
    val serviceName: String,
    val amount: Long,
    val startDate: Long,
    val billingCycle: String,
    val categoryId: Long?,
    val categoryName: String?,
    val categoryColor: Long?,
    val categoryIconId: String?
)

@Dao
interface SubscriptionDao {
    @Transaction
    @Query("""
        SELECT s.id, s.serviceName, s.amount, s.startDate, s.billingCycle, s.categoryId,
               c.name as categoryName, c.color as categoryColor, c.iconId as categoryIconId
        FROM subscriptions s
        LEFT JOIN subscription_categories c ON s.categoryId = c.id
        ORDER BY s.id DESC
    """)
    fun getAllSubscriptionsWithCategory(): Flow<List<SubscriptionWithCategory>>

    @Transaction
    @Query("""
        SELECT s.id, s.serviceName, s.amount, s.startDate, s.billingCycle, s.categoryId,
               c.name as categoryName, c.color as categoryColor, c.iconId as categoryIconId
        FROM subscriptions s
        LEFT JOIN subscription_categories c ON s.categoryId = c.id
    """)
    suspend fun getAllSubscriptionsWithCategorySync(): List<SubscriptionWithCategory>

    @Query("SELECT * FROM subscriptions WHERE id = :id LIMIT 1")
    suspend fun getSubscriptionById(id: Long): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()
}
