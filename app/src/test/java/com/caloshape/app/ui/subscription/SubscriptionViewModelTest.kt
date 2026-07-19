package com.caloshape.app.ui.subscription

import android.app.Activity
import com.caloshape.app.data.billing.BillingGateway
import com.caloshape.app.data.entitlement.EntitlementSyncer
import com.caloshape.app.data.entitlement.PurchaseEntitlementResult
import com.caloshape.app.data.entitlement.RestoreSubscriptionResult
import com.caloshape.app.data.entitlement.api.EntitlementSyncResponse
import com.caloshape.app.data.membership.api.MembershipSummaryDto
import com.caloshape.app.data.membership.repo.MembershipRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var syncer: EntitlementSyncer
    private lateinit var membershipRepository: MembershipRepository
    private lateinit var billingGateway: BillingGateway
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        syncer = mockk()
        membershipRepository = mockk()
        billingGateway = mockk()
        activity = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun purchase_success_updatesTrialEligibilityAndCallsSuccess() = runTest(dispatcher) {
        val response = activeEntitlement(trialEligible = false)
        coEvery { syncer.purchaseSubscriptionAndSync(activity, "yearly", any()) } returns
            PurchaseEntitlementResult(success = true, response = response)
        val viewModel = createViewModel()
        var returned: EntitlementSyncResponse? = null

        viewModel.purchaseProduct(
            activity = activity,
            productId = "yearly",
            onSuccess = { returned = it }
        )
        advanceUntilIdle()

        assertEquals(response, returned)
        assertFalse(viewModel.ui.value.purchasing)
        assertNull(viewModel.ui.value.errorKind)
        assertTrue(viewModel.ui.value.trialEligibilityLoaded)
        assertFalse(viewModel.ui.value.trialEligible)
    }

    @Test
    fun purchase_cancelled_returnsToIdleWithoutAnError() = runTest(dispatcher) {
        coEvery { syncer.purchaseSubscriptionAndSync(activity, "yearly", any()) } returns
            PurchaseEntitlementResult(success = false, message = "Purchase cancelled")
        val viewModel = createViewModel()
        var cancelled = false

        viewModel.purchaseProduct(
            activity = activity,
            productId = "yearly",
            onSuccess = {},
            onCancelled = { cancelled = true }
        )
        advanceUntilIdle()

        assertTrue(cancelled)
        assertFalse(viewModel.ui.value.purchasing)
        assertNull(viewModel.ui.value.errorKind)
    }

    @Test
    fun pendingPurchaseShowsPendingStateInsteadOfGenericFailure() = runTest(dispatcher) {
        coEvery { syncer.purchaseSubscriptionAndSync(activity, "yearly", any()) } returns
            PurchaseEntitlementResult(success = false, message = "Purchase is pending")
        val viewModel = createViewModel()

        viewModel.purchaseProduct(activity, "yearly", onSuccess = {})
        advanceUntilIdle()

        assertEquals(SubscriptionErrorKind.PurchasePending, viewModel.ui.value.errorKind)
        assertFalse(viewModel.ui.value.canRestorePurchase)
    }

    @Test
    fun duplicatePurchaseTapOnlyStartsOneBillingFlow() = runTest(dispatcher) {
        val gate = CompletableDeferred<PurchaseEntitlementResult>()
        coEvery { syncer.purchaseSubscriptionAndSync(activity, "yearly", any()) } coAnswers {
            gate.await()
        }
        val viewModel = createViewModel()

        viewModel.purchaseProduct(activity, "yearly", onSuccess = {})
        runCurrent()
        viewModel.purchaseProduct(activity, "yearly", onSuccess = {})
        gate.complete(PurchaseEntitlementResult(success = true, response = activeEntitlement()))
        advanceUntilIdle()

        coVerify(exactly = 1) { syncer.purchaseSubscriptionAndSync(activity, "yearly", any()) }
    }

    @Test
    fun trialEligibilityUsesBackendResultAndFailsClosed() = runTest(dispatcher) {
        coEvery { membershipRepository.getSummary() } returns MembershipSummaryDto(
            premiumStatus = "FREE",
            trialEligible = true
        )
        val eligibleViewModel = createViewModel()

        eligibleViewModel.loadTrialEligibility()
        advanceUntilIdle()

        assertTrue(eligibleViewModel.ui.value.trialEligible)
        assertFalse(eligibleViewModel.ui.value.trialEligibilityCheckFailed)

        coEvery { membershipRepository.getSummary() } throws IllegalStateException("offline")
        val failingViewModel = createViewModel()
        failingViewModel.loadTrialEligibility()
        advanceUntilIdle()

        assertFalse(failingViewModel.ui.value.trialEligible)
        assertTrue(failingViewModel.ui.value.trialEligibilityCheckFailed)
        assertEquals(
            SubscriptionErrorKind.TrialEligibilityCheckFailed,
            failingViewModel.ui.value.errorKind
        )
    }

    @Test
    fun dismissTrialEligibilityError_keepsTrialDisabledAndAllowsThePaywallToContinue() =
        runTest(dispatcher) {
            coEvery { membershipRepository.getSummary() } throws IllegalStateException("offline")
            val viewModel = createViewModel()

            viewModel.loadTrialEligibility()
            advanceUntilIdle()
            viewModel.dismissTrialEligibilityError()

            assertFalse(viewModel.ui.value.trialEligible)
            assertTrue(viewModel.ui.value.trialEligibilityLoaded)
            assertTrue(viewModel.ui.value.trialEligibilityCheckFailed)
            assertTrue(viewModel.ui.value.trialEligibilityErrorDismissed)
            assertNull(viewModel.ui.value.errorKind)
        }

    @Test
    fun restoreWithGracePeriodRefreshesServerEntitlementAndSucceeds() = runTest(dispatcher) {
        val response = activeEntitlement()
        coEvery { syncer.restoreSubscription() } returns RestoreSubscriptionResult.RestoredWithPaymentIssue(
            MembershipSummaryDto(
                premiumStatus = "PREMIUM",
                paymentIssue = true
            )
        )
        coEvery { syncer.refreshServerEntitlementSummaryOnly() } returns response
        val viewModel = createViewModel()
        var restored: EntitlementSyncResponse? = null

        viewModel.restorePurchase { restored = it }
        advanceUntilIdle()

        assertEquals(response, restored)
        assertFalse(viewModel.ui.value.purchasing)
        assertNull(viewModel.ui.value.errorKind)
    }

    private fun createViewModel() = SubscriptionViewModel(
        entitlementSyncer = syncer,
        membershipRepository = membershipRepository,
        billingGateway = billingGateway
    )

    private fun activeEntitlement(trialEligible: Boolean = true) = EntitlementSyncResponse(
        status = "ACTIVE",
        entitlementType = "YEARLY",
        premiumStatus = "PREMIUM",
        currentPremiumUntil = "2027-01-01T00:00:00Z",
        trialEligible = trialEligible
    )
}
