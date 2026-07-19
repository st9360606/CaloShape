package com.caloshape.app.data.activity.util

import kotlin.math.roundToInt


object ActivityKcalEstimator {
    private const val COEFF = 0.0005

    fun estimateActiveKcal(weightKg: Double, steps: Long): Int {
        if (weightKg <= 0.0 || steps <= 0L) return 0
        return (weightKg * steps.toDouble() * COEFF).roundToInt()
    }
}
