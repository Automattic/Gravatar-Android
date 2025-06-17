package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class TrackTrackerTest {

    @Test
    fun `when trackEvent is invoked then call track on client`() {
        val mockClient = mockk<TracksClient>(relaxed = true)
        val tracker = TracksTracker(mockClient)
        tracker.userId = "test_user_id"
        val event = object : Event {
            override val name: String = "test_event"
        }

        tracker.trackEvent(event)

        verify { mockClient.track(event.name, "test_user_id", TracksClient.NosaraUserType.ANON) }
    }

    @Test
    fun `when flush is invoked then call flush on client`() {
        val mockClient = mockk<TracksClient>(relaxed = true)
        val tracker = TracksTracker(mockClient)

        tracker.flush()

        verify(exactly = 1) { mockClient.flush() }
    }
}
