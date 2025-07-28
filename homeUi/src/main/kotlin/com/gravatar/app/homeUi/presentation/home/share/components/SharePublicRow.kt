package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme

@Composable
internal fun SharePublicRow(
    label: String,
    value: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    singleLineValue: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.6f
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (singleLineValue) 1 else Int.MAX_VALUE,
                overflow = if (singleLineValue) TextOverflow.Ellipsis else TextOverflow.Visible,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharePublicRowPreview() {
    GravatarAppTheme {
        SharePublicRow(
            label = "Public Label",
            value = "Public Value",
            checked = true,
            onCheckedChange = {}
        )
    }
}
