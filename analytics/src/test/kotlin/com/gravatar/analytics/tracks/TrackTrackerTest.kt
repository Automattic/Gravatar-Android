package com.gravatar.analytics.tracks

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Event
import com.gravatar.analytics.TrackerSetupData
import com.gravatar.analytics.TrackerSetupDataProvider
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

class TrackTrackerTest {

    private lateinit var tracker: TracksTracker
    private lateinit var mockClient: TracksClient
    private lateinit var applicationScope: CoroutineScope
    private lateinit var mutableSetupData: MutableStateFlow<TrackerSetupData>

    @Before
    fun setUp() {
        mockClient = mockk<TracksClient>(relaxed = true)
        applicationScope = CoroutineScope(Dispatchers.Unconfined)
        mutableSetupData = MutableStateFlow(TrackerSetupData())
        val provider: TrackerSetupDataProvider = object : TrackerSetupDataProvider {
            override fun getTrackerSetupData(): Flow<TrackerSetupData> = mutableSetupData
        }
        tracker = TracksTracker(provider, applicationScope, mockClient)
    }

    @Test
    fun `when trackEvent is invoked without userId then call track on client with ANON`() {
        val event = object : Event {
            override val name: String = "test_event"
        }

        tracker.trackEvent(event)

        verify {
            mockClient.track(
                "${TracksTracker.TRACKS_EVENT_NAME_PREFIX}${event.name}",
                any(),
                any(),
                TracksClient.NosaraUserType.ANON
            )
        }
    }

    @Test
    fun `when trackEvent is invoked with userId then call track on client with GRAVATAR`() {
        val event = object : Event {
            override val name: String = "test_event_with_user"
        }
        // Update the shared state to include a userId so the tracker switches to WPCOM
        mutableSetupData.value = TrackerSetupData(userId = "someUserId")

        tracker.trackEvent(event)

        verify {
            mockClient.track(
                "${TracksTracker.TRACKS_EVENT_NAME_PREFIX}${event.name}",
                any(),
                "someUserId",
                TracksClient.NosaraUserType.WPCOM
            )
        }
    }

    @Test
    fun `when flush is invoked then call flush on client`() {
        tracker.flush()

        verifySequence {
            mockClient.flush()
        }
    }

    @Test
    fun `when TrackingState changes then trackEvent behavior updates accordingly`() {
        val event = object : Event {
            override val name: String = "test_event_tracking_state"
        }

        // Initially ENABLED by default, should track
        tracker.trackEvent(event)
        verify(exactly = 1) { mockClient.track(any(), any(), any(), any()) }

        // Disable tracking: should not track further events
        mutableSetupData.value = TrackerSetupData(trackingState = com.gravatar.analytics.TrackingState.DISABLED)
        tracker.trackEvent(event)
        // Still only one call so far
        verify(exactly = 1) { mockClient.track(any(), any(), any(), any()) }

        // Re-enable tracking: should resume tracking
        mutableSetupData.value = TrackerSetupData(trackingState = com.gravatar.analytics.TrackingState.ENABLED)
        tracker.trackEvent(event)
        verify(exactly = 2) { mockClient.track(any(), any(), any(), any()) }
    }
}
