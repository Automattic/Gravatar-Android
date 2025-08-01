package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.services.GravatarService
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteUserProfileUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val gravatarService: GravatarService = mockk()
    private val authRepository: AuthRepository = mockk()
    private val logout: Logout = mockk()
    private lateinit var deleteUserProfileUseCase: DeleteUserProfileUseCase

    private val testToken = "test-token"

    @Before
    fun setup() {
        deleteUserProfileUseCase = DeleteUserProfileUseCase(
            gravatarService = gravatarService,
            authRepository = authRepository,
            logout = logout
        )

        // Default mock behavior
        coEvery { logout.invoke() } returns Unit
    }

    @Test
    fun `invoke should return success when user is logged in and profile deletion succeeds`() = runTest {
        // Given
        coEvery { authRepository.getToken() } returns testToken
        coEvery { gravatarService.deleteProfile(testToken) } returns Result.success(Unit)

        // When
        val result = deleteUserProfileUseCase.invoke()

        // Then
        assertTrue(result.isSuccess)
        coVerify { authRepository.getToken() }
        coVerify { gravatarService.deleteProfile(testToken) }
        coVerify { logout.invoke() }
    }

    @Test
    fun `invoke should return failure when user is not logged in`() = runTest {
        // Given
        coEvery { authRepository.getToken() } returns null

        // When
        val result = deleteUserProfileUseCase.invoke()

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)
        coVerify { authRepository.getToken() }
        coVerify(exactly = 0) { gravatarService.deleteProfile(any()) }
        coVerify(exactly = 0) { logout.invoke() }
    }

    @Test
    fun `invoke should return failure when profile deletion fails`() = runTest {
        // Given
        val testException = Exception("Service error")
        coEvery { authRepository.getToken() } returns testToken
        coEvery { gravatarService.deleteProfile(testToken) } returns Result.failure(testException)

        // When
        val result = deleteUserProfileUseCase.invoke()

        // Then
        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
        coVerify { authRepository.getToken() }
        coVerify { gravatarService.deleteProfile(testToken) }
        coVerify(exactly = 0) { logout.invoke() }
    }
}
