package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.share.PrivateContactState

@Composable
internal fun SharePrivateContactInfo(
    privateContactState: PrivateContactState,
    onEmailValueChange: (String) -> Unit,
    onEmailSwitchCheckedChange: (Boolean) -> Unit,
    onPhoneValueChange: (String) -> Unit,
    onPhoneSwitchCheckedChange: (Boolean) -> Unit,
    onTitleClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
        ShareSectionTitle(
            accessoryIcon = R.drawable.share_section_title_warning,
            title = R.string.share_tab_private_contact_info_title,
            icon = R.drawable.share_section_title_lock,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onTitleClicked()
                },
        )
        ShareEditableField(
            placeholder = R.string.share_tab_private_contact_email_placeholder,
            value = privateContactState.emailValue,
            onValueChange = onEmailValueChange,
            switchChecked = privateContactState.isEmailShared,
            keyboardType = KeyboardType.Email,
            onSwitchCheckedChange = onEmailSwitchCheckedChange,
        )
        ShareEditableField(
            placeholder = R.string.share_tab_private_contact_phone_number_placeholder,
            value = privateContactState.phoneValue,
            onValueChange = onPhoneValueChange,
            switchChecked = privateContactState.isPhoneShared,
            keyboardType = KeyboardType.Phone,
            onSwitchCheckedChange = onPhoneSwitchCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharePrivateContactInfoPreview() {
    SharePrivateContactInfo(
        privateContactState = PrivateContactState(
            emailValue = "example@email.com",
            isEmailShared = true,
            phoneValue = "123-456-7890",
            isPhoneShared = false
        ),
        onEmailValueChange = {},
        onEmailSwitchCheckedChange = {},
        onPhoneValueChange = {},
        onPhoneSwitchCheckedChange = {},
        onTitleClicked = {},
    )
}
