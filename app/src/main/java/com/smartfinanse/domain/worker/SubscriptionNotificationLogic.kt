package com.smartfinanse.domain.worker

import com.smartfinanse.domain.model.BillingCycle
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SubscriptionNotificationLogic {

    fun shouldSendReminder(
        startDate: Long,
        billingCycle: BillingCycle,
        todayStartMillis: Long
    ): Int? {
        val nextPayment = calculateNextPaymentDate(startDate, billingCycle, todayStartMillis)
        val diffMillis = nextPayment - todayStartMillis
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()

        return if (diffDays == 7 || diffDays == 1) {
            diffDays
        } else {
            null
        }
    }

    fun calculateNextPaymentDate(
        startDate: Long,
        billingCycle: BillingCycle,
        currentStartMillis: Long
    ): Long {
        val calendar = Calendar.getInstance()
        val currentCal = Calendar.getInstance()
        currentCal.timeInMillis = currentStartMillis

        calendar.timeInMillis = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.after(currentCal)) {
            return calendar.timeInMillis
        }

        while (!calendar.after(currentCal)) {
            when (billingCycle) {
                BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                BillingCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }
}
