package com.caloshape.app.data.foodlog.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodLogPortionMultiplierRequestDto(
    val multiplier: Int,
    val reason: String? = null
)
