package com.caloshape.app.ui.home.ui.membership

import com.caloshape.app.data.entitlement.model.PremiumStatus
import com.caloshape.app.core.time.UtcTimeFormatter

enum class MembershipDisplayKind {
    FREE,
    TRIAL,
    PREMIUM,
    PAYMENT_ISSUE
}

data class MembershipDisplay(
    val kind: MembershipDisplayKind,
    val subtitle: MembershipSubtitle
)

sealed interface MembershipSubtitle {
    data object Upgrade : MembershipSubtitle
    data object UpdatePayment : MembershipSubtitle
    data object ActiveMember : MembershipSubtitle
    data object TrialEndsToday : MembershipSubtitle
    data class TrialDaysLeft(val days: Int) : MembershipSubtitle
    data class Until(val date: String) : MembershipSubtitle
}

object MembershipUiMapper {

    fun map(
        status: PremiumStatus,
        currentPremiumUntil: String? = null,
        trialDaysLeft: Int? = null,
        paymentIssue: Boolean = false
    ): MembershipDisplay {
        if (paymentIssue && status == PremiumStatus.PREMIUM) {
            return MembershipDisplay(
                kind = MembershipDisplayKind.PAYMENT_ISSUE,
                subtitle = MembershipSubtitle.UpdatePayment
            )
        }

        return when (status) {
            PremiumStatus.FREE -> MembershipDisplay(
                kind = MembershipDisplayKind.FREE,
                subtitle = MembershipSubtitle.Upgrade
            )

            PremiumStatus.TRIAL -> MembershipDisplay(
                kind = MembershipDisplayKind.TRIAL,
                subtitle = formatTrialSubtitle(trialDaysLeft)
            )

            PremiumStatus.PREMIUM -> MembershipDisplay(
                kind = MembershipDisplayKind.PREMIUM,
                subtitle = formatPremiumSubtitle(currentPremiumUntil)
            )
        }
    }

    private fun formatPremiumSubtitle(currentPremiumUntil: String?): MembershipSubtitle {
        val date = formatDateOrNull(currentPremiumUntil)

        return if (date != null) {
            MembershipSubtitle.Until(date)
        } else {
            MembershipSubtitle.ActiveMember
        }
    }

    private fun formatTrialSubtitle(daysLeft: Int?): MembershipSubtitle {
        val safeDays = (daysLeft ?: 0).coerceAtLeast(0)

        return when (safeDays) {
            0 -> MembershipSubtitle.TrialEndsToday
            else -> MembershipSubtitle.TrialDaysLeft(safeDays)
        }
    }

    fun formatDateOrNull(raw: String?): String? =
        UtcTimeFormatter.formatUtcDateOrNull(raw)
}
