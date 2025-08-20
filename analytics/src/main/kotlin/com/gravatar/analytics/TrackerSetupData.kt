package com.gravatar.analytics

data class TrackerSetupData(
    val trackingState: TrackingState = TrackingState.ENABLED,
    val userId: String? = null,
)

enum class TrackingState {
    ENABLED,
    DISABLED,
}
