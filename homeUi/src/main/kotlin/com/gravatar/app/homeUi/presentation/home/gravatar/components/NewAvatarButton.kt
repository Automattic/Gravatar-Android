package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R

@Composable
fun NewAvatarButton(
    label: String,
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 10.dp,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = true,
                onClick = onClick,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 12.dp, start = 10.dp, end = 10.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
            )
            BasicText(
                text = label,
                autoSize = TextAutoSize.StepBased(
                    maxFontSize = MaterialTheme.typography.labelMedium.fontSize
                ),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NewAvatarButtonPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(10.dp)) {
            NewAvatarButton(
                label = "Camera",
                iconRes = R.drawable.ic_camera,
                contentDescription = "Add new avatar",
                onClick = { }
            )
        }
    }
}
