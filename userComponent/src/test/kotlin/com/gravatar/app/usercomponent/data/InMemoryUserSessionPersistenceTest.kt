package com.gravatar.app.usercomponent.data

import app.cash.turbine.test
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.extensions.defaultProfile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InMemoryUserSessionPersistenceTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var userSessionPersistence: InMemoryUserSessionPersistence
    private val profileRepository = mockk<ProfileRepository>()
    private val applicationScope = CoroutineScope(testDispatcher)
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    private val profile = defaultProfile(
        hash = "hash",
    )

    @Test
    fun `initial state should be LOGGED_IN when user is logged in`() = runTest {
        // Given
        coEvery { profileRepository.get() } returns flow { emit(profile) }

        // When
        userSessionPersistence = InMemoryUserSessionPersistence(
            profileRepository = profileRepository,
            applicationScope = applicationScope,
            dispatcherProvider = testDispatcherProvider
        )

        // Then
        userSessionPersistence.state.test {
            assertEquals(UserSession.LOGGED_IN, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state should be LOGGED_OUT when user is not logged in`() = runTest {
        // Given
        coEvery { profileRepository.get() } returns flow { emit(null) }

        // When
        userSessionPersistence = InMemoryUserSessionPersistence(
            profileRepository = profileRepository,
            applicationScope = applicationScope,
            dispatcherProvider = testDispatcherProvider
        )

        // Then
        userSessionPersistence.state.test {
            assertEquals(UserSession.LOGGED_OUT, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `set should update the state`() = runTest {
        // Given
        coEvery { profileRepository.get() } returns flow { emit(null) }

        userSessionPersistence = InMemoryUserSessionPersistence(
            profileRepository = profileRepository,
            applicationScope = applicationScope,
            dispatcherProvider = testDispatcherProvider
        )

        // When & Then
        userSessionPersistence.state.test {
            // Initial state
            assertEquals(UserSession.LOGGED_OUT, awaitItem())

            // Set new state
            userSessionPersistence.set(UserSession.LOGGED_IN)
            assertEquals(UserSession.LOGGED_IN, awaitItem())

            // Set another state
            userSessionPersistence.set(UserSession.LOGGED_OUT)
            assertEquals(UserSession.LOGGED_OUT, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
