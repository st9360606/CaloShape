package com.caloshape.app.ui.subscription

import com.caloshape.app.R
import com.caloshape.app.data.billing.CaloShapeBillingProducts
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardOfferSelectionTest {

    @Test
    fun `eligible user with enabled discount trial uses discount trial offer`() {
        assertEquals(
            CaloShapeBillingProducts.OfferTags.ONBOARD_TRIAL_DISCOUNT_YEARLY,
            resolveOneTimeOfferTag(
                trialEnabled = true,
                trialEligible = true,
                discountTrialOfferAvailable = true
            )
        )
    }

    @Test
    fun `disabled trial uses paid discount offer`() {
        assertEquals(
            CaloShapeBillingProducts.OfferTags.ONBOARD_DISCOUNT_YEARLY,
            resolveOneTimeOfferTag(
                trialEnabled = false,
                trialEligible = true,
                discountTrialOfferAvailable = true
            )
        )
    }

    @Test
    fun `user with trial history uses paid discount offer`() {
        assertEquals(
            CaloShapeBillingProducts.OfferTags.ONBOARD_DISCOUNT_YEARLY,
            resolveOneTimeOfferTag(
                trialEnabled = true,
                trialEligible = false,
                discountTrialOfferAvailable = true
            )
        )
    }

    @Test
    fun `missing discount trial offer falls back to paid discount offer`() {
        assertEquals(
            CaloShapeBillingProducts.OfferTags.ONBOARD_DISCOUNT_YEARLY,
            resolveOneTimeOfferTag(
                trialEnabled = true,
                trialEligible = true,
                discountTrialOfferAvailable = false
            )
        )
    }

    @Test
    fun `trial eligibility check failure does not say trial was already used`() {
        assertEquals(
            R.string.subscription_trial_unavailable,
            resolveTrialStatusTextResource(
                trialEligibilityLoaded = true,
                trialEligibilityCheckFailed = true,
                trialEligible = false,
                trialOfferAvailable = true,
                trialEnabled = false
            )
        )
    }

    @Test
    fun `confirmed ineligible user still sees trial already used`() {
        assertEquals(
            R.string.subscription_trial_already_used,
            resolveTrialStatusTextResource(
                trialEligibilityLoaded = true,
                trialEligibilityCheckFailed = false,
                trialEligible = false,
                trialOfferAvailable = true,
                trialEnabled = false
            )
        )
    }
}
