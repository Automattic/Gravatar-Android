package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.Tracker
import com.gravatar.analytics.TrackerSetupDataProvider
import com.gravatar.analytics.TrackingState
import com.gravatar.analytics.asJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Provided
import java.util.UUID

internal class TracksTracker(
    @Provided trackerSetupDataProvider: TrackerSetupDataProvider,
    @Provided applicationScope: CoroutineScope,
    private val tracksClient: TracksClient,
) : Tracker() {

    internal companion object {
        const val TRACKS_EVENT_NAME_PREFIX = "gravatarandroid_"
    }

    init {
        trackerSetupDataProvider.getTrackerSetupData()
            .onEach {
                trackingState = it.trackingState
                userId = it.userId
            }
            .launchIn(applicationScope)
    }

    private var userId: String? = null
    private val anonId: String = generateNewAnonID()
    private var trackingState: TrackingState = TrackingState.ENABLED

    override fun trackEvent(event: Event) {
        if (trackingState == TrackingState.DISABLED) return

        val userType = userId?.let {
            TracksClient.NosaraUserType.WPCOM
        } ?: TracksClient.NosaraUserType.ANON
        tracksClient.track(
            "${TRACKS_EVENT_NAME_PREFIX}${event.name}",
            event.properties.asJson(),
            userId ?: anonId,
            userType
        )
    }

    override fun flush() {
        tracksClient.flush()
    }
}

private fun generateNewAnonID(): String {
    // Generate a new UUID and return it as a string.
    return UUID.randomUUID().toString()
}
