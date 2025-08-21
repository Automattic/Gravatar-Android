package com.gravatar.analytics

import kotlinx.coroutines.flow.Flow

interface TrackerSetupDataProvider {
    fun getTrackerSetupData(): Flow<TrackerSetupData>
}
