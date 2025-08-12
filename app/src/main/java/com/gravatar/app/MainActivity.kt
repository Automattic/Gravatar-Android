package com.gravatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.automattic.android.tracks.crashlogging.CrashLogging
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.navigation.RootNavigation
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val crashLogging: CrashLogging = get()
        crashLogging.sendReport(exception = Exception("Dev test"), message = "This is just a test")

        setContent {
            GravatarAppTheme {
                RootNavigation()
            }
        }
    }
}
