package com.caloshape.app.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloshape.app.data.billing.BillingGateway
import com.caloshape.app.data.billing.CaloShapeBillingProducts
import com.caloshape.app.data.billing.SubscriptionOfferPriceText
import com.caloshape.app.data.entitlement.EntitlementSyncer
import com.caloshape.app.data.entitlement.EntitlementSyncer.Companion.PURCHASE_ALREADY_OWNED_RESTORE_REQUIRED
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
    AlreadyOwnedRestoreRequired
}

data class SubscriptionUiState(
    val selectedProductId: String = CaloShapeBillingProducts.YEARLY,
    val purchasing: Boolean = false,
    val error: String? = null,
    val errorKind: SubscriptionErrorKind? = null,
    val canRestorePurchase: Boolean = false,
    val trialEligible: Boolean = false,
    val trialEligibilityLoaded: Boolean = false,
    val subscriptionOffersLoaded: Boolean = false,
    val subscriptionOfferPriceLoadFailed: Boolean = false,
    val yearlyBaseTrialOfferAvailable: Boolean = false,
    val yearlyDiscountTrialOfferAvailable: Boolean = false,
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
                        error = null,
                        errorKind = null
                    )
                }
            }.onFailure { ex ->
                _ui.update {
                    it.copy(
                        trialEligible = false,
                        trialEligibilityLoaded = true,
                        error = ex.message ?: "Unable to check trial eligibility. Please try again.",
                        errorKind = null
                    )
                }
            }
        }
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

            val baseTrial = runCatching {
                billingGateway.querySubscriptionOfferPrice(
                    productId = CaloShapeBillingProducts.YEARLY,
                    offerTag = CaloShapeBillingProducts.OfferTags.ONBOARD_TRIAL_YEARLY
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
            val baseTrialReady = baseTrial.hasSamePaidPriceAs(base)
            val discountTrialReady = discountTrial.hasSamePaidPriceAs(discount)

            _ui.update {
                it.copy(
                    subscriptionOffersLoaded = true,
                    subscriptionOfferPriceLoadFailed = !requiredPricesReady,
                    yearlyBaseTrialOfferAvailable = baseTrialReady,
                    yearlyDiscountTrialOfferAvailable = discountTrialReady,
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
                        trialEligibilityLoaded = true
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
            val errorKind = if (result.restoreRequired || isAlreadyOwnedRestoreRequiredMessage(result.message)) {
                SubscriptionErrorKind.AlreadyOwnedRestoreRequired
            } else {
                null
            }

            _ui.update {
                it.copy(
                    purchasing = false,
                    error = if (errorKind == null) result.message ?: "Purchase failed" else null,
                    errorKind = errorKind,
                    canRestorePurchase = shouldShowRestore,
                    trialEligible = result.response?.trialEligible ?: it.trialEligible,
                    trialEligibilityLoaded = if (result.response != null) true else it.trialEligibilityLoaded
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

            val response = runCatching {
                entitlementSyncer.refreshEntitlementSummary()
            }.getOrElse { ex ->
                _ui.update {
                    it.copy(
                        purchasing = false,
                        error = ex.message ?: "Could not restore purchase. Please try again.",
                        errorKind = null,
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
                    trialEligibilityLoaded = true
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
                        error = "No active purchase found.",
                        errorKind = null,
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

    private fun isAlreadyOwnedRestoreRequiredMessage(message: String?): Boolean {
        return message == PURCHASE_ALREADY_OWNED_RESTORE_REQUIRED
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

internal fun SubscriptionOfferPriceText?.hasSamePaidPriceAs(
    reference: SubscriptionOfferPriceText?
): Boolean {
    return this != null &&
            reference != null &&
            formattedPrice == reference.formattedPrice &&
            formattedMonthlyEquivalent == reference.formattedMonthlyEquivalent
}
