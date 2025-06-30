package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.presentation.home.gravatar.AvatarUi
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.atomic.Avatar
import java.net.URL

internal val avatarSize = 96.dp
private val cornerRadius = 8.dp

@Composable
internal fun SelectableAvatar(
    avatar: AvatarUi,
    size: Dp,
    modifier: Modifier,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
) {
    when (avatar) {
        is AvatarUi.Uploaded -> {
            val sizePx = with(LocalDensity.current) { size.roundToPx() }
            SelectableAvatar(
                imageUrl = avatar.imageUrlWithSize(sizePx),
                isSelected = avatar.isSelected,
                loadingState = avatar.loadingState,
                modifier = modifier,
                onAvatarOptionClicked = { onAvatarOptionClicked(avatar.avatar, it) },
            )
        }
    }
}

@Composable
private fun SelectableAvatar(
    imageUrl: String,
    isSelected: Boolean,
    loadingState: AvatarLoadingState,
    modifier: Modifier = Modifier,
    onAvatarOptionClicked: ((AvatarOption) -> Unit)? = null,
) {
    var moreOptionsPopupVisible by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable {
                moreOptionsPopupVisible = true
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        4.dp,
                        MaterialTheme.colorScheme.onSurface,
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
        when (loadingState) {
            AvatarLoadingState.None -> {}
            AvatarLoadingState.Loading -> LoadingOverlay()
        }
        if (moreOptionsPopupVisible) {
            AvatarMoreOptionsPickerPopup(
                anchorAlignment = Alignment.Start,
                offset = DpOffset(0.dp, 10.dp),
                onDismissRequest = { moreOptionsPopupVisible = false },
                onAvatarOptionClicked = { avatarOption ->
                    moreOptionsPopupVisible = false
                    onAvatarOptionClicked?.let { it(avatarOption) }
                },
            )
        }
    }
}

private fun AvatarUi.Uploaded.imageUrlWithSize(sizePx: Int) = avatar.imageUrl.toURL()?.let { url ->
    URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
}.toString()

@Composable
private fun LoadingOverlay(modifier: Modifier = Modifier) {
    Overlay(modifier) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(30.dp),
            strokeWidth = 2.dp,
            color = Color.White,
        )
    }
}

@Composable
private fun Overlay(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cornerRadius),
            ),
    ) {
        content()
    }
}

internal sealed class AvatarLoadingState {
    data object None : AvatarLoadingState()

    data object Loading : AvatarLoadingState()
}

private val AvatarUi.Uploaded.loadingState: AvatarLoadingState
    get() = if (isLoading) AvatarLoadingState.Loading else AvatarLoadingState.None

@Preview
@Composable
private fun SelectableAvatarNotSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        modifier = Modifier.size(150.dp),
        loadingState = AvatarLoadingState.None,
    )
}

@Preview
@Composable
private fun SelectableAvatarSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = true,
        modifier = Modifier.size(150.dp),
        loadingState = AvatarLoadingState.None,
    )
}
