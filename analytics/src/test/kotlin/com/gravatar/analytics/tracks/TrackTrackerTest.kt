package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TrackTrackerTest {

    private lateinit var tracker: TracksTracker
    private lateinit var mockClient: TracksClient

    @Before
    fun setUp() {
        mockClient = mockk<TracksClient>(relaxed = true)
        tracker = TracksTracker(mockClient)
    }

    @Test
    fun `when trackEvent is invoked then call track on client`() {
        tracker.userId = "test_user_id"
        val event = object : Event {
            override val name: String = "test_event"
        }

        tracker.trackEvent(event)

        verify { mockClient.track(event.name, "test_user_id", TracksClient.NosaraUserType.ANON) }
    }

    @Test
    fun `when flush is invoked then call flush on client`() {
        tracker.flush()

        verify(exactly = 1) { mockClient.flush() }
    }
}
