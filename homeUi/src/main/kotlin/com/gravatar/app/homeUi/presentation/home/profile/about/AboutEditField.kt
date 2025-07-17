package com.gravatar.app.homeUi.presentation.home.profile.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme

@Composable
internal fun AboutEditField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    description: String? = null,
    edited: Boolean = false,
) {
    val inputFieldShape = RoundedCornerShape(8.dp)
    var isFocused by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = maxLines,
            minLines = maxLines,
            singleLine = maxLines == 1,
            enabled = enabled,
            onValueChange = onValueChange,
            modifier = Modifier
                .onFocusChanged {
                    isFocused = it.hasFocus
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isFocused) {
                                Modifier
                                    .background(MaterialTheme.colorScheme.surface, inputFieldShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, inputFieldShape)
                            } else {
                                if (edited) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        inputFieldShape
                                    )
                                } else {
                                    Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh, inputFieldShape)
                                }
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    innerTextField()
                }
            },
        )
        if (!description.isNullOrEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditFieldNoDescriptionPreview() {
    GravatarAppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            AboutEditField(
                value = "John Doe",
                description = null,
                enabled = true,
                label = "Display name",
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditFieldEditedPreview() {
    GravatarAppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            AboutEditField(
                value = "John Doe",
                description = null,
                enabled = true,
                label = "Display name",
                edited = true,
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditFieldDescriptionPreview() {
    GravatarAppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            AboutEditField(
                value = """
                    Oceanographer, Filmmaker, and Connoisseur of Red Beanies. 
                    Diving deep in pursuit of underwater wonders. 
                    While oceanographer Steve Zissou is working on his latest,
                """.trimIndent(),
                description = "Brief description for your profile.",
                enabled = true,
                label = "About me",
                maxLines = 4,
                onValueChange = {},
            )
        }
    }
}
