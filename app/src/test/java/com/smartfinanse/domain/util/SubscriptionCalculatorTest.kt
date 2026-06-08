package com.smartfinanse.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

class SubscriptionCalculatorTest {

    private fun localDateToMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Test
    fun testNextRenewal_monthly_sameMonth() {
        // startDate = 2026-01-31, interval = 1 miesiąc, today = 2026-02-15 -> wynik: 2026-02-28
        val start = localDateToMillis(LocalDate.of(2026, 1, 31))
        val today = localDateToMillis(LocalDate.of(2026, 2, 15))
        val interval = Period.ofMonths(1)

        val nextRenewal = SubscriptionCalculator.calculateNextRenewal(start, interval, today)

        assertEquals(localDateToMillis(LocalDate.of(2026, 2, 28)), nextRenewal)
    }

    @Test
    fun testNextRenewal_monthly_maintainingDayOfMonth() {
        // startDate = 2026-01-31, interval = 1 miesiąc, today = 2026-03-01 -> wynik: 2026-03-31
        val start = localDateToMillis(LocalDate.of(2026, 1, 31))
        val today = localDateToMillis(LocalDate.of(2026, 3, 1))
        val interval = Period.ofMonths(1)

        val nextRenewal = SubscriptionCalculator.calculateNextRenewal(start, interval, today)

        assertEquals(localDateToMillis(LocalDate.of(2026, 3, 31)), nextRenewal)
    }

    @Test
    fun testNextRenewal_weekly() {
        // startDate = 2026-06-10, interval = 1 tydzień (Period.ofDays(7)), today = 2026-06-11 -> wynik: 2026-06-17
        val start = localDateToMillis(LocalDate.of(2026, 6, 10))
        val today = localDateToMillis(LocalDate.of(2026, 6, 11))
        val interval = Period.ofDays(7)

        val nextRenewal = SubscriptionCalculator.calculateNextRenewal(start, interval, today)

        assertEquals(localDateToMillis(LocalDate.of(2026, 6, 17)), nextRenewal)
    }
}
