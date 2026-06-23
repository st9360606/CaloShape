package com.caloshape.app.data.entitlement

import com.caloshape.app.data.membership.api.MembershipSummaryDto

sealed interface RestoreSubscriptionResult {
    data class Restored(val summary: MembershipSummaryDto) : RestoreSubscriptionResult
    data class RestoredWithPaymentIssue(val summary: MembershipSummaryDto) : RestoreSubscriptionResult
    data object NoActivePurchase : RestoreSubscriptionResult
    data object BoundToAnotherAccount : RestoreSubscriptionResult
    data class Failed(val message: String? = null) : RestoreSubscriptionResult
}
