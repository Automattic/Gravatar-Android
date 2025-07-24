package com.gravatar.app.usercomponent.domain.model

data class UserSharePreferences(
    val name: Boolean,
    val location: Boolean,
    val title: Boolean,
    val organization: Boolean,
    val description: Boolean,
    val profileUrl: Boolean,
)
