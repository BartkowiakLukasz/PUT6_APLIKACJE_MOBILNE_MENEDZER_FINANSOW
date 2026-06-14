package com.smartfinanse.presentation.subscription.add

import com.smartfinanse.domain.model.SubscriptionCategory

data class AddSubscriptionUiState(
    val serviceName: String = "",
    val amount: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val isMonthly: Boolean = true,
    val selectedCategoryId: Long? = null,
    val categories: List<SubscriptionCategory> = emptyList(),
    val categorySearchQuery: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val nameError: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val globalError: String? = null
)
