package com.gravatar.app.networkmonitor

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

internal class AndroidNetworkMonitor(
    context: Context,
    private val applicationScope: CoroutineScope,
) : NetworkMonitor {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val state = MutableStateFlow<Set<Network>>(emptySet())

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                state.update { currentState ->
                    currentState.filter { it != network }.toSet()
                }
            }

            override fun onUnavailable() {
                state.update { currentState ->
                    emptySet()
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, capabilities)
                state.update { currentState ->
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        (currentState + network).toSet()
                    } else {
                        currentState.filter { it != network }.toSet()
                    }
                }
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun observe(): Flow<NetworkState> {
        return state
            .map { networks ->
                if (networks.isNotEmpty()) {
                    NetworkState.CONNECTED
                } else {
                    NetworkState.DISCONNECTED
                }
            }
            .distinctUntilChanged()
            .shareIn(
                scope = applicationScope,
                replay = 1,
                started = SharingStarted.WhileSubscribed()
            )
    }
}
