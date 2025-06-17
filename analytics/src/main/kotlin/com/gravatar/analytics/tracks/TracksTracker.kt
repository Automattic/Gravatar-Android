package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.Tracker
import java.util.UUID

internal class TracksTracker(private val tracksClient: TracksClient) : Tracker() {
    override var userId: String? = null
    private val anonId: String = generateNewAnonID()

    override fun trackEvent(event: Event) {
        // We should add GRAVATAR userType when available.
        tracksClient.track(event.name, userId ?: anonId, TracksClient.NosaraUserType.ANON)
    }

    override fun flush() {
        tracksClient.flush()
    }
}

private fun generateNewAnonID(): String {
    // Generate a new UUID and return it as a string.
    return UUID.randomUUID().toString()
}
