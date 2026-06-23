package com.caloshape.app.data.fasting.notifications

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.caloshape.app.data.fasting.model.FastingPlan
import com.caloshape.app.data.fasting.repo.FastingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: FastingRepository,
    private val scheduler: FastingAlarmScheduler
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork() start")

        // ???šçŸ¥è¢«é?ï¼šæœ¬?°æ”¶?‚ï??¿å?å¤©å¤©è¢?alarm ?šé?
        if (!NotificationPermission.isGranted(applicationContext)) {
            Log.w(TAG, "notification not granted -> cancel alarms")
            scheduler.cancel()
            return Result.success()
        }

        val dto = try {
            // ???Œæ™¯?ªè?ï¼šä?è¦è‡ª?•å»ºç«‹é?è¨­ï??´ä?è¦å¯«??enabled=falseï¼?
            repo.getMineOrNull()
        } catch (t: Throwable) {
            Log.e(TAG, "getMineOrNull failed", t)
            return Result.retry()
        }

        if (dto == null || !dto.enabled) {
            Log.d(TAG, "dto missing or enabled=false -> cancel")
            scheduler.cancel()
            return Result.success()
        }

        val plan = FastingPlan.ofOrDefault(dto.planCode)
        val startLocal = LocalTime.parse(dto.startTime)

        // ???ªå?å¾Œç«¯ triggersï¼›å¤±??fallback ?¬åœ°ç®?
        val times: TriggerTimes = try {
            val tr = repo.nextTriggers(plan, startLocal)
            val nextStart = Instant.parse(tr.nextStartUtc)
            val nextEnd = Instant.parse(tr.nextEndUtc)
            TriggerTimes(nextStart, nextEnd, nextEnd.minusSeconds(3600))
        } catch (t: Throwable) {
            Log.w(TAG, "nextTriggers failed -> local fallback", t)
            val zone = runCatching { ZoneId.of(dto.timeZone) }.getOrElse { ZoneId.systemDefault() }
            NextTriggerCalculator.compute(
                startTime = startLocal,
                eatingHours = plan.eatingHours,
                zoneId = zone
            )
        }

        scheduler.schedule(
            startUtc = times.nextStart,
            endSoonUtc = times.endSoon,
            planCode = dto.planCode,
            startTime = dto.startTime,
            endTime = dto.endTime
        )

        Log.d(TAG, "schedule done")
        return Result.success()
    }

    companion object {
        private const val TAG = "FASTING"
    }
}
