package com.gravatar.app.homeUi.presentation.home.gravatar.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.gravatar.AvatarUi
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.components.ComponentState
import java.net.URL
import com.gravatar.ui.components.atomic.Avatar as AtomicAvatar

internal val avatarSize = 88.dp
private val cornerRadius = 8.dp

@Composable
internal fun SelectableAvatar(
    avatar: AvatarUi,
    size: Dp,
    modifier: Modifier,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    onFailedAvatarClicked: ((Uri) -> Unit)? = null,
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

        is AvatarUi.Local -> {
            SelectableAvatar(
                imageUrl = avatar.uri.toString(),
                isSelected = false,
                loadingState = avatar.loadingState,
                modifier = modifier,
                onFailedAvatarClicked = { onFailedAvatarClicked?.invoke(avatar.uri) }
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
    onFailedAvatarClicked: (() -> Unit)? = null,
) {
    var moreOptionsPopupVisible by remember { mutableStateOf(false) }
    val animatedAlpha: Float by animateFloatAsState(
        targetValue = if (moreOptionsPopupVisible) 0.7f else 1f,
        animationSpec = tween(
            durationMillis = 120,
        )
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (moreOptionsPopupVisible) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 120,
        )
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer(
                alpha = animatedAlpha,
                scaleX = animatedScale,
                scaleY = animatedScale,
                transformOrigin = TransformOrigin.Center
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        3.dp,
                        MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(cornerRadius)
                    )
                } else {
                    Modifier
                },
            )
            .clickable {
                when (loadingState) {
                    AvatarLoadingState.Failure -> onFailedAvatarClicked?.invoke()
                    AvatarLoadingState.Loading -> Unit
                    AvatarLoadingState.None -> {
                        moreOptionsPopupVisible = true
                    }
                }
            },
    ) {
        AtomicAvatar(
            state = ComponentState.Loaded(imageUrl),
            size = avatarSize,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
        )
        when (loadingState) {
            AvatarLoadingState.None -> LoadedOverlay(isSelected)
            AvatarLoadingState.Loading -> LoadingOverlay()
            is AvatarLoadingState.Failure -> FailureOverlay()
        }
        if (moreOptionsPopupVisible) {
            AvatarMoreOptionsPickerPopup(
                isSelected = isSelected,
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

private fun AvatarUi.Uploaded.imageUrlWithSize(sizePx: Int): String {
    return avatar.imageUrl.toURL()
        ?.let { url ->
            URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
        }?.toString() ?: avatar.imageUrl.toString()
}

@Composable
private fun BoxScope.LoadedOverlay(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = null,
            modifier = modifier
                .padding(10.dp)
                .size(22.dp)
                .border(1.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(2.dp)
                .align(Alignment.BottomEnd),
            tint = Color.White,
        )
    }
}

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
private fun FailureOverlay(modifier: Modifier = Modifier) {
    Overlay(modifier) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = stringResource(R.string.gravatar_tab_failed_to_load_avatar_content_description),
            tint = Color.White,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
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

    data object Failure : AvatarLoadingState()
}

private val AvatarUi.Uploaded.loadingState: AvatarLoadingState
    get() = if (isLoading) AvatarLoadingState.Loading else AvatarLoadingState.None

private val AvatarUi.Local.loadingState: AvatarLoadingState
    get() = when {
        isLoading -> AvatarLoadingState.Loading
        else -> AvatarLoadingState.Failure
    }

@Preview
@Composable
private fun SelectableAvatarNotSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        modifier = Modifier.size(avatarSize),
        loadingState = AvatarLoadingState.None,
    )
}

@Preview
@Composable
private fun SelectableAvatarSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = true,
        modifier = Modifier
            .size(avatarSize),
        loadingState = AvatarLoadingState.None,
    )
}
