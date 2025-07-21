package com.gravatar.app.networkmonitor.di

import com.gravatar.app.networkmonitor.AndroidNetworkMonitor
import com.gravatar.app.networkmonitor.NetworkMonitor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkMonitorModule = module {
    singleOf(::AndroidNetworkMonitor) { bind<NetworkMonitor>() }
}
