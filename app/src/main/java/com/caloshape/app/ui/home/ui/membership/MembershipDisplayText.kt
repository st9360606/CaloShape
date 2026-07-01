package com.caloshape.app.ui.home.ui.membership

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.caloshape.app.R

@Composable
fun localizedMembershipTitle(kind: MembershipDisplayKind): String {
    return stringResource(
        when (kind) {
            MembershipDisplayKind.FREE -> R.string.settings_membership_free
            MembershipDisplayKind.TRIAL -> R.string.settings_membership_trial
            MembershipDisplayKind.PREMIUM -> R.string.settings_membership_premium
            MembershipDisplayKind.PAYMENT_ISSUE -> R.string.settings_membership_payment
        }
    )
}

@Composable
fun localizedMembershipSubtitle(subtitle: MembershipSubtitle): String {
    return when (subtitle) {
        MembershipSubtitle.Upgrade -> stringResource(R.string.settings_membership_upgrade)
        MembershipSubtitle.UpdatePayment -> stringResource(R.string.settings_membership_update_payment)
        MembershipSubtitle.ActiveMember -> stringResource(R.string.settings_membership_active_member)
        MembershipSubtitle.TrialEndsToday -> stringResource(R.string.settings_membership_trial_ends_today)
        is MembershipSubtitle.TrialDaysLeft -> pluralStringResource(
            R.plurals.settings_membership_trial_days_left,
            subtitle.days,
            subtitle.days
        )
        is MembershipSubtitle.Until -> stringResource(
            R.string.settings_membership_until_format,
            subtitle.date
        )
    }
}
