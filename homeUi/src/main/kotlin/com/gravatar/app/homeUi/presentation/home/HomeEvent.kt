package com.gravatar.app.homeUi.presentation.home

internal sealed class HomeEvent {
    data class ShowBottomBar(val show: Boolean) : HomeEvent()
}
