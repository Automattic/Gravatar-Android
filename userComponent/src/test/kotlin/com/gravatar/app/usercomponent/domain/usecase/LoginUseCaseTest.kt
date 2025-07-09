package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var loginUseCase: LoginUseCase
    private val authRepository = mockk<AuthRepository>()
    private val profileRepository = mockk<ProfileRepository>()
    private val userSessionPersistence = mockk<UserSessionPersistence>()

    private val testLoginRequest = LoginRequest(
        code = "test-code",
        clientSecret = "test-client-secret",
        redirectUri = "test-redirect-uri",
        clientId = "test-client-id"
    )

    @Before
    fun setup() {
        loginUseCase = LoginUseCase(
            authRepository = authRepository,
            profileRepository = profileRepository,
            userSessionPersistence = userSessionPersistence,
        )
        coEvery { userSessionPersistence.set(any()) } returns Unit
    }

    @Test
    fun `login should return success when authRepository login succeeds`() = runTest {
        // Given
        val loginResult = Result.success(Unit)
        coEvery { authRepository.login(testLoginRequest) } returns loginResult
        coEvery { profileRepository.refreshUserProfile() } returns Result.success(Unit)

        // When
        val result = loginUseCase.invoke(testLoginRequest)

        // Then
        assertTrue(result.isSuccess)
        coVerify { authRepository.login(testLoginRequest) }
        coVerify { profileRepository.refreshUserProfile() }
        coVerify { userSessionPersistence.set(UserSession.LOGGED_IN) }
    }

    @Test
    fun `login should return failure when authRepository login fails`() = runTest {
        // Given
        val loginResult = Result.failure<Unit>(RuntimeException("Login failed"))
        coEvery { authRepository.login(testLoginRequest) } returns loginResult

        // When
        val result = loginUseCase.invoke(testLoginRequest)

        // Then
        assertTrue(result.isFailure)
        coVerify { authRepository.login(testLoginRequest) }
        coVerify(exactly = 0) { profileRepository.refreshUserProfile() }
        coVerify(exactly = 0) { userSessionPersistence.set(UserSession.LOGGED_IN) }
    }

    @Test
    fun `login should return success when refresh profile fails`() = runTest {
        // Given
        val loginResult = Result.success(Unit)
        coEvery { authRepository.login(testLoginRequest) } returns loginResult
        coEvery {
            profileRepository.refreshUserProfile()
        } returns Result.failure(IllegalStateException("Profile refresh failed"))

        // When
        val result = loginUseCase.invoke(testLoginRequest)

        // Then
        assertTrue(result.isSuccess)
        coVerify { authRepository.login(testLoginRequest) }
        coVerify { profileRepository.refreshUserProfile() }
        coVerify { userSessionPersistence.set(UserSession.LOGGED_IN) }
    }
}
