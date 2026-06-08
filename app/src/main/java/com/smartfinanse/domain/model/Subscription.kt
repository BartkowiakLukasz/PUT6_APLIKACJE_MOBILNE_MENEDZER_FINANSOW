package com.smartfinanse.domain.model

data class Subscription(
    val id: Long = 0,
    val serviceName: String,
    val amount: Long,
    val startDate: Long,
    val billingCycle: BillingCycle,
    val categoryId: Long?,
    val categoryName: String? = null,
    val categoryColor: Long? = null,
    val categoryIconId: String? = null
)

enum class BillingCycle {
    MONTHLY, YEARLY
}
