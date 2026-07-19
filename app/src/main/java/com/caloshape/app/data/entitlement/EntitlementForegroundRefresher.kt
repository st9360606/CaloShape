package com.caloshape.app.data.entitlement

import android.util.Log
import com.caloshape.app.data.auth.state.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Refreshes the server-owned entitlement summary whenever the app returns to
 * the foreground. It deliberately does not query Google Play or restore a
 * purchase: those actions remain explicit user flows.
 */
class EntitlementForegroundRefresher(
    private val scope: CoroutineScope,
    private val authState: AuthState,
    private val entitlementSyncer: EntitlementSyncer
) {
    fun onForeground() {
        scope.launch {
            if (!authState.isSignedInFlow.first()) return@launch

            runCatching { entitlementSyncer.refreshServerEntitlementSummaryOnly() }
                .onFailure { error ->
                    Log.w(TAG, "foreground entitlement refresh failed: ${error.javaClass.simpleName}")
                }
        }
    }

    private companion object {
        const val TAG = "EntitlementForeground"
    }
}
