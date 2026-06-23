package com.caloshape.app.data.referral.repo

import com.caloshape.app.data.referral.api.ClaimReferralRequest
import com.caloshape.app.data.referral.api.ClaimReferralResponse
import com.caloshape.app.data.referral.api.ReferralApi
import com.caloshape.app.data.referral.api.ReferralSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferralRepository @Inject constructor(
    private val api: ReferralApi
) {
    suspend fun getSummary(): ReferralSummaryDto = api.me()
    suspend fun claim(promoCode: String): ClaimReferralResponse = api.claim(ClaimReferralRequest(promoCode.trim()))
}
