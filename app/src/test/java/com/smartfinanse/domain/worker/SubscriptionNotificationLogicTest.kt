package com.smartfinanse.domain.worker

import com.smartfinanse.domain.model.BillingCycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class SubscriptionNotificationLogicTest {

    private fun buildTime(year: Int, month: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1) // Calendar months are 0-indexed
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun `shouldSendReminder returns 7 when exactly 7 days before next payment`() {
        val startDate = buildTime(2023, 10, 1) // 1 Oct 2023
        val today = buildTime(2023, 10, 25)    // 25 Oct 2023
        // Next payment: 1 Nov 2023. Diff: 1 Nov - 25 Oct = 7 days

        val result = SubscriptionNotificationLogic.shouldSendReminder(
            startDate = startDate,
            billingCycle = BillingCycle.MONTHLY,
            todayStartMillis = today
        )
        
        assertEquals(7, result)
    }

    @Test
    fun `shouldSendReminder returns 1 when exactly 1 day before next payment`() {
        val startDate = buildTime(2023, 10, 1) // 1 Oct 2023
        val today = buildTime(2023, 10, 31)    // 31 Oct 2023
        // Next payment: 1 Nov 2023. Diff: 1 Nov - 31 Oct = 1 day

        val result = SubscriptionNotificationLogic.shouldSendReminder(
            startDate = startDate,
            billingCycle = BillingCycle.MONTHLY,
            todayStartMillis = today
        )
        
        assertEquals(1, result)
    }

    @Test
    fun `shouldSendReminder returns null when not 1 or 7 days before next payment`() {
        val startDate = buildTime(2023, 10, 1) // 1 Oct 2023
        val today = buildTime(2023, 10, 20)    // 20 Oct 2023 (12 days before 1 Nov)

        val result = SubscriptionNotificationLogic.shouldSendReminder(
            startDate = startDate,
            billingCycle = BillingCycle.MONTHLY,
            todayStartMillis = today
        )
        
        assertNull(result)
    }

    @Test
    fun `yearly cycle reminder test for 7 days`() {
        val startDate = buildTime(2022, 5, 10) // 10 May 2022
        val today = buildTime(2023, 5, 3)      // 3 May 2023
        // Next payment: 10 May 2023. Diff: 10 May - 3 May = 7 days

        val result = SubscriptionNotificationLogic.shouldSendReminder(
            startDate = startDate,
            billingCycle = BillingCycle.YEARLY,
            todayStartMillis = today
        )
        
        assertEquals(7, result)
    }
}
