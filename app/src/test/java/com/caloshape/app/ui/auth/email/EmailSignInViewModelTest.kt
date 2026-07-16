package com.caloshape.app.ui.auth.email

import com.caloshape.app.data.auth.repo.EmailAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
class EmailSignInViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `code input keeps only first six digits`() = runTest(dispatcher) {
        val viewModel = EmailSignInViewModel(mockk(relaxed = true))
        viewModel.prepareCode("person@example.com")

        viewModel.onCodeChange("12a345678")

        assertEquals("123456", viewModel.code.value?.code)
        advanceUntilIdle()
    }

    @Test
    fun `verify ignores incomplete code`() = runTest(dispatcher) {
        val repository = mockk<EmailAuthRepository>(relaxed = true)
        val viewModel = EmailSignInViewModel(repository)
        viewModel.prepareCode("person@example.com")
        viewModel.onCodeChange("12345")

        viewModel.verify { error("Incomplete OTP must not succeed") }
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.verify(any(), any()) }
    }

    @Test
    fun `verify does not submit twice while request is in flight`() = runTest(dispatcher) {
        val repository = mockk<EmailAuthRepository>()
        val responseGate = CompletableDeferred<Unit>()
        coEvery { repository.verify(any(), any()) } coAnswers { responseGate.await() }
        val viewModel = EmailSignInViewModel(repository)
        viewModel.prepareCode("person@example.com")
        viewModel.onCodeChange("123456")

        viewModel.verify { }
        runCurrent()
        viewModel.verify { }
        runCurrent()

        coVerify(exactly = 1) { repository.verify("person@example.com", "123456") }

        responseGate.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun `send code does not submit twice while request is in flight`() = runTest(dispatcher) {
        val repository = mockk<EmailAuthRepository>()
        val responseGate = CompletableDeferred<Boolean>()
        coEvery { repository.start(any()) } coAnswers { responseGate.await() }
        val viewModel = EmailSignInViewModel(repository)
        viewModel.onEmailChange("person@example.com")

        viewModel.sendCode { }
        runCurrent()
        viewModel.sendCode { }
        runCurrent()

        coVerify(exactly = 1) { repository.start("person@example.com") }

        responseGate.complete(true)
        advanceUntilIdle()
    }

    @Test
    fun `send code maps rate limit to localized error category`() = runTest(dispatcher) {
        val repository = mockk<EmailAuthRepository>()
        val rateLimit = mockk<HttpException>()
        every { rateLimit.code() } returns 429
        coEvery { repository.start(any()) } throws rateLimit
        val viewModel = EmailSignInViewModel(repository)
        viewModel.onEmailChange("person@example.com")

        viewModel.sendCode { error("Rate-limited request must not navigate") }
        advanceUntilIdle()

        assertEquals(EmailCodeError.TOO_MANY_ATTEMPTS, viewModel.enter.value.error)
    }

    @Test
    fun `resend maps rate limit to localized error category`() = runTest(dispatcher) {
        val repository = mockk<EmailAuthRepository>()
        val rateLimit = mockk<HttpException>()
        every { rateLimit.code() } returns 429
        coEvery { repository.start(any()) } throws rateLimit
        val viewModel = EmailSignInViewModel(repository)
        viewModel.prepareCode("person@example.com")
        advanceTimeBy(60_000)
        runCurrent()

        viewModel.resend()
        runCurrent()

        assertEquals(EmailCodeError.TOO_MANY_ATTEMPTS, viewModel.code.value?.error)
    }
}
