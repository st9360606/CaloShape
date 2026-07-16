package com.caloshape.app.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloshape.app.data.billing.BillingGateway
import com.caloshape.app.data.billing.CaloShapeBillingProducts
import com.caloshape.app.data.billing.SubscriptionOfferPriceText
import com.caloshape.app.data.entitlement.EntitlementSyncer
import com.caloshape.app.data.entitlement.EntitlementSyncer.Companion.PURCHASE_ALREADY_OWNED_RESTORE_REQUIRED
import com.caloshape.app.data.entitlement.RestoreSubscriptionResult
import com.caloshape.app.data.entitlement.api.EntitlementSyncResponse
import com.caloshape.app.data.membership.repo.MembershipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class SubscriptionErrorKind {
    AlreadyOwnedRestoreRequired,
    NoActivePurchase,
    BoundToAnotherAccount,
    RestoreFailed,
    PurchasePending,
    PurchaseFailed,
    TrialEligibilityCheckFailed
}

data class SubscriptionUiState(
    val selectedProductId: String = CaloShapeBillingProducts.YEARLY,
    val purchasing: Boolean = false,
    val error: String? = null,
    val errorKind: SubscriptionErrorKind? = null,
    val canRestorePurchase: Boolean = false,
    val trialEligible: Boolean = false,
    val trialEligibilityLoaded: Boolean = false,
    val trialEligibilityCheckFailed: Boolean = false,
    val subscriptionOffersLoaded: Boolean = false,
    val subscriptionOfferPriceLoadFailed: Boolean = false,
    val yearlyDiscountTrialDays: Int? = null,
    val yearlyBasePrice: String? = null,
    val yearlyBaseMonthlyEquivalent: String? = null,
    val yearlyDiscountPrice: String? = null,
    val yearlyDiscountMonthlyEquivalent: String? = null
) {
    val busy: Boolean
        get() = purchasing
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val entitlementSyncer: EntitlementSyncer,
    private val membershipRepository: MembershipRepository,
    private val billingGateway: BillingGateway
) : ViewModel() {

    private val _ui = MutableStateFlow(SubscriptionUiState())
    val ui = _ui.asStateFlow()

    fun loadTrialEligibility() {
        if (_ui.value.trialEligibilityLoaded) return

        viewModelScope.launch {
            runCatching {
                membershipRepository.getSummary()
            }.onSuccess { summary ->
                _ui.update {
                    it.copy(
                        trialEligible = summary.trialEligible,
                        trialEligibilityLoaded = true,
                        trialEligibilityCheckFailed = false,
                        error = null,
                        errorKind = null
                    )
                }
            }.onFailure {
                _ui.update {
                    it.copy(
                        trialEligible = false,
                        trialEligibilityLoaded = true,
                        trialEligibilityCheckFailed = true,
                        error = null,
                        errorKind = SubscriptionErrorKind.TrialEligibilityCheckFailed
                    )
                }
            }
        }
    }

    fun retryTrialEligibility() {
        if (_ui.value.busy) return

        _ui.update {
            it.copy(
                trialEligible = false,
                trialEligibilityLoaded = false,
                trialEligibilityCheckFailed = false,
                error = null,
                errorKind = null
            )
        }

        loadTrialEligibility()
    }

    fun loadSubscriptionOfferPrices() {
        viewModelScope.launch {
            _ui.update {
                it.copy(
                    subscriptionOffersLoaded = false,
                    subscriptionOfferPriceLoadFailed = false
                )
            }

            val base = runCatching {
                billingGateway.querySubscriptionOfferPrice(
                    productId = CaloShapeBillingProducts.YEARLY,
                    offerTag = CaloShapeBillingProducts.OfferTags.DEFAULT_YEARLY
                )
            }.getOrNull()

            val discount = runCatching {
                billingGateway.querySubscriptionOfferPrice(
                    productId = CaloShapeBillingProducts.YEARLY,
                    offerTag = CaloShapeBillingProducts.OfferTags.ONBOARD_DISCOUNT_YEARLY
                )
            }.getOrNull()

            val discountTrial = runCatching {
                billingGateway.querySubscriptionOfferPrice(
                    productId = CaloShapeBillingProducts.YEARLY,
                    offerTag = CaloShapeBillingProducts.OfferTags.ONBOARD_TRIAL_DISCOUNT_YEARLY
                )
            }.getOrNull()

            val baseReady = base?.formattedMonthlyEquivalent != null
            val discountReady = discount?.formattedMonthlyEquivalent != null
            val requiredPricesReady = baseReady && discountReady
            val discountTrialReady = discountTrial.hasSamePaidPriceAs(discount)
            val discountTrialDays = if (discountTrialReady) {
                discountTrial?.freeTrialDays?.takeIf { it > 0 }
            } else {
                null
            }

            _ui.update {
                it.copy(
                    subscriptionOffersLoaded = true,
                    subscriptionOfferPriceLoadFailed = !requiredPricesReady,
                    yearlyDiscountTrialDays = discountTrialDays,
                    yearlyBasePrice = base?.formattedPrice,
                    yearlyBaseMonthlyEquivalent = base?.formattedMonthlyEquivalent,
                    yearlyDiscountPrice = discount?.formattedPrice,
                    yearlyDiscountMonthlyEquivalent = discount?.formattedMonthlyEquivalent
                )
            }
        }
    }

    fun selectProduct(productId: String) {
        if (_ui.value.busy) return

        _ui.update {
            it.copy(
                selectedProductId = productId,
                error = null,
                errorKind = null,
                canRestorePurchase = false
            )
        }
    }

    fun purchase(
        activity: Activity,
        offerTag: String? = null,
        onSuccess: (EntitlementSyncResponse) -> Unit,
        onCancelled: (() -> Unit)? = null
    ) {
        purchaseProduct(
            activity = activity,
            productId = _ui.value.selectedProductId,
            offerTag = offerTag,
            onSuccess = onSuccess,
            onCancelled = onCancelled
        )
    }

    fun purchaseProduct(
        activity: Activity,
        productId: String,
        offerTag: String? = null,
        onSuccess: (EntitlementSyncResponse) -> Unit,
        onCancelled: (() -> Unit)? = null
    ) {
        if (_ui.value.busy) return

        viewModelScope.launch {
            _ui.update {
                it.copy(
                    selectedProductId = productId,
                    purchasing = true,
                    error = null,
                    errorKind = null,
                    canRestorePurchase = false
                )
            }

            val result = entitlementSyncer.purchaseSubscriptionAndSync(
                activity = activity,
                productId = productId,
                offerTag = offerTag
            )

            if (result.success && result.response != null) {
                _ui.update {
                    it.copy(
                        purchasing = false,
                        error = null,
                        errorKind = null,
                        canRestorePurchase = false,
                        trialEligible = result.response.trialEligible,
                        trialEligibilityLoaded = true,
                        trialEligibilityCheckFailed = false
                    )
                }
                onSuccess(result.response)
                return@launch
            }

            if (isPurchaseCancelledMessage(result.message)) {
                _ui.update {
                    it.copy(
                        purchasing = false,
                        error = null,
                        errorKind = null,
                        canRestorePurchase = false
                    )
                }
                onCancelled?.invoke()
                return@launch
            }

            val shouldShowRestore = result.restoreRequired || isPostPurchaseSyncFailure(result.message)
            val errorKind = resolveSubscriptionPurchaseErrorKind(
                restoreRequired = result.restoreRequired,
                message = result.message
            )

            _ui.update {
                it.copy(
                    purchasing = false,
                    error = null,
                    errorKind = errorKind,
                    canRestorePurchase = shouldShowRestore,
                    trialEligible = result.response?.trialEligible ?: it.trialEligible,
                    trialEligibilityLoaded = if (result.response != null) true else it.trialEligibilityLoaded,
                    trialEligibilityCheckFailed =
                        if (result.response != null) false else it.trialEligibilityCheckFailed
                )
            }
        }
    }

    fun restorePurchase(onSuccess: (EntitlementSyncResponse) -> Unit) {
        if (_ui.value.busy) return

        viewModelScope.launch {
            _ui.update {
                it.copy(
                    purchasing = true,
                    error = null,
                    errorKind = null,
                    canRestorePurchase = false
                )
            }

            val restoreResult = runCatching {
                entitlementSyncer.restoreSubscription()
            }.getOrElse {
                RestoreSubscriptionResult.Failed()
            }

            val restored =
                restoreResult is RestoreSubscriptionResult.Restored ||
                        restoreResult is RestoreSubscriptionResult.RestoredWithPaymentIssue

            if (!restored) {
                _ui.update {
                    it.copy(
                        purchasing = false,
                        error = null,
                        errorKind = restoreResult.toSubscriptionErrorKind(),
                        canRestorePurchase =
                            restoreResult !is RestoreSubscriptionResult.BoundToAnotherAccount
                    )
                }
                return@launch
            }

            val response = runCatching {
                entitlementSyncer.refreshServerEntitlementSummaryOnly()
            }.getOrElse {
                _ui.update {
                    it.copy(
                        purchasing = false,
                        error = null,
                        errorKind = SubscriptionErrorKind.RestoreFailed,
                        canRestorePurchase = true
                    )
                }
                return@launch
            }

            _ui.update {
                it.copy(
                    purchasing = false,
                    errorKind = null,
                    trialEligible = response.trialEligible,
                    trialEligibilityLoaded = true,
                    trialEligibilityCheckFailed = false
                )
            }

            if (hasOpenedEntitlement(response)) {
                _ui.update {
                    it.copy(
                        error = null,
                        errorKind = null,
                        canRestorePurchase = false
                    )
                }
                onSuccess(response)
            } else {
                _ui.update {
                    it.copy(
                        error = null,
                        errorKind = SubscriptionErrorKind.NoActivePurchase,
                        canRestorePurchase = true
                    )
                }
            }
        }
    }

    private fun hasOpenedEntitlement(response: EntitlementSyncResponse): Boolean {
        val premiumStatus = response.premiumStatus.uppercase(Locale.US)

        return response.status.equals("ACTIVE", ignoreCase = true) &&
                (premiumStatus == "PREMIUM" || premiumStatus == "TRIAL") &&
                !response.entitlementType.isNullOrBlank() &&
                response.currentPremiumUntil != null
    }

    private fun isPostPurchaseSyncFailure(message: String?): Boolean {
        return message.orEmpty().contains(
            other = "entitlement sync failed",
            ignoreCase = true
        )
    }

    private fun isPurchaseCancelledMessage(message: String?): Boolean {
        val raw = message.orEmpty()

        return raw.contains("cancel", ignoreCase = true) ||
                raw.contains("USER_CANCELED", ignoreCase = true)
    }
}

internal fun resolveSubscriptionPurchaseErrorKind(
    restoreRequired: Boolean,
    message: String?
): SubscriptionErrorKind {
    return when {
        restoreRequired || message == PURCHASE_ALREADY_OWNED_RESTORE_REQUIRED -> {
            SubscriptionErrorKind.AlreadyOwnedRestoreRequired
        }

        message.orEmpty().contains("pending", ignoreCase = true) -> {
            SubscriptionErrorKind.PurchasePending
        }

        else -> SubscriptionErrorKind.PurchaseFailed
    }
}

internal fun RestoreSubscriptionResult.toSubscriptionErrorKind(): SubscriptionErrorKind? {
    return when (this) {
        RestoreSubscriptionResult.NoActivePurchase -> SubscriptionErrorKind.NoActivePurchase
        RestoreSubscriptionResult.BoundToAnotherAccount -> SubscriptionErrorKind.BoundToAnotherAccount
        is RestoreSubscriptionResult.Failed -> SubscriptionErrorKind.RestoreFailed
        is RestoreSubscriptionResult.Restored,
        is RestoreSubscriptionResult.RestoredWithPaymentIssue -> null
    }
}

internal fun SubscriptionOfferPriceText?.hasSamePaidPriceAs(
    reference: SubscriptionOfferPriceText?
): Boolean {
    return this != null &&
            reference != null &&
            formattedPrice == reference.formattedPrice &&
            formattedMonthlyEquivalent == reference.formattedMonthlyEquivalent
}
