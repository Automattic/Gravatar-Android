package com.gravatar.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteAccountStatus(
    @Json(name = "status") val status: String = "disabled",
)