package com.gravatar.app.usercomponent.domain.model

data class UserSharePreferences(
    val privateEmail: Boolean,
    val privatePhone: Boolean,
    val name: Boolean,
    val location: Boolean,
    val title: Boolean,
    val organization: Boolean,
    val description: Boolean,
    val profileUrl: Boolean,
) {
    companion object {
        val Default = UserSharePreferences(
            privateEmail = true,
            privatePhone = true,
            name = true,
            location = true,
            title = true,
            organization = true,
            description = true,
            profileUrl = true
        )
    }
}
