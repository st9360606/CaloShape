package com.caloshape.app.ui.subscription

import com.caloshape.app.data.billing.SubscriptionOfferPriceText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionOfferPriceMatchTest {

    @Test
    fun `trial offer is available when its paid price matches displayed price`() {
        val displayed = price(
            offerTag = "paid",
            formattedPrice = "NT$649.00",
            formattedMonthlyEquivalent = "NT$54.08"
        )
        val trial = price(
            offerTag = "trial",
            formattedPrice = "NT$649.00",
            formattedMonthlyEquivalent = "NT$54.08"
        )

        assertTrue(trial.hasSamePaidPriceAs(displayed))
    }

    @Test
    fun `trial offer is unavailable when its paid price differs from displayed price`() {
        val displayed = price(
            offerTag = "paid",
            formattedPrice = "NT$649.00",
            formattedMonthlyEquivalent = "NT$54.08"
        )
        val trial = price(
            offerTag = "trial",
            formattedPrice = "NT$999.00",
            formattedMonthlyEquivalent = "NT$83.25"
        )

        assertFalse(trial.hasSamePaidPriceAs(displayed))
    }

    @Test
    fun `missing trial offer is unavailable`() {
        assertFalse(null.hasSamePaidPriceAs(price(offerTag = "paid")))
    }

    private fun price(
        offerTag: String,
        formattedPrice: String = "NT$649.00",
        formattedMonthlyEquivalent: String = "NT$54.08"
    ): SubscriptionOfferPriceText {
        return SubscriptionOfferPriceText(
            productId = "caloshape_yearly",
            offerTag = offerTag,
            formattedPrice = formattedPrice,
            formattedMonthlyEquivalent = formattedMonthlyEquivalent
        )
    }
}
