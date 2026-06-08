package com.smartfinanse.data.repository

import com.smartfinanse.data.local.dao.SubscriptionDao
import com.smartfinanse.data.local.entity.SubscriptionEntity
import com.smartfinanse.domain.model.BillingCycle
import com.smartfinanse.domain.model.Subscription
import com.smartfinanse.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<Subscription>> =
        dao.getAllSubscriptionsWithCategory().map { list ->
            list.map { it ->
                Subscription(
                    id = it.id,
                    serviceName = it.serviceName,
                    amount = it.amount,
                    startDate = it.startDate,
                    billingCycle = BillingCycle.valueOf(it.billingCycle),
                    categoryId = it.categoryId,
                    categoryName = it.categoryName,
                    categoryColor = it.categoryColor,
                    categoryIconId = it.categoryIconId
                )
            }
        }

    override suspend fun getAllSubscriptionsSync(): List<Subscription> =
        dao.getAllSubscriptionsWithCategorySync().map { it ->
            Subscription(
                id = it.id,
                serviceName = it.serviceName,
                amount = it.amount,
                startDate = it.startDate,
                billingCycle = BillingCycle.valueOf(it.billingCycle),
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                categoryColor = it.categoryColor,
                categoryIconId = it.categoryIconId
            )
        }

    override suspend fun getSubscriptionById(id: Long): Subscription? =
        dao.getSubscriptionById(id)?.let { entity ->
            Subscription(
                id = entity.id,
                serviceName = entity.serviceName,
                amount = entity.amount,
                startDate = entity.startDate,
                billingCycle = BillingCycle.valueOf(entity.billingCycle),
                categoryId = entity.categoryId
            )
        }

    override suspend fun insertSubscription(subscription: Subscription): Long =
        dao.insertSubscription(
            SubscriptionEntity(
                id = subscription.id,
                serviceName = subscription.serviceName,
                amount = subscription.amount,
                startDate = subscription.startDate,
                billingCycle = subscription.billingCycle.name,
                categoryId = subscription.categoryId
            )
        )

    override suspend fun updateSubscription(subscription: Subscription) {
        dao.updateSubscription(
            SubscriptionEntity(
                id = subscription.id,
                serviceName = subscription.serviceName,
                amount = subscription.amount,
                startDate = subscription.startDate,
                billingCycle = subscription.billingCycle.name,
                categoryId = subscription.categoryId
            )
        )
    }

    override suspend fun deleteSubscription(id: Long) {
        dao.getSubscriptionById(id)?.let {
            dao.deleteSubscription(it)
        }
    }
}
