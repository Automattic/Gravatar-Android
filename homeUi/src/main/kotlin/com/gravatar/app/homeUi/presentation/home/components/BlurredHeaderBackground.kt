package com.gravatar.app.homeUi.presentation.home.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun BlurredHeaderBackground(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier) {
        AsyncImageWithCachePlaceholder(
            avatarUrl,
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 80.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = scrimAlpha))
        )
        content()
    }
}

private val scrimAlpha = if (Build.VERSION.SDK_INT >= 32) {
    0.15f
} else {
    0.6f
}
