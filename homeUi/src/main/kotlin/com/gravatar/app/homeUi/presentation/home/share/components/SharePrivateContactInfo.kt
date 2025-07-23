package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R

@Composable
internal fun SharePrivateContactInfo(
    // Email state and callbacks
    emailValue: String,
    onEmailValueChange: (String) -> Unit,
    emailSwitchChecked: Boolean,
    onEmailSwitchCheckedChange: (Boolean) -> Unit,
    // Phone number state and callbacks
    phoneValue: String,
    onPhoneValueChange: (String) -> Unit,
    phoneSwitchChecked: Boolean,
    onPhoneSwitchCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
        ShareSectionTitle(
            leftIcon = R.drawable.share_section_title_warning,
            title = R.string.share_tab_private_contact_info_title,
            rightIcon = R.drawable.share_section_title_lock,
            modifier = Modifier.fillMaxWidth()
        )
        ShareEditableField(
            placeholder = R.string.share_tab_private_contact_email_placeholder,
            value = emailValue,
            onValueChange = onEmailValueChange,
            switchChecked = emailSwitchChecked,
            onSwitchCheckedChange = onEmailSwitchCheckedChange,
        )
        ShareEditableField(
            placeholder = R.string.share_tab_private_contact_phone_number_placeholder,
            value = phoneValue,
            onValueChange = onPhoneValueChange,
            switchChecked = phoneSwitchChecked,
            onSwitchCheckedChange = onPhoneSwitchCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharePrivateContactInfoPreview() {
    SharePrivateContactInfo(
        emailValue = "example@email.com",
        onEmailValueChange = {},
        emailSwitchChecked = true,
        onEmailSwitchCheckedChange = {},
        phoneValue = "123-456-7890",
        onPhoneValueChange = {},
        phoneSwitchChecked = false,
        onPhoneSwitchCheckedChange = {}
    )
}
