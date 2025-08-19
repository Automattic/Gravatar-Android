package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.Tracker
import java.util.UUID

internal class TracksTracker(private val tracksClient: TracksClient) : Tracker() {

    internal companion object {
        const val TRACKS_EVENT_NAME_PREFIX = "gravatarandroid_"
    }

    override var userId: String? = null
    private val anonId: String = generateNewAnonID()

    override fun trackEvent(event: Event) {
        val userType = userId?.let {
            TracksClient.NosaraUserType.WPCOM
        } ?: TracksClient.NosaraUserType.ANON
        val props = event.properties?.toJson()
        tracksClient.track("${TRACKS_EVENT_NAME_PREFIX}${event.name}", props, userId ?: anonId, userType)
    }

    override fun flush() {
        tracksClient.flush()
    }
}

private fun generateNewAnonID(): String {
    // Generate a new UUID and return it as a string.
    return UUID.randomUUID().toString()
}
