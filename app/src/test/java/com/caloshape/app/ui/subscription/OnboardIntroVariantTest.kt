package com.caloshape.app.ui.subscription

import com.caloshape.app.data.billing.CaloShapeBillingProducts
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardIntroVariantTest {

    @Test
    fun `eligible user with Play trial offer sees trial intro`() {
        assertEquals(
            OnboardIntroVariant.Trial,
            resolveOnboardIntroVariant(
                trialEligible = true,
                trialOfferAvailable = true
            )
        )
    }

    @Test
    fun `user with trial history sees standard intro`() {
        assertEquals(
            OnboardIntroVariant.Standard,
            resolveOnboardIntroVariant(
                trialEligible = false,
                trialOfferAvailable = true
            )
        )
    }

    @Test
    fun `user without eligible Play offer sees standard intro`() {
        assertEquals(
            OnboardIntroVariant.Standard,
            resolveOnboardIntroVariant(
                trialEligible = true,
                trialOfferAvailable = false
            )
        )
    }

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
}
