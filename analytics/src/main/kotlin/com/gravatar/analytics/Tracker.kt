package com.gravatar.analytics

abstract class Tracker {
    abstract var userId: String?
    abstract fun trackEvent(event: Event)
    abstract fun flush()
}
