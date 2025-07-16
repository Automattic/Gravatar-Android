package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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
    headerState: AnimatedProfileHeaderState,
    modifier: Modifier = Modifier
) {
    if (saveState == ProfileHeaderSaveState.SAVED) {
        AnimatedProfileHeaderSavedState(headerState, modifier, avatarUrl, profile)
    } else {
        ProfileHeader(profile, avatarUrl, saveState, onSaveProfile, modifier)
    }
}

@Composable
private fun AnimatedProfileHeaderSavedState(
    headerState: AnimatedProfileHeaderState,
    modifier: Modifier,
    avatarUrl: String?,
    profile: Profile
) {
    val density = LocalDensity.current

    val avatarSize by animateDpAsState(
        targetValue = lerp(AVATAR_EXPANDED_SIZE, AVATAR_COLLAPSED_SIZE, headerState.expansionFraction),
        label = "avatarSize"
    )
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }

    val avatarOffset by animateDpOffsetAsState(
        targetValue = remember(screenWidth, headerState.expansionFraction) {
            DpOffset(
                x = lerp(
                    (screenWidth / 2) - (AVATAR_EXPANDED_SIZE / 2) - HEADER_HORIZONTAL_PADDING,
                    0.dp,
                    headerState.expansionFraction
                ),
                y = 0.dp
            )
        },
        label = "avatarOffset"
    )

    var displayNameSize by remember(profile.displayName) { mutableStateOf(DpSize.Zero) }
    val displayNameOffset by animateDpOffsetAsState(
        targetValue = DpOffset(
            x = lerp(
                (screenWidth / 2 - (displayNameSize.width / 2) - HEADER_HORIZONTAL_PADDING),
                AVATAR_COLLAPSED_SIZE + PROFILE_INFO_START_PADDING,
                headerState.expansionFraction
            ),
            y = lerp(
                AVATAR_EXPANDED_SIZE + PROFILE_INFO_TOP_PADDING,
                0.dp,
                headerState.expansionFraction
            )
        ),
        label = "displayNameOffset"
    )

    var jobInfoSize by remember { mutableStateOf(DpSize.Zero) }
    val jobInfoOffset by animateDpOffsetAsState(
        targetValue = DpOffset(
            x = lerp(
                (screenWidth / 2 - (jobInfoSize.width / 2) - HEADER_HORIZONTAL_PADDING),
                AVATAR_COLLAPSED_SIZE + PROFILE_INFO_START_PADDING,
                headerState.expansionFraction
            ),
            y = lerp(
                AVATAR_EXPANDED_SIZE + displayNameSize.height + PROFILE_INFO_TOP_PADDING,
                displayNameSize.height,
                headerState.expansionFraction
            )
        ),
        label = "jobInfoOffset"
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
                modifier = Modifier.offset(avatarOffset.x, avatarOffset.y)
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
                    .padding(start = displayNameOffset.x, top = displayNameOffset.y)
                    .onGloballyPositioned { coordinates ->
                        displayNameSize = coordinates.size.toDpSize(density)
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
                        .padding(start = jobInfoOffset.x, top = jobInfoOffset.y)
                        .onGloballyPositioned { coordinates ->
                            jobInfoSize = coordinates.size.toDpSize(density)
                        },
                )
            }
        }
    }
}

class AnimatedProfileHeaderState(
    initialExpansionFraction: Float
) {
    companion object {
        internal const val MAX_EXPANSION_FRACTION = 0f
        internal const val MIN_EXPANSION_FRACTION = 1f

        internal val COLLAPSED = AnimatedProfileHeaderState(MIN_EXPANSION_FRACTION)
        internal val EXPANDED = AnimatedProfileHeaderState(MAX_EXPANSION_FRACTION)
    }

    var expansionFraction by mutableFloatStateOf(initialExpansionFraction)
        private set

    fun updateExpansion(fraction: Float) {
        expansionFraction = fraction.coerceIn(
            MAX_EXPANSION_FRACTION,
            MIN_EXPANSION_FRACTION
        )
    }
}

// Use in the composable
@Composable
fun rememberAnimatedProfileHeaderState(
    initialExpansionFraction: Float = AnimatedProfileHeaderState.MAX_EXPANSION_FRACTION
): AnimatedProfileHeaderState {
    return remember { AnimatedProfileHeaderState(initialExpansionFraction) }
}

private fun IntSize.toDpSize(density: Density): DpSize {
    return DpSize(
        width = with(density) { width.toDp() },
        height = with(density) { height.toDp() },
    )
}

/**
 * Animates a [DpOffset] to a target value.
 *
 * @param targetValue The target value to animate to.
 * @param animationSpec The animation spec to configure the animation.
 * @param label An optional label for the animation, useful for debugging.
 * @return A [State] object that holds the animated [DpOffset] value.
 */
@Composable
fun animateDpOffsetAsState(
    targetValue: DpOffset,
    animationSpec: AnimationSpec<Dp> = SpringSpec(),
    label: String = "DpOffsetAnimation"
): State<DpOffset> {
    val xOffset = animateDpAsState(
        targetValue = targetValue.x,
        animationSpec = animationSpec,
        label = "$label-X"
    )
    val yOffset = animateDpAsState(
        targetValue = targetValue.y,
        animationSpec = animationSpec,
        label = "$label-Y"
    )

    return remember(xOffset, yOffset) {
        derivedStateOf { DpOffset(xOffset.value, yOffset.value) }
    }
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
