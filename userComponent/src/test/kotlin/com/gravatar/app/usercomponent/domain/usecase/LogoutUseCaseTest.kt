package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LogoutUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var logoutUseCase: LogoutUseCase
    private val authRepository = mockk<AuthRepository>()
    private val profileRepository = mockk<ProfileRepository>()

    @Before
    fun setup() {
        logoutUseCase = LogoutUseCase(
            authRepository = authRepository,
            profileRepository = profileRepository
        )

        // Set up default behavior for mocks
        coEvery { profileRepository.delete() } returns Unit
        coEvery { authRepository.logout() } returns Unit
    }

    @Test
    fun `invoke should call profileRepository delete and authRepository logout in correct order`() = runTest {
        // When
        logoutUseCase.invoke()

        // Then
        coVerify(exactly = 1) { profileRepository.delete() }
        coVerify(exactly = 1) { authRepository.logout() }

        // Verify order of calls
        coVerifySequence {
            profileRepository.delete()
            authRepository.logout()
        }
    }
}
