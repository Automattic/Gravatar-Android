package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.app.homeUi.presentation.home.components.AsyncImageWithCachePlaceholder
import com.gravatar.app.homeUi.presentation.home.components.GravatarAvatarWithShadow
import com.gravatar.restapi.models.Profile

private val AVATAR_EXPANDED_SIZE = 104.dp
private val AVATAR_COLLAPSED_SIZE = 44.dp
private val HEADER_HORIZONTAL_PADDING = 16.dp
private val PROFILE_INFO_START_PADDING = 16.dp
private val PROFILE_INFO_TOP_PADDING = 16.dp

@Composable
internal fun AnimatedProfileHeader(
    profile: Profile,
    avatarUrl: String?,
    saveState: ProfileHeaderSaveState,
    onSaveProfile: () -> Unit,
    scrollPosition: Float, // 0f = fully expanded, 1f = fully collapsed
    modifier: Modifier = Modifier
) {
    when (saveState) {
        ProfileHeaderSaveState.SAVED -> {
            AnimatedProfileHeaderSavedState(scrollPosition, modifier, avatarUrl, profile)
        }
        ProfileHeaderSaveState.SAVING,
        ProfileHeaderSaveState.UNSAVED -> {
            ProfileHeader(profile, avatarUrl, saveState, onSaveProfile, modifier)
        }
    }
}

@Composable
private fun AnimatedProfileHeaderSavedState(
    scrollPosition: Float,
    modifier: Modifier,
    avatarUrl: String?,
    profile: Profile
) {
    // Avatar animations
    val avatarSize by animateDpAsState(
        targetValue = lerp(AVATAR_EXPANDED_SIZE, AVATAR_COLLAPSED_SIZE, scrollPosition),
        label = "avatarSize"
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Avatar animations
    val avatarOffset by animateDpAsState(
        targetValue = lerp(
            (screenWidth / 2) - (AVATAR_EXPANDED_SIZE / 2) - HEADER_HORIZONTAL_PADDING,
            0.dp,
            scrollPosition
        ),
        label = "avatarOffset"
    )

    val density = LocalDensity.current
    var displayNameWidth by remember { mutableStateOf(0.dp) }
    var displayNameHeight by remember { mutableStateOf(0.dp) }
    val displayNameXOffset by animateDpAsState(
        targetValue = lerp(
            (screenWidth / 2 - (displayNameWidth / 2) - HEADER_HORIZONTAL_PADDING),
            AVATAR_COLLAPSED_SIZE + PROFILE_INFO_START_PADDING,
            scrollPosition
        ),
        label = "infoXOffset"
    )
    val displayNameYOffset by animateDpAsState(
        targetValue = lerp(AVATAR_EXPANDED_SIZE + PROFILE_INFO_TOP_PADDING, 0.dp, scrollPosition),
        label = "infoYOffset"
    )

    var jobInfoWidth by remember { mutableStateOf(0.dp) }
    val jobInfoXOffset by animateDpAsState(
        targetValue = lerp(
            (screenWidth / 2 - (jobInfoWidth / 2) - HEADER_HORIZONTAL_PADDING),
            AVATAR_COLLAPSED_SIZE + PROFILE_INFO_START_PADDING,
            scrollPosition
        ),
        label = "infoXOffset"
    )
    val jobInfoYOffset by animateDpAsState(
        targetValue = lerp(
            AVATAR_EXPANDED_SIZE + displayNameHeight + PROFILE_INFO_TOP_PADDING,
            displayNameHeight,
            scrollPosition
        ),
    )

    Box(
        modifier
            .fillMaxWidth()
    ) {
        // Background image with blur
        AsyncImageWithCachePlaceholder(
            avatarUrl.orEmpty(),
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 40.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
                .alpha(0.7f)
        )

        // Content container with animated layout
        Box(
            modifier = Modifier
                .padding(16.dp)
                .systemBarsPadding()
                .fillMaxWidth()
        ) {
            // Avatar with animated position
            Box(
                modifier = Modifier.offset(avatarOffset, 0.dp)
            ) {
                GravatarAvatarWithShadow(
                    url = avatarUrl.orEmpty(),
                    borderShape = CircleShape,
                    modifier = Modifier.size(avatarSize)
                )
            }
            BasicText(
                text = profile.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    maxFontSize = 18.sp
                ),
                modifier = Modifier
                    .padding(
                        start = displayNameXOffset.coerceAtLeast(0.dp),
                        top = displayNameYOffset.coerceAtLeast(0.dp)
                    )
                    .onGloballyPositioned { coordinates ->
                        displayNameWidth = with(density) { coordinates.size.width.toDp() }
                        displayNameHeight = with(density) { coordinates.size.height.toDp() }
                    },
            )

            profile.jobInfo().takeIf { it.isNotBlank() }?.let { jobInfo ->
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
                    modifier = Modifier
                        .padding(start = jobInfoXOffset.coerceAtLeast(0.dp), top = jobInfoYOffset.coerceAtLeast(0.dp))
                        .onGloballyPositioned { coordinates ->
                            jobInfoWidth = with(density) { coordinates.size.width.toDp() }
                        },
                )
            }
        }
    }
}

// Helper function for linear interpolation
private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + fraction * (end - start)
}

private fun lerp(start: Dp, end: Dp, fraction: Float): Dp {
    return Dp(lerp(start.value, end.value, fraction))
}

private fun Profile.jobInfo(): String {
    return buildString {
        if (jobTitle.isNotBlank()) {
            append(jobTitle)
        }
        if (company.isNotBlank()) {
            if (isNotEmpty()) append(", ")
            append(company)
        }
    }
}
