package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.presentation.home.gravatar.AvatarUi
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.atomic.Avatar
import java.net.URL

internal val avatarSize = 96.dp

@Composable
internal fun SelectableAvatar(
    avatar: AvatarUi,
    size: Dp,
    modifier: Modifier,
) {
    when (avatar) {
        is AvatarUi.Uploaded -> {
            val sizePx = with(LocalDensity.current) { size.roundToPx() }
            SelectableAvatar(
                imageUrl = avatar.imageUrlWithSize(sizePx),
                isSelected = avatar.isSelected,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SelectableAvatar(
    imageUrl: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 8.dp
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isSelected) {
                    Modifier.border(
                        4.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(cornerRadius)
                    )
                } else {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceDim,
                        RoundedCornerShape(cornerRadius),
                    )
                },
            ),
    ) {
        Avatar(
            state = ComponentState.Loaded(imageUrl),
            size = avatarSize,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
        )
    }
}

private fun AvatarUi.Uploaded.imageUrlWithSize(sizePx: Int) = avatar.imageUrl.toURL()?.let { url ->
    URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
}.toString()

@Preview
@Composable
private fun SelectableAvatarNotSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        modifier = Modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun SelectableAvatarSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = true,
        modifier = Modifier.size(150.dp),
    )
}
