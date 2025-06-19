package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
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
    fun `when trackEvent is invoked without userId then call track on client with ANON`() {
        val event = object : Event {
            override val name: String = "test_event"
        }

        tracker.trackEvent(event)

        verify { mockClient.track(event.name, any(), TracksClient.NosaraUserType.ANON) }
    }

    @Test
    fun `when trackEvent is invoked with userId then call track on client with GRAVATAR`() {
        val event = object : Event {
            override val name: String = "test_event_with_user"
        }
        tracker.userId = "someUserId"

        tracker.trackEvent(event)

        verify { mockClient.track(event.name, "someUserId", TracksClient.NosaraUserType.WPCOM) }
    }

    @Test
    fun `when flush is invoked then call flush on client`() {
        tracker.flush()

        verifySequence {
            mockClient.flush()
        }
    }
}
