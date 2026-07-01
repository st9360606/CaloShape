package com.caloshape.app.ui.home.ui.membership

import com.caloshape.app.data.entitlement.model.PremiumStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MembershipUiMapperTest {

    @Test
    fun `free membership maps to upgrade subtitle`() {
        val display = MembershipUiMapper.map(status = PremiumStatus.FREE)

        assertEquals(MembershipDisplayKind.FREE, display.kind)
        assertEquals(MembershipSubtitle.Upgrade, display.subtitle)
    }

    @Test
    fun `premium payment issue maps to update payment subtitle`() {
        val display = MembershipUiMapper.map(
            status = PremiumStatus.PREMIUM,
            paymentIssue = true
        )

        assertEquals(MembershipDisplayKind.PAYMENT_ISSUE, display.kind)
        assertEquals(MembershipSubtitle.UpdatePayment, display.subtitle)
    }

    @Test
    fun `trial ending today maps to today subtitle`() {
        val display = MembershipUiMapper.map(
            status = PremiumStatus.TRIAL,
            trialDaysLeft = 0
        )

        assertEquals(MembershipDisplayKind.TRIAL, display.kind)
        assertEquals(MembershipSubtitle.TrialEndsToday, display.subtitle)
    }

    @Test
    fun `trial with remaining days preserves count for plural resources`() {
        val display = MembershipUiMapper.map(
            status = PremiumStatus.TRIAL,
            trialDaysLeft = 3
        )

        assertEquals(MembershipSubtitle.TrialDaysLeft(3), display.subtitle)
    }

    @Test
    fun `premium without valid expiry maps to active member subtitle`() {
        val display = MembershipUiMapper.map(
            status = PremiumStatus.PREMIUM,
            currentPremiumUntil = null
        )

        assertEquals(MembershipDisplayKind.PREMIUM, display.kind)
        assertEquals(MembershipSubtitle.ActiveMember, display.subtitle)
    }

    @Test
    fun `premium with valid expiry maps to localized until value`() {
        val display = MembershipUiMapper.map(
            status = PremiumStatus.PREMIUM,
            currentPremiumUntil = "2026-07-15T12:00:00Z"
        )

        assertTrue(display.subtitle is MembershipSubtitle.Until)
        assertTrue((display.subtitle as MembershipSubtitle.Until).date.isNotBlank())
    }
}
