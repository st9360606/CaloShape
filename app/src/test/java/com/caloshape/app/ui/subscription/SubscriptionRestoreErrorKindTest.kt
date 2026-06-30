package com.caloshape.app.ui.subscription

import com.caloshape.app.data.entitlement.RestoreSubscriptionResult
import org.junit.Assert.assertEquals
import org.junit.Test

class SubscriptionRestoreErrorKindTest {

    @Test
    fun `pending Google Play purchase remains distinct from purchase failure`() {
        assertEquals(
            SubscriptionErrorKind.PurchasePending,
            resolveSubscriptionPurchaseErrorKind(
                restoreRequired = false,
                message = "Purchase is pending. Please wait until Google Play confirms the payment."
            )
        )
    }

    @Test
    fun `generic billing error maps to purchase failure`() {
        assertEquals(
            SubscriptionErrorKind.PurchaseFailed,
            resolveSubscriptionPurchaseErrorKind(
                restoreRequired = false,
                message = "Billing unavailable"
            )
        )
    }

    @Test
    fun `missing Play purchase maps to no active purchase`() {
        assertEquals(
            SubscriptionErrorKind.NoActivePurchase,
            RestoreSubscriptionResult.NoActivePurchase.toSubscriptionErrorKind()
        )
    }

    @Test
    fun `purchase owned by another account maps to bound account error`() {
        assertEquals(
            SubscriptionErrorKind.BoundToAnotherAccount,
            RestoreSubscriptionResult.BoundToAnotherAccount.toSubscriptionErrorKind()
        )
    }

    @Test
    fun `generic restore failure remains distinct`() {
        assertEquals(
            SubscriptionErrorKind.RestoreFailed,
            RestoreSubscriptionResult.Failed().toSubscriptionErrorKind()
        )
    }
}
