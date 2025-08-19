package com.gravatar.crashlogging

import android.app.Application
import androidx.core.os.ConfigurationCompat
import java.util.Locale

internal class ContextBasedLocaleProvider(
    private val context: Application,
) : LocaleProvider {
    override fun provideLocale(): Locale? {
        return ConfigurationCompat.getLocales(context.resources.configuration)[0]
    }
}
