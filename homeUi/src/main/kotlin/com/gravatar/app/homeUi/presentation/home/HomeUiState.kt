package com.gravatar.app.homeUi.presentation.home

import com.gravatar.app.networkmonitor.NetworkState

internal data class HomeUiState(
    val networkState: NetworkState? = null,
) {

    val noInternetBannerVisible: Boolean = networkState == NetworkState.DISCONNECTED
}
