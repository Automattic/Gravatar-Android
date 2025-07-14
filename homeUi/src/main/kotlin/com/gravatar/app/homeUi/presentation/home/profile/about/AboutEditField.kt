package com.gravatar.app.homeUi.presentation.home.profile.about

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun AboutEditField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    description: String? = null,
) {
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
            textStyle = MaterialTheme.typography.bodyLarge,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = maxLines,
            minLines = maxLines,
            singleLine = maxLines == 1,
            enabled = enabled,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp))
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
    MaterialTheme {
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
internal fun AboutEditFieldDescriptionPreview() {
    MaterialTheme {
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
