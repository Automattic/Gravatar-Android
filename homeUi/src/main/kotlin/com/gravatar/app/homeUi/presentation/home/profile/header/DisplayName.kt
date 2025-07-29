package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
internal fun DisplayName(displayName: String, modifier: Modifier = Modifier) {
    BasicText(
        text = displayName,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            maxFontSize = 20.sp
        ),
        modifier = modifier
    )
}
