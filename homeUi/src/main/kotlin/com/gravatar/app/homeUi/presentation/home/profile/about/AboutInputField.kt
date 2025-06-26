package com.gravatar.app.homeUi.presentation.home.profile.about

/**
 * Represents a set of fields that can be shown in the "About" section.
 *
 * @property value The value of the about input field.
 */
internal class AboutInputField(
    val value: String,
) {
    internal companion object {
        /**
         * The user’s display name.
         */
        val DisplayName: AboutInputField = AboutInputField("display_name")

        /**
         * A short biography or description about the user.
         */
        val AboutMe: AboutInputField = AboutInputField("about_me")

        /**
         * A phonetic pronunciation of the user’s name.
         */
        val Pronunciation: AboutInputField = AboutInputField("pronunciation")

        /**
         * The pronouns the user identifies with (e.g., she/her, they/them).
         */
        val Pronouns: AboutInputField = AboutInputField("pronouns")

        /**
         * The user's geographic location.
         */
        val Location: AboutInputField = AboutInputField("location")

        /**
         * The user's current job title or role.
         */
        val JobTitle: AboutInputField = AboutInputField("job_title")

        /**
         * The company or organization the user is affiliated with.
         */
        val Company: AboutInputField = AboutInputField("company")

        /**
         * User's first name. This is only provided in authenticated API requests.
         */
        val FirstName: AboutInputField = AboutInputField("first_name")

        /**
         * User's last name. This is only provided in authenticated API requests.
         */
        val LastName: AboutInputField = AboutInputField("last_name")

        /**
         * A convenience set representing all possible about info fields.
         */
        val all: Set<AboutInputField> = setOf(
            FirstName,
            LastName,
            DisplayName,
            Pronunciation,
            Pronouns,
            JobTitle,
            Company,
            AboutMe,
            Location,
        )

        /**
         * A subset of fields that are related to the user's name.
         */
        val name: Set<AboutInputField> = setOf(
            FirstName,
            LastName,
            DisplayName,
            Pronunciation,
            Pronouns,
        )

        /**
         * A subset of fields that are professional or work-related.
         */
        val professional: Set<AboutInputField> = setOf(
            JobTitle,
            Company,
        )

        /**
         * A convenience set representing about fields.
         */
        val about: Set<AboutInputField> = setOf(
            AboutMe,
            Location,
        )
    }

    internal val isName: Boolean
        get() = name.contains(this)

    internal val isProfessional: Boolean
        get() = professional.contains(this)

    internal val isAbout: Boolean
        get() = about.contains(this)

    internal val order: Int
        get() = when (this) {
            FirstName -> 0
            LastName -> 1
            DisplayName -> 2
            Pronunciation -> 3
            Pronouns -> 4
            JobTitle -> 100
            Company -> 101
            AboutMe -> 200
            Location -> 201
            else -> -1
        }
}
