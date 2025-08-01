package com.gravatar.app.homeUi.presentation.home

import app.cash.turbine.test
import com.gravatar.app.networkmonitor.NetworkMonitor
import com.gravatar.app.networkmonitor.NetworkState
import com.gravatar.app.testUtils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val networkStateFlow = MutableSharedFlow<NetworkState>(replay = 1)
    private val networkMonitor: NetworkMonitor = object : NetworkMonitor {
        override fun observe() = networkStateFlow
    }

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(networkMonitor)
    }

    @Test
    fun `when viewmodel is initialized then uiState has default values`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(HomeUiState(), initialState)
            assertEquals(false, initialState.noInternetBannerVisible)
        }
    }

    @Test
    fun `when network state changes to CONNECTED then noInternetBannerVisible is false`() = runTest {
        // Given
        viewModel = HomeViewModel(networkMonitor)

        // When
        networkStateFlow.emit(NetworkState.CONNECTED)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(NetworkState.CONNECTED, state.networkState)
            assertEquals(false, state.noInternetBannerVisible)
        }
    }

    @Test
    fun `when network state changes to DISCONNECTED then noInternetBannerVisible is true`() = runTest {
        // Given
        viewModel = HomeViewModel(networkMonitor)

        // When
        networkStateFlow.emit(NetworkState.DISCONNECTED)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(NetworkState.DISCONNECTED, state.networkState)
            assertEquals(true, state.noInternetBannerVisible)
        }
    }

    @Test
    fun `when ShowBottomBar event is received then showBottomBar state is updated accordingly`() = runTest {
        // Given
        viewModel = HomeViewModel(networkMonitor)

        // When - Hide bottom bar
        viewModel.onEvent(HomeEvent.ShowBottomBar(show = false))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.showBottomBar)
        }

        // When - Show bottom bar
        viewModel.onEvent(HomeEvent.ShowBottomBar(show = true))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(true, state.showBottomBar)
        }
    }
}
