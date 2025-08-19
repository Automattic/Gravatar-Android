package com.gravatar.analytics

import org.json.JSONObject

interface Event {
    val name: String
    val properties: Map<String, Any>
        get() = emptyMap()
}

internal fun Map<String, Any>.asJson(): JSONObject {
    return JSONObject().apply {
        this@asJson.forEach { (key, value) ->
            put(key, value)
        }
    }
}
