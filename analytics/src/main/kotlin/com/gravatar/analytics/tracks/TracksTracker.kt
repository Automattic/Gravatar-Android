package com.gravatar.analytics.tracks

import android.content.Context
import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.Tracker

internal class TracksTracker(context: Context) : Tracker {

    private val tracksClient = TracksClient.getClient(context)

    override fun trackEvent(event: Event) {
        // TODO: We should add the userId to the event if available and set the user type accordingly.
        tracksClient.track(event.name, null, TracksClient.NosaraUserType.ANON)
        tracksClient.flush()
    }
}