package com.gravatar.crashlogging

import com.automattic.android.tracks.crashlogging.CrashLoggingDataProvider
import com.automattic.android.tracks.crashlogging.CrashLoggingUser
import com.automattic.android.tracks.crashlogging.EventLevel
import com.automattic.android.tracks.crashlogging.ExtraKnownKey
import com.automattic.android.tracks.crashlogging.PerformanceMonitoringConfig
import com.automattic.android.tracks.crashlogging.ReleaseName
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

internal class GravatarCrashLoggingDataProvider(
    localeProvider: LocaleProvider,
    userRepository: UserRepository
) : CrashLoggingDataProvider {

    override val applicationContextProvider = emptyFlow<Map<String, String>>()

    override val buildType = BuildConfig.BUILD_TYPE

    override val enableCrashLoggingLogs = BuildConfig.DEBUG

    override val locale = localeProvider.provideLocale()

    override val performanceMonitoringConfig = PerformanceMonitoringConfig.Disabled

    override val releaseName = if (BuildConfig.DEBUG) {
        ReleaseName.SetByApplication(DEBUG_RELEASE_NAME)
    } else {
        ReleaseName.SetByTracksLibrary
    }

    override val sentryDSN = BuildConfig.SENTRY_DSN

    override val user = userRepository.getProfile().map { profile ->
        CrashLoggingUser(
            userID = profile?.userId?.toString(),
            email = profile?.hash,
            username = profile?.displayName,
        )
    }

    override fun crashLoggingEnabled() = true

    override fun extraKnownKeys() = emptyList<ExtraKnownKey>()

    override fun provideExtrasForEvent(
        currentExtras: Map<ExtraKnownKey, String>,
        eventLevel: EventLevel
    ) = emptyMap<ExtraKnownKey, String>()

    override fun shouldDropWrappingException(
        module: String,
        type: String,
        value: String
    ) = false

    companion object {
        const val DEBUG_RELEASE_NAME = "debug"
    }
}
