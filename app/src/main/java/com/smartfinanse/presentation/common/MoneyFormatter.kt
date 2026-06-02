package com.smartfinanse.presentation.common

import java.text.NumberFormat
import java.util.Locale

object MoneyFormatter {

    private val polishLocale = Locale.forLanguageTag("pl-PL")

    var currentCurrencySymbol: String = "zł"

    fun format(amountGrosze: Long, isExpense: Boolean): String {
        val zloty = amountGrosze / 100.0
        val formatted = NumberFormat.getNumberInstance(polishLocale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(zloty)
        val sign = if (isExpense) "-" else "+"
        return "$sign$formatted $currentCurrencySymbol"
    }
}
