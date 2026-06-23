package com.caloshape.app.data.workout.repo

import com.caloshape.app.data.workout.api.EstimateRequest
import com.caloshape.app.data.workout.api.EstimateResponse
import com.caloshape.app.data.workout.api.LogWorkoutRequest
import com.caloshape.app.data.workout.api.LogWorkoutResponse
import com.caloshape.app.data.workout.api.PresetWorkoutDto
import com.caloshape.app.data.workout.api.TodayWorkoutResponse
import com.caloshape.app.data.workout.api.WorkoutHistoryResponse
import com.caloshape.app.data.workout.api.WorkoutApi
import com.caloshape.app.data.workout.model.WorkoutWeeklyProgressDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi
) {

    suspend fun estimateFreeText(text: String): EstimateResponse {
        return api.estimate(EstimateRequest(text = text))
    }

    suspend fun saveWorkout(
        activityId: Long,
        minutes: Int,
        kcal: Int?
    ): LogWorkoutResponse {
        val req = LogWorkoutRequest(
            activityId = activityId,
            minutes = minutes,
            kcal = kcal
        )
        return api.log(req)
    }

    suspend fun loadPresets(): List<PresetWorkoutDto> {
        return api.presets().presets
    }

    suspend fun loadToday(): TodayWorkoutResponse {
        return api.today()
    }

    suspend fun loadRecentHistory(): WorkoutHistoryResponse {
        return api.recentHistory()
    }

    suspend fun deleteSession(sessionId: Long): TodayWorkoutResponse {
        return api.deleteSession(sessionId)
    }

    suspend fun loadMyWeightKg(): Double {
        return api.myWeight().kg
    }

    suspend fun loadWeeklyProgress(weekOffset: Int = 0): WorkoutWeeklyProgressDto {
        return api.weeklyProgress(weekOffset = weekOffset.coerceIn(0, MAX_PROGRESS_WEEK_OFFSET))
    }

    private companion object {
        const val MAX_PROGRESS_WEEK_OFFSET = 5
    }
}
