package com.gravatar.app

import com.gravatar.analytics.TrackerSetupData
import com.gravatar.analytics.TrackerSetupDataProvider
import com.gravatar.analytics.TrackingState
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetPrivacySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AppTrackerSetupDataProvider(
    private val getPrivacySettings: GetPrivacySettings,
    private val userRepository: UserRepository,
) : TrackerSetupDataProvider {
    override fun getTrackerSetupData(): Flow<TrackerSetupData> {
        val userIdFlow: Flow<String?> = userRepository.getProfile().map { it?.userLogin }.distinctUntilChanged()
        return getPrivacySettings()
            .combine(userIdFlow) { privacySettings, userId ->
                TrackerSetupData(
                    trackingState = if (privacySettings.analyticsEnabled) {
                        TrackingState.ENABLED
                    } else {
                        TrackingState.DISABLED
                    },
                    userId = userId
                )
            }
    }
}
