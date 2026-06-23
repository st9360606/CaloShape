package com.caloshape.app.ui.subscription

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
}
