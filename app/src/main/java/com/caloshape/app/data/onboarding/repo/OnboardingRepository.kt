package com.caloshape.app.data.onboarding.repo

import com.caloshape.app.data.onboarding.api.OnboardingApi
import com.caloshape.app.data.onboarding.api.OnboardingBootstrapDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val api: OnboardingApi,
) {
    suspend fun bootstrap(): OnboardingBootstrapDto {
        return api.bootstrap()
    }
}