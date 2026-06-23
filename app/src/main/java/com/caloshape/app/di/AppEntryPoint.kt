package com.caloshape.app.di

import com.caloshape.app.data.account.repo.AccountRepository
import com.caloshape.app.data.auth.repo.AuthRepository
import com.caloshape.app.data.auth.repo.TokenStore
import com.caloshape.app.data.auth.state.AuthState
import com.caloshape.app.data.entitlement.EntitlementSyncer
import com.caloshape.app.data.foodlog.repo.FoodLogsRepository
import com.caloshape.app.data.onboarding.repo.OnboardingRepository
import com.caloshape.app.data.profile.repo.AutoGoalsRepository
import com.caloshape.app.data.profile.repo.UserProfileStore
import com.caloshape.app.data.profile.repo.ProfileRepository
import com.caloshape.app.data.weight.repo.WeightRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 讓非 ViewModel / 非 @AndroidEntryPoint 的類別，也能從 Hilt 取出單例。
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun authRepository(): AuthRepository
    fun authState(): AuthState
    fun tokenStore(): TokenStore
    fun profileRepository(): ProfileRepository
    fun userProfileStore(): UserProfileStore
    fun weightRepository(): WeightRepository
    fun autoGoalsRepository(): AutoGoalsRepository
    fun foodLogsRepository(): FoodLogsRepository
    fun accountRepository(): AccountRepository
    fun entitlementSyncer(): EntitlementSyncer
    fun onboardingRepository(): OnboardingRepository
}
