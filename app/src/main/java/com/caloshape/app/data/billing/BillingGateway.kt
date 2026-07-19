package com.caloshape.app.data.billing

import android.app.Activity

data class ActiveSub(
    val productId: String,
    val purchaseToken: String,
    val acknowledged: Boolean = false
)

data class SubscriptionOfferPriceText(
    val productId: String,
    val offerTag: String?,
    val formattedPrice: String,
    val formattedMonthlyEquivalent: String?,
    val freeTrialDays: Int? = null
)

sealed interface BillingPurchaseResult {
    data class Success(val sub: ActiveSub) : BillingPurchaseResult
    data object Cancelled : BillingPurchaseResult
    data object Pending : BillingPurchaseResult
    data object AlreadyOwned : BillingPurchaseResult
    data class Error(val message: String) : BillingPurchaseResult
}

interface BillingGateway {

    
    suspend fun queryActiveSubscriptions(): List<ActiveSub>

    
    suspend fun querySubscriptionOfferPrice(
        productId: String,
        offerTag: String? = null
    ): SubscriptionOfferPriceText?

    
    suspend fun launchSubscriptionPurchase(
        activity: Activity,
        productId: String,
        offerTag: String? = null
    ): BillingPurchaseResult

    
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean
}
