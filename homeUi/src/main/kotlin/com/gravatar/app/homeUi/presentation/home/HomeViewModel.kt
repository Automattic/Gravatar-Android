package com.gravatar.app.homeUi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.networkmonitor.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class HomeViewModel(
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        networkMonitor.observe()
            .onEach { networkState ->
                _uiState.update { currentState ->
                    currentState.copy(networkState = networkState)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(homeEvent: HomeEvent) {
        when (homeEvent) {
            is HomeEvent.ShowBottomBar -> {
                _uiState.update { currentState ->
                    currentState.copy(showBottomBar = homeEvent.show)
                }
            }
        }
    }
}
