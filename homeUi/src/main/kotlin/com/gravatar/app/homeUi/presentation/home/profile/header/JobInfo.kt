package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
internal fun JobInfo(jobInfo: String, modifier: Modifier = Modifier) {
    BasicText(
        text = jobInfo,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            maxFontSize = 14.sp
        ),
        modifier = modifier
    )
}
