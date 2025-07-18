package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R

@Composable
fun NewAvatarButton(
    label: String,
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    Surface(
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.primary,
        color = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        },
        shape = shape,
        modifier = modifier
            .then(
                if (isSystemInDarkTheme()) {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = shape
                    )
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 14.dp, start = 10.dp, end = 10.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(32.dp)
            )
            BasicText(
                text = label,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxFontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun NewAvatarButtonPreview() {
    GravatarAppTheme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp)
        ) {
            NewAvatarButton(
                label = "Camera",
                iconRes = R.drawable.ic_camera,
                contentDescription = "Add new avatar",
                onClick = { }
            )
        }
    }
}
