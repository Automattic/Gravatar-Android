package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.AsyncImageWithCachePlaceholder
import com.gravatar.app.homeUi.presentation.home.components.GravatarAvatarWithShadow

val GRAVATAR_HEADER_COLLAPSED_HEIGHT = 2 * 16.dp + 44.dp
private val CIRCLE_AVATAR_SIZE_EXPANDED = 105.dp
private val CONTAINER_PADDING = 16.dp

@OptIn(ExperimentalMotionApi::class)
@Composable
fun GravatarHeader(
    avatarUrl: String?,
    progress: Float,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val avatarUrl = avatarUrl.orEmpty()

    val expandedHeight = CIRCLE_AVATAR_SIZE_EXPANDED + CONTAINER_PADDING * 2 + with(LocalDensity.current) {
        WindowInsets.statusBars.getBottom(this).toDp()
    }

    val motionScene = remember {
        context.resources.openRawResource(R.raw.gravatar_header_motion_scene)
            .readBytes()
            .decodeToString()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        AsyncImageWithCachePlaceholder(
            avatarUrl,
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 40.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
                .alpha(0.7f)
        )
        MotionLayout(
            motionScene = MotionScene(content = motionScene),
            progress = progress,
            modifier = modifier
                .systemBarsPadding()
                .fillMaxWidth()
                .height(expandedHeight)
        ) {
            // Main circular avatar
            GravatarAvatarWithShadow(
                url = avatarUrl,
                borderShape = CircleShape,
                modifier = Modifier
                    .layoutId("avatar1")
            )

            // Secondary square avatar
            GravatarAvatarWithShadow(
                url = avatarUrl,
                borderShape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .layoutId("avatar2")
            )

            // Menu button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .layoutId("menuButton")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.more_button),
                    contentDescription = stringResource(R.string.gravatar_tab_header_more_options),
                )
            }
        }
    }
}

@Preview
@Composable
fun GravatarHeaderMotionCollapsedPreview() {
    GravatarHeader(
        avatarUrl = "https://gravatar.com/avatar/test",
        progress = 0f,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
fun GravatarHeaderMotionHalfExpandedPreview() {
    GravatarHeader(
        avatarUrl = "https://gravatar.com/avatar/test",
        progress = 0.5f,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
fun GravatarHeaderMotionPreview() {
    GravatarHeader(
        avatarUrl = "https://gravatar.com/avatar/test",
        progress = 1.0f,
        modifier = Modifier.fillMaxWidth(),
    )
}
