package com.gravatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gravatar.app.navigation.RootNavigation
import com.gravatar.app.ui.theme.GravatarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GravatarTheme {
                RootNavigation()
            }
        }
    }
}
