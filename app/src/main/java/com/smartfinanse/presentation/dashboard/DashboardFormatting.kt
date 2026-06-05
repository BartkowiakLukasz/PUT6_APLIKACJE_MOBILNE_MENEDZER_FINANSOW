package com.smartfinanse.presentation.dashboard

import com.smartfinanse.presentation.common.MoneyFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatBalance(amountGrosze: Long): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(kotlin.math.abs(zloty))
    val sign = when {
        amountGrosze > 0 -> "+"
        amountGrosze < 0 -> "−"
        else -> ""
    }
    return "$sign$formatted ${MoneyFormatter.currentCurrencySymbol}"
}

fun formatPlainAmount(amountGrosze: Long): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(zloty)
    return "$formatted ${MoneyFormatter.currentCurrencySymbol}"
}

fun formatChartCenterAmount(amountGrosze: Long, isIncome: Boolean): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(zloty)
    val sign = when {
        isIncome -> "+"
        amountGrosze > 0 -> "−"
        else -> ""
    }
    return "$sign$formatted ${MoneyFormatter.currentCurrencySymbol}"
}

fun formatCompactAmount(amountGrosze: Long): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }.format(zloty)
    return "$formatted zł"
}

fun formatRange(start: Long?, end: Long?): String {
    if (start == null || end == null) return "Zakres"
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    return "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
}

fun periodDisplayLabel(filter: TimeFilter, customStart: Long?, customEnd: Long?): String {
    return when (filter) {
        TimeFilter.CUSTOM -> formatRange(customStart, customEnd)
        else -> filter.label
    }
}

fun periodLabelForSection(filter: TimeFilter, customStart: Long?, customEnd: Long?): String {
    return when (filter) {
        TimeFilter.CUSTOM -> formatRange(customStart, customEnd)
        else -> filter.label.lowercase(Locale("pl", "PL"))
    }
}
