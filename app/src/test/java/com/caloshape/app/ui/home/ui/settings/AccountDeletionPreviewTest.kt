package com.caloshape.app.ui.home.ui.settings

import com.caloshape.app.data.account.api.AccountDeletionPreviewResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountDeletionPreviewTest {

    @Test
    fun activeGooglePlaySubscription_requiresWarningBeforeFinalConfirmation() {
        val preview = AccountDeletionPreviewResponse(
            hasActiveGooglePlaySubscription = true,
            requiresSubscriptionWarning = true
        )

        assertEquals(
            AccountDeletionNextStep.SUBSCRIPTION_WARNING,
            resolveAccountDeletionNextStep(preview)
        )
    }

    @Test
    fun freeAccount_canProceedToFinalConfirmation() {
        assertEquals(
            AccountDeletionNextStep.FINAL_CONFIRMATION,
            resolveAccountDeletionNextStep(AccountDeletionPreviewResponse())
        )
    }

    @Test
    fun missingOrBlockedPreview_failsClosed() {
        assertEquals(AccountDeletionNextStep.ERROR, resolveAccountDeletionNextStep(null))
        assertEquals(
            AccountDeletionNextStep.ERROR,
            resolveAccountDeletionNextStep(AccountDeletionPreviewResponse(canDelete = false))
        )
    }
}
