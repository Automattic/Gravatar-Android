package com.gravatar.app.homeUi.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun GravatarAvatarWithShadow(
    url: String,
    borderShape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    val colorStops = remember {
        arrayOf(
            0.0f to Color.Black.copy(0.3f),
            0.6f to Color.Black.copy(alpha = 0f)
        )
    }

    var loaded by remember {
        mutableStateOf(false)
    }

    val finalModifier = remember(loaded, borderShape) {
        if (loaded) {
            modifier
                .clip(borderShape)
                .background(Brush.linearGradient(colorStops = colorStops))
                .padding(1.dp)
                .shadow(1.dp, borderShape)
        } else {
            modifier
                .padding(1.dp)
                .clip(borderShape)
        }
    }

    Box(
        modifier = finalModifier
    ) {
        AsyncImageWithCachePlaceholder(url, modifier.clip(borderShape), onLoadedState = {
            loaded = it
        })
    }
}
