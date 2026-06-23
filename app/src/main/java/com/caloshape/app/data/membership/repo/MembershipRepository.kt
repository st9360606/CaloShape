package com.caloshape.app.data.membership.repo

import com.caloshape.app.data.membership.api.MembershipApi
import com.caloshape.app.data.membership.api.MembershipSummaryDto
import com.caloshape.app.data.membership.api.RewardHistoryItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MembershipRepository @Inject constructor(
    private val api: MembershipApi
) {
    suspend fun getSummary(): MembershipSummaryDto = api.me()
    suspend fun getRewardHistory(): List<RewardHistoryItemDto> = api.rewards()
}
