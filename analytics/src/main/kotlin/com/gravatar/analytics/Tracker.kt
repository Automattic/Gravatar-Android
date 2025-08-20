package com.gravatar.analytics

abstract class Tracker {
    abstract fun trackEvent(event: Event)
    abstract fun flush()
}
