package com.gravatar.app.usercomponent.domain.usecase

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class IsUserLoggedInUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase
    private val authRepository = mockk<AuthRepository>()

    @Before
    fun setup() {
        isUserLoggedInUseCase = IsUserLoggedInUseCase(
            authRepository = authRepository
        )
    }

    @Test
    fun `invoke should return flow from authRepository isUserLoggedIn when user is logged in`() = runTest {
        // Given
        val isLoggedInFlow = flowOf(true, false)
        every { authRepository.isUserLoggedIn() } returns isLoggedInFlow

        // When
        isUserLoggedInUseCase().test {
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }
}
