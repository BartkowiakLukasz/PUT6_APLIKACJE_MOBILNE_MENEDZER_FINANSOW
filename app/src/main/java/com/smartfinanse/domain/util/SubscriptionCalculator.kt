package com.smartfinanse.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

object SubscriptionCalculator {
    fun calculateNextRenewal(
        startDateMillis: Long,
        interval: Period,
        todayMillis: Long = System.currentTimeMillis()
    ): Long {
        val zoneId = ZoneId.systemDefault()
        val startLocalDate = Instant.ofEpochMilli(startDateMillis).atZone(zoneId).toLocalDate()
        val todayLocalDate = Instant.ofEpochMilli(todayMillis).atZone(zoneId).toLocalDate()

        if (startLocalDate.isAfter(todayLocalDate)) {
            return startLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        }

        var multiplier = 1
        var nextRenewal = startLocalDate.plus(interval.multipliedBy(multiplier))

        while (nextRenewal.isBefore(todayLocalDate) || nextRenewal.isEqual(todayLocalDate)) {
            multiplier++
            nextRenewal = startLocalDate.plus(interval.multipliedBy(multiplier))
        }

        return nextRenewal.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }
}
