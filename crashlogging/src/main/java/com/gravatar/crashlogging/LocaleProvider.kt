package com.gravatar.crashlogging

import java.util.Locale

internal fun interface LocaleProvider {
    fun provideLocale(): Locale?
}
