package com.gravatar.app.networkmonitor

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {

    fun observe(): Flow<NetworkState>
}

enum class NetworkState {
    CONNECTED,
    DISCONNECTED,
}
