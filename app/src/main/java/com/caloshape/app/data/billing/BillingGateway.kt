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

    /**
     * App ?Ÿå? / ?»å…¥å¾?restore entitlement ?¨ã€?     */
    suspend fun queryActiveSubscriptions(): List<ActiveSub>

    /**
     * Paywall é¡¯ç¤º?¹æ ¼?¨ã€?     *
     * ?¹æ ¼å¿…é?ä»?Google Play Billing ProductDetails / PricingPhase ?ºæ?ï¼Œé¿??Play Console
     * èª¿æ•´?¹æ ¼å¾?App ä»é¡¯ç¤ºè? hardcode ?¹æ ¼??     *
     * @param productId Google Play subscription product id.
     * @param offerTag ?¥æ?å®šï??ƒè??–æ?å®?offer ?„ç¬¬ä¸€??paid pricing phase??     *                 ??nullï¼Œæ?è®€??regular base plan??     */
    suspend fun querySubscriptionOfferPrice(
        productId: String,
        offerTag: String? = null
    ): SubscriptionOfferPriceText?

    /**
     * è¨‚é–±?é??Šæ–¹æ¡ˆå?ï¼Œå???Google Play Billing Sheet??     *
     * @param productId Google Play subscription product id.
     * @param offerTag ?¥æ?å®šï??ƒå„ª?ˆä½¿?¨ç¬¦??offerTag ??subscription offer??     *                 ??nullï¼Œæ? fallback ?°ç¬¬ä¸€?‹å¯??offer??     */
    suspend fun launchSubscriptionPurchase(
        activity: Activity,
        productId: String,
        offerTag: String? = null
    ): BillingPurchaseResult

    /**
     * å¾Œç«¯é©—è??å?ä¸¦é???Premium å¾Œï???acknowledge??     */
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean
}
