package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun CollapsibleTopAppBar(
    modifier: Modifier = Modifier,
    minVisibleHeight: Dp = 0.dp,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable () -> Unit,
) {
    // Sets the app bar's height offset to collapse the entire bar's height when content is
    // scrolled.
    var heightOffsetLimit by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(heightOffsetLimit) {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    // Set up support for resizing the top app bar when vertically dragging the bar itself.
    val appBarDragModifier = if (scrollBehavior != null && !scrollBehavior.isPinned) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
            },
            onDragStopped = { velocity ->
                settleAppBar(
                    scrollBehavior.state,
                    velocity,
                    scrollBehavior.flingAnimationSpec,
                    scrollBehavior.snapAnimationSpec
                )
            }
        )
    } else {
        Modifier
    }

    // Compose a Surface with a TopAppBarLayout content.
    // The surface's background color is animated as specified above.
    // The height of the app bar is determined by subtracting the bar's height offset from the
    // app bar's defined constant height value (i.e. the ContainerHeight token).
    val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
    Surface(modifier = modifier.then(appBarDragModifier)) {
        Layout(
            content = content,
            measurePolicy = { measurables, constraints ->
                val placeable = measurables.firstOrNull()?.measure(constraints.copy(minWidth = 0))
                val totalHeight = placeable?.height?.toFloat() ?: 0f
                // Ensure heightOffsetLimit is always negative or zero to avoid invalid range error
                val fullMinHeight = statusBarHeight + minVisibleHeight.toPx()
                heightOffsetLimit = if (totalHeight > fullMinHeight) {
                    (totalHeight - fullMinHeight) * -1
                } else {
                    0f
                }
                val scrollOffset = scrollBehavior?.state?.heightOffset ?: 0f
                val height = totalHeight + scrollOffset
                val layoutHeight = height.roundToInt().coerceAtLeast(fullMinHeight.toInt())
                layout(constraints.maxWidth, layoutHeight) {
                    placeable?.place(0, scrollOffset.toInt())
                }
            }
        )
    }
}

@Suppress("MagicNumber")
@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

/**
 * Calculates the expansion progress of a TopAppBarScrollBehavior.
 * Returns 1.0 when fully expanded and 0.0 when fully collapsed.
 */
@ExperimentalMaterial3Api
@Composable
fun rememberExpansionProgress(state: TopAppBarState): Float {
    return remember(state) {
        derivedStateOf {
            1f - state.collapsedFraction
        }
    }.value
}
