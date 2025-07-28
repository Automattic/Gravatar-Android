package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R

@Composable
internal fun ShareEditableField(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        Switch(
            checked = switchChecked,
            onCheckedChange = onSwitchCheckedChange,
        )
    }
}

@Preview
@Composable
private fun ShareEditableInfoPreview() {
    ShareEditableField(
        placeholder = R.string.share_tab_private_contact_email_placeholder,
        value = "gravatar@a8c.com",
        onValueChange = {},
        switchChecked = true,
        onSwitchCheckedChange = {}
    )
}

@Preview
@Composable
private fun ShareEditableInfoEmptyPreview() {
    ShareEditableField(
        placeholder = R.string.share_tab_private_contact_phone_number_placeholder,
        value = "",
        onValueChange = {},
        switchChecked = false,
        onSwitchCheckedChange = {}
    )
}
