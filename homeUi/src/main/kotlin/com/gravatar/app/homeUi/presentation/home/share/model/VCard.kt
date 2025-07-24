package com.gravatar.app.homeUi.presentation.home.share.model

internal class VCard private constructor(
    val firstName: String? = null,
    val lastName: String? = null,
    val nickname: String? = null,
    val organization: String? = null,
    val title: String? = null,
    val profileUrl: String? = null,
    val note: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
) {

    override fun toString(): String {
        val contentBuilder = StringBuilder().append("BEGIN:VCARD\n")
            .append("VERSION:3.0\n")
            .append("PRODID:Gravatar Android\n")

        val firstName = firstName.orEmpty()
        val lastName = lastName.orEmpty()
        val nickname = nickname.orEmpty()
        if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            contentBuilder.append("N:$lastName;$firstName;;;\n")
                .append("FN:${("$firstName $lastName".trim()).ifEmpty { nickname }}\n")
        }
        val calculatedNickname = nickname.ifEmpty { "$firstName $lastName".trim() }

        calculatedNickname.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("NICKNAME:$it\n") }
        organization?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("ORG:$it\n") }
        title?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("TITLE:$it\n") }
        profileUrl?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("URL:$it\n") }
        note?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("NOTE:$it\n") }
        phoneNumber?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("TEL;TYPE=cell:$it\n") }
        email?.takeIf { it.isNotEmpty() }?.let { contentBuilder.append("EMAIL:$it\n") }

        contentBuilder.append("END:VCARD")
        return contentBuilder.toString()
    }

    class Builder(
        private var firstName: String? = null,
        private var lastName: String? = null,
        private var nickname: String? = null,
        private var organization: String? = null,
        private var title: String? = null,
        private var profileUrl: String? = null,
        private var note: String? = null,
        private var phoneNumber: String? = null,
        private var email: String? = null,
    ) {
        fun firstName(firstName: String?) = apply { this.firstName = firstName }
        fun lastName(lastName: String?) = apply { this.lastName = lastName }
        fun nickname(nickname: String?) = apply { this.nickname = nickname }
        fun organization(organization: String?) = apply { this.organization = organization }
        fun title(title: String?) = apply { this.title = title }
        fun profileUrl(url: String?) = apply { this.profileUrl = url }
        fun note(description: String?) = apply { this.note = description }
        fun phoneNumber(phone: String?) = apply { this.phoneNumber = phone }
        fun email(email: String?) = apply { this.email = email }

        fun build() = VCard(
            firstName = firstName,
            lastName = lastName,
            nickname = nickname,
            organization = organization,
            title = title,
            profileUrl = profileUrl,
            note = note,
            phoneNumber = phoneNumber,
            email = email
        )
    }
}
