package com.smartfinanse.presentation.transaction.list

import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.presentation.common.DateFormatter
import com.smartfinanse.presentation.common.MoneyFormatter
import com.smartfinanse.domain.util.capitalizeFirst

fun TransactionWithCategory.toUi(): TransactionItemUi {
    val isExpense = category?.isExpense ?: true
    return TransactionItemUi(
        id = transaction.id,
        title = transaction.description.capitalizeFirst(),
        categoryName = category?.name?.capitalizeFirst(),
        categoryId = category?.id,
        amountFormatted = MoneyFormatter.format(transaction.amount, isExpense),
        dateFormatted = DateFormatter.format(transaction.date),
        isCash = transaction.isCash,
        isExpense = isExpense,
        isRecurring = transaction.isRecurring
    )
}
