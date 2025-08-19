package com.gravatar.app.design.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.gravatar.analytics.Tracker
import com.gravatar.app.design.analytics.DesignEvent
import org.koin.compose.koinInject

@Composable
fun Screen(
    screenName: String,
    appearanceLightStatusBars: Boolean = !isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val tracker = koinInject<Tracker>()
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? android.app.Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.isAppearanceLightStatusBars = appearanceLightStatusBars
    }

    // Track when the screen becomes visible or is no longer visible
    DisposableEffect(screenName) {
        tracker.trackEvent(DesignEvent.ScreenView(screenName))

        onDispose {
            tracker.trackEvent(DesignEvent.ScreenLeave(screenName))
        }
    }

    content()
}
