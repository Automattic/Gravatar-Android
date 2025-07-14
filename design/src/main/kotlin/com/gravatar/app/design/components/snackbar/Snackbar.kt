package com.gravatar.app.design.components.snackbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GravatarSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
    ) { snackbarData ->
        val snackbarVisuals = snackbarData.visuals as? GravatarSnackbarVisuals
        val containerColor =
            snackbarVisuals?.snackbarType?.containerColor ?: MaterialTheme.colorScheme.inverseSurface
        val contentColor =
            snackbarVisuals?.snackbarType?.contentColor ?: MaterialTheme.colorScheme.inverseOnSurface
        Snackbar(
            snackbarData = snackbarData,
            containerColor = containerColor,
            dismissActionContentColor = contentColor,
            actionContentColor = contentColor,
            contentColor = contentColor,
            actionColor = contentColor,
        )
    }
}

suspend fun SnackbarHostState.showGravatarSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    snackbarType: SnackbarType = SnackbarType.Info,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
): SnackbarResult {
    val visuals = GravatarSnackbarVisuals(
        message = message,
        withDismissAction = withDismissAction,
        actionLabel = actionLabel,
        duration = duration,
        snackbarType = snackbarType,
    )

    return showSnackbar(visuals)
}

internal data class GravatarSnackbarVisuals(
    val snackbarType: SnackbarType,
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    override val message: String,
    override val withDismissAction: Boolean,
) : SnackbarVisuals

internal val SnackbarType.containerColor: Color
    @Composable get() = when (this) {
        SnackbarType.Info -> MaterialTheme.colorScheme.inverseSurface
        SnackbarType.Error -> MaterialTheme.colorScheme.errorContainer
    }

internal val SnackbarType.contentColor: Color
    @Composable get() = when (this) {
        SnackbarType.Info -> MaterialTheme.colorScheme.inverseOnSurface
        SnackbarType.Error -> MaterialTheme.colorScheme.onErrorContainer
    }

enum class SnackbarType {
    Info,
    Error,
}
