package com.caloshape.app.data.entitlement

import com.caloshape.app.data.auth.state.AuthState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntitlementForegroundRefresherTest {

    @Test
    fun returningToForegroundRefreshesOnlyTheSignedInAccountsServerSummary() = runTest {
        val authState = mockk<AuthState>()
        val entitlementSyncer = mockk<EntitlementSyncer>(relaxed = true)
        every { authState.isSignedInFlow } returns flowOf(true)

        EntitlementForegroundRefresher(this, authState, entitlementSyncer).onForeground()
        advanceUntilIdle()

        coVerify(exactly = 1) { entitlementSyncer.refreshServerEntitlementSummaryOnly() }
        coVerify(exactly = 0) { entitlementSyncer.refreshEntitlementSummary() }
    }

    @Test
    fun activityRecreationWhileSignedOutDoesNotCallEntitlementEndpoints() = runTest {
        val authState = mockk<AuthState>()
        val entitlementSyncer = mockk<EntitlementSyncer>(relaxed = true)
        every { authState.isSignedInFlow } returns flowOf(false)

        EntitlementForegroundRefresher(this, authState, entitlementSyncer).onForeground()
        advanceUntilIdle()

        coVerify(exactly = 0) { entitlementSyncer.refreshServerEntitlementSummaryOnly() }
        coVerify(exactly = 0) { entitlementSyncer.refreshEntitlementSummary() }
    }

    @Test
    fun activityRecreationWhileSignedInRefreshesServerSummaryWithoutPlayRestore() = runTest {
        val authState = mockk<AuthState>()
        val entitlementSyncer = mockk<EntitlementSyncer>(relaxed = true)
        every { authState.isSignedInFlow } returns flowOf(true)

        EntitlementForegroundRefresher(this, authState, entitlementSyncer).onForeground()
        EntitlementForegroundRefresher(this, authState, entitlementSyncer).onForeground()
        advanceUntilIdle()

        coVerify(exactly = 2) { entitlementSyncer.refreshServerEntitlementSummaryOnly() }
        coVerify(exactly = 0) { entitlementSyncer.refreshEntitlementSummary() }
    }
}
