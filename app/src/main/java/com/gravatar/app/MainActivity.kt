package com.gravatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gravatar.analytics.Tracker
import com.gravatar.app.analytics.AppEvent
import com.gravatar.app.navigation.RootNavigation
import com.gravatar.app.ui.theme.GravatarTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val tracker: Tracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tracker.trackEvent(AppEvent.Test)

        setContent {
            GravatarTheme {
                RootNavigation()
            }
        }
    }
}
