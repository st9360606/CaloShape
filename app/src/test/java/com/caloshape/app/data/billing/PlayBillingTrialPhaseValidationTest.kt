package com.caloshape.app.data.billing

import com.android.billingclient.api.ProductDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlayBillingTrialPhaseValidationTest {

    @Test
    fun `three day free phase returns three days`() {
        assertEquals(
            3,
            extractFreeTrialDays(
                priceAmountMicros = 0L,
                recurrenceMode = ProductDetails.RecurrenceMode.FINITE_RECURRING,
                billingCycleCount = 1,
                billingPeriod = "P3D"
            )
        )
    }

    @Test
    fun `one week free phase returns seven days`() {
        assertEquals(
            7,
            extractFreeTrialDays(
                priceAmountMicros = 0L,
                recurrenceMode = ProductDetails.RecurrenceMode.FINITE_RECURRING,
                billingCycleCount = 1,
                billingPeriod = "P1W"
            )
        )
    }

    @Test
    fun `multiple free cycles return total trial days`() {
        assertEquals(
            6,
            extractFreeTrialDays(
                priceAmountMicros = 0L,
                recurrenceMode = ProductDetails.RecurrenceMode.FINITE_RECURRING,
                billingCycleCount = 2,
                billingPeriod = "P3D"
            )
        )
    }

    @Test
    fun `paid phase has no free trial days`() {
        assertNull(
            extractFreeTrialDays(
                priceAmountMicros = 649_000_000L,
                recurrenceMode = ProductDetails.RecurrenceMode.INFINITE_RECURRING,
                billingCycleCount = 0,
                billingPeriod = "P1Y"
            )
        )
    }

    @Test
    fun `infinite zero price phase has no free trial days`() {
        assertNull(
            extractFreeTrialDays(
                priceAmountMicros = 0L,
                recurrenceMode = ProductDetails.RecurrenceMode.INFINITE_RECURRING,
                billingCycleCount = 0,
                billingPeriod = ""
            )
        )
    }

    @Test
    fun `month duration is not approximated as days`() {
        assertNull(
            extractFreeTrialDays(
                priceAmountMicros = 0L,
                recurrenceMode = ProductDetails.RecurrenceMode.FINITE_RECURRING,
                billingCycleCount = 1,
                billingPeriod = "P1M"
            )
        )
    }
}
