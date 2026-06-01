package com.smartfinanse.presentation.transaction.list

import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.presentation.common.DateFormatter
import com.smartfinanse.presentation.common.MoneyFormatter

fun TransactionWithCategory.toUi(): TransactionItemUi {
    val isExpense = category?.isExpense ?: true
    return TransactionItemUi(
        id = transaction.id,
        title = transaction.description,
        categoryName = category?.name,
        amountFormatted = MoneyFormatter.format(transaction.amount, isExpense),
        dateFormatted = DateFormatter.format(transaction.date),
        isCash = transaction.isCash
    )
}
