package com.gravatar.app.design.analytics

import com.gravatar.analytics.Event
import org.json.JSONObject

sealed class DesignEvent : Event {

    data class ScreenView(val screen: String) : DesignEvent() {
        override val name: String = "screen_view"
        override val properties: Event.Properties? = ScreenViewProperties(screen)
    }

    data class ScreenLeave(val screen: String) : DesignEvent() {
        override val name: String = "screen_leave"
        override val properties: Event.Properties? = ScreenViewProperties(screen)
    }
}

private class ScreenViewProperties(
    val screen: String
) : Event.Properties {
    override fun toJson(): JSONObject {
        return JSONObject().apply {
            put("screen", screen)
        }
    }
}
