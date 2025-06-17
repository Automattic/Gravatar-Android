package com.gravatar.app.analytics

import com.gravatar.analytics.Event

sealed class AppEvent : Event {

    data object Test : AppEvent() {
        override val name: String = "gravatar_android_test"
    }
}
