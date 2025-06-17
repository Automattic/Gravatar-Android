package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.Tracker

internal class TracksTracker(private val tracksClient: TracksClient) : Tracker {

    override fun trackEvent(event: Event) {
        // We should add the userId to the event if available and set the user type accordingly.
        tracksClient.track(event.name, "Gravatar", TracksClient.NosaraUserType.ANON)
    }

    override fun flush() {
        tracksClient.flush()
    }
}
