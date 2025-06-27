package com.gravatar.app.homeUi.presentation.home.profile.about

internal enum class AboutInputField(
    private val section: Section
) {
    FIRST_NAME(Section.NAME),
    LAST_NAME(Section.NAME),
    DISPLAY_NAME(Section.NAME),
    PRONUNCIATION(Section.NAME),
    PRONOUNS(Section.NAME),
    JOB_TITLE(Section.PROFESSIONAL),
    COMPANY(Section.PROFESSIONAL),
    ABOUT_ME(Section.ABOUT),
    LOCATION(Section.ABOUT),
    CELL_PHONE(Section.CONTACT),
    CONTACT_EMAIL(Section.CONTACT);

    internal val isName: Boolean
        get() = section == Section.NAME

    internal val isProfessional: Boolean
        get() = section == Section.PROFESSIONAL

    internal val isAbout: Boolean
        get() = section == Section.ABOUT

    internal val isContact: Boolean
        get() = section == Section.CONTACT

    internal val order: Int
        get() = when (this) {
            FIRST_NAME -> 0
            LAST_NAME -> 1
            DISPLAY_NAME -> 2
            PRONUNCIATION -> 3
            PRONOUNS -> 4
            JOB_TITLE -> 100
            COMPANY -> 101
            ABOUT_ME -> 200
            LOCATION -> 201
            CELL_PHONE -> 300
            CONTACT_EMAIL -> 301
        }
}
