package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp

@Composable
internal fun MeasureComposableHeight(
    content: @Composable () -> Unit,
    onHeightMeasured: (Dp) -> Unit
) {
    SubcomposeLayout { constraints ->
        // First, measure the content without placing it
        val placeable = subcompose("measurePass") {
            content()
        }.map { it.measure(constraints) }

        // Calculate the height
        val height = placeable.maxByOrNull { it.height }?.height ?: 0

        // Convert to Dp and report the height
        onHeightMeasured(with(density) { height.toDp() })

        // Return an empty layout with zero size
        layout(0, 0) {}
    }
}
