package com.caloshape.app.data.fasting.repo

import com.caloshape.app.data.fasting.api.FastingApi
import com.caloshape.app.data.fasting.api.FastingPlanDto
import com.caloshape.app.data.fasting.api.NextTriggersResp
import com.caloshape.app.data.fasting.api.UpsertFastingPlanReq
import com.caloshape.app.data.fasting.model.FastingPlan
import retrofit2.HttpException
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FastingRepository(
    private val api: FastingApi,
    private val zoneIdProvider: () -> ZoneId
) {
    private val fmt = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * ?ªè?ï¼šä?å­˜åœ¨?å? nullï¼?04ï¼‰ï??¶ä? HTTP ?¯èª¤?´æ¥ä¸Ÿå‡º
     */
    suspend fun getMineOrNull(): FastingPlanDto? {
        val resp = api.getMine()
        if (resp.isSuccessful) return resp.body()
        if (resp.code() == 404) return null
        throw HttpException(resp)
    }

    /**
     * ?ªåœ¨?Œç??„ä?å­˜åœ¨(404)?æ?å»ºç??è¨­
     * ??ä¸è???5xx ?‚å¯«?é?è¨­ï??¿å?è¦†è??¨æˆ¶è¨­å?
     */
    suspend fun ensureDefaultIfMissing(): FastingPlanDto {
        val exist = getMineOrNull()
        if (exist != null) return exist

        val tz = zoneIdProvider().id
        return api.upsert(
            UpsertFastingPlanReq(
                planCode = "16:8",
                startTime = "09:00",
                enabled = false,
                timeZone = tz
            )
        )
    }

    suspend fun save(planCode: String, start: LocalTime, enabled: Boolean): FastingPlanDto {
        val tz = zoneIdProvider().id
        return api.upsert(
            UpsertFastingPlanReq(
                planCode = planCode,
                startTime = start.format(fmt),
                enabled = enabled,
                timeZone = tz
            )
        )
    }

    suspend fun nextTriggers(plan: FastingPlan, start: LocalTime): NextTriggersResp {
        val tz = zoneIdProvider().id
        return api.nextTriggers(
            planCode = plan.code,
            startTime = start.format(fmt),
            timeZone = tz
        )
    }
}
