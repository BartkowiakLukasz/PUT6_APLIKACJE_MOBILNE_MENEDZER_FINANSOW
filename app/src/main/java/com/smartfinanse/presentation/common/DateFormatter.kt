package com.smartfinanse.presentation.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    private val polishLocale = Locale.forLanguageTag("pl-PL")
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", polishLocale)

    fun format(epochMillis: Long): String {
        val date = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return date.format(formatter)
    }
}
