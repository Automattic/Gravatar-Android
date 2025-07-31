package com.gravatar.app.homeUi.presentation.home

import com.gravatar.app.networkmonitor.NetworkState

internal data class HomeUiState(
    val networkState: NetworkState? = null,
    val showBottomBar: Boolean = true,
) {

    val noInternetBannerVisible: Boolean = networkState == NetworkState.DISCONNECTED
}
