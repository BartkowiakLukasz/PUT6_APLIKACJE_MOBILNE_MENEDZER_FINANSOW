package com.smartfinanse.presentation.subscriptions

import com.smartfinanse.domain.usecase.SubscriptionItem

data class SubscriptionsUiState(
    val subscriptions: List<SubscriptionItemUi> = emptyList(),
    val totalMonthlyCostFormatted: String = "0,00 zł",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class SubscriptionItemUi(
    val id: Long,
    val title: String,
    val categoryName: String?,
    val categoryId: Long?,
    val amountFormatted: String,
    val nextRenewalDateFormatted: String,
    val daysUntilRenewal: Int,
    val isExpense: Boolean
)
