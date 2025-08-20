package com.gravatar.app.design.analytics

import com.gravatar.analytics.Event

sealed class DesignEvent : Event {

    data class ScreenView(val screen: String) : DesignEvent() {
        override val name: String = "screen_view"
        override val properties: Map<String, Any> = mapOf(
            "screen" to screen,
        )
    }

    data class ScreenLeave(val screen: String) : DesignEvent() {
        override val name: String = "screen_leave"
        override val properties: Map<String, Any> = mapOf(
            "screen" to screen,
        )
    }
}
