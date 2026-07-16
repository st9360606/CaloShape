package com.caloshape.app.ui.home.ui.settings.model

import com.caloshape.app.data.auth.repo.AuthRepository
import com.caloshape.app.data.auth.repo.LocalUserDataPurger
import com.caloshape.app.data.profile.repo.ProfileRepository
import com.caloshape.app.data.users.repo.UsersRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var usersRepository: UsersRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var localUserDataPurger: LocalUserDataPurger

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        usersRepository = mockk()
        profileRepository = mockk()
        authRepository = mockk()
        localUserDataPurger = mockk(relaxed = true)

        coEvery { usersRepository.meOrNull() } returns null
        coEvery { profileRepository.getServerProfileOrNull() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun logout_whenRemoteLogoutSucceeds_purgesLocalUserData() = runTest(dispatcher) {
        coEvery { authRepository.logoutRemoteThenClear() } returns Result.success(Unit)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { localUserDataPurger.purge() }
    }

    @Test
    fun logout_whenRemoteLogoutFails_keepsLocalUserData() = runTest(dispatcher) {
        coEvery { authRepository.logoutRemoteThenClear() } returns
            Result.failure(IllegalStateException("network"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 0) { localUserDataPurger.purge() }
    }

    private fun createViewModel() = SettingsViewModel(
        usersRepo = usersRepository,
        profileRepo = profileRepository,
        authRepo = authRepository,
        localUserDataPurger = localUserDataPurger
    )
}
