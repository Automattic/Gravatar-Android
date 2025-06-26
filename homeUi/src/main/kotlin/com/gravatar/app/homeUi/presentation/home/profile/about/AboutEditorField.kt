package com.gravatar.app.homeUi.presentation.home.profile.about

internal data class AboutEditorField(
    val type: AboutInputField,
    val value: String,
    val maxLines: Int = 1,
)
