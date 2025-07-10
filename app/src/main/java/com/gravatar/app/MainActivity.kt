package com.gravatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gravatar.analytics.Tracker
import com.gravatar.app.analytics.AppEvent
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.navigation.RootNavigation
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val tracker: Tracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GravatarAppTheme {
                RootNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Remove this lines when we have the first real event to track.
        tracker.userId = "hamorillo"
        tracker.trackEvent(AppEvent.Test)
        tracker.flush()
    }
}
