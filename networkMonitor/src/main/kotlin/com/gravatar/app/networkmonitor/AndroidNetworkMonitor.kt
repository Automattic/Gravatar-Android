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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal class AndroidNetworkMonitor(
    context: Context,
    private val applicationScope: CoroutineScope,
) : NetworkMonitor {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val state = MutableSharedFlow<NetworkState>(replay = 1)

    private val activeNetworks = mutableSetOf<Network>()

    init {
        emitInitialState()
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                activeNetworks.add(network)
                emit(NetworkState.CONNECTED)
            }

            override fun onLost(network: Network) {
                activeNetworks.remove(network)
                if (activeNetworks.isEmpty()) {
                    emit(NetworkState.DISCONNECTED)
                }
            }

            override fun onUnavailable() {
                emit(NetworkState.DISCONNECTED)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, capabilities)
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    emit(NetworkState.CONNECTED)
                } else {
                    emit(NetworkState.DISCONNECTED)
                }
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun emitInitialState() {
        val currentNetwork = connectivityManager.activeNetwork
        val currentCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (currentCapabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true) {
            emit(NetworkState.CONNECTED)
        } else {
            emit(NetworkState.DISCONNECTED)
        }
    }

    override fun observe(): Flow<NetworkState> {
        return state.asSharedFlow()
            .distinctUntilChanged()
    }

    private fun emit(value: NetworkState) {
        applicationScope.launch {
            state.emit(value)
        }
    }
}
