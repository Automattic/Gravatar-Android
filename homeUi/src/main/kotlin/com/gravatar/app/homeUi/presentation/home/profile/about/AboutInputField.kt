package com.gravatar.app.homeUi.presentation.home.profile.about

internal enum class AboutInputField(
    private val section: Section
) {
    FIRST_NAME(Section.ABOUT),
    LAST_NAME(Section.ABOUT),
    DISPLAY_NAME(Section.ABOUT),
    PRONUNCIATION(Section.ABOUT),
    PRONOUNS(Section.ABOUT),
    ABOUT_ME(Section.ABOUT),
    LOCATION(Section.ABOUT),
    JOB_TITLE(Section.PROFESSIONAL),
    COMPANY(Section.PROFESSIONAL);

    internal val isAbout: Boolean
        get() = section == Section.ABOUT

    internal val isProfessional: Boolean
        get() = section == Section.PROFESSIONAL

    internal val order: Int
        get() = when (this) {
            DISPLAY_NAME -> 0
            FIRST_NAME -> 1
            LAST_NAME -> 2
            PRONUNCIATION -> 3
            PRONOUNS -> 4
            LOCATION -> 5
            ABOUT_ME -> 6
            JOB_TITLE -> 100
            COMPANY -> 101
        }
}
