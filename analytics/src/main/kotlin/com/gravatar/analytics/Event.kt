package com.gravatar.analytics

import org.json.JSONObject

interface Event {
    val name: String
    val properties: Properties?
        get() = null

    interface Properties {
        fun toJson(): JSONObject
    }
}
