package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.share.PrivateContactInfo

@Composable
internal fun SharePrivateContactInfo(
    privateContactInfo: PrivateContactInfo,
    onEmailValueChange: (String) -> Unit,
    onEmailSwitchCheckedChange: (Boolean) -> Unit,
    onPhoneValueChange: (String) -> Unit,
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
            value = privateContactInfo.emailValue,
            onValueChange = onEmailValueChange,
            switchChecked = privateContactInfo.isEmailShared,
            onSwitchCheckedChange = onEmailSwitchCheckedChange,
        )
        ShareEditableField(
            placeholder = R.string.share_tab_private_contact_phone_number_placeholder,
            value = privateContactInfo.phoneValue,
            onValueChange = onPhoneValueChange,
            switchChecked = privateContactInfo.isPhoneShared,
            onSwitchCheckedChange = onPhoneSwitchCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharePrivateContactInfoPreview() {
    SharePrivateContactInfo(
        privateContactInfo = PrivateContactInfo(
            emailValue = "example@email.com",
            isEmailShared = true,
            phoneValue = "123-456-7890",
            isPhoneShared = false
        ),
        onEmailValueChange = {},
        onEmailSwitchCheckedChange = {},
        onPhoneValueChange = {},
        onPhoneSwitchCheckedChange = {}
    )
}
