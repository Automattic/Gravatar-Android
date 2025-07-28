package com.gravatar.app.usercomponent.domain.model

data class PrivateContactInfo(
    val privateEmail: String,
    val privatePhone: String,
) {
    companion object {
        val Default = PrivateContactInfo(
            privateEmail = "",
            privatePhone = ""
        )
    }
}
