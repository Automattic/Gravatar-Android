package com.gravatar.app

import android.app.Application
import com.automattic.android.tracks.crashlogging.CrashLogging
import com.gravatar.app.di.appModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GravatarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GravatarApplication)
            modules(appModule)
        }

        val crashLogging: CrashLogging = get()
        crashLogging.initialize()
    }
}
