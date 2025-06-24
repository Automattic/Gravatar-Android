package com.gravatar.app.loginUi.presentation.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.app.loginUi.presentation.oauth.OAuthResult
import com.gravatar.app.loginUi.presentation.oauth.OAuthResultContract

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
) {
    val context = LocalContext.current

    val oAuthLauncher = rememberLauncherForActivityResult(OAuthResultContract()) { result ->
        when (result) {
            OAuthResult.DISMISSED -> Unit
            is OAuthResult.TOKEN -> {
                Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                onLoggedIn()
            }

            OAuthResult.ERROR -> {
                Toast.makeText(context, "Login failed. Please try again.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text("Login Screen")
                    Button(
                        onClick = {
                            oAuthLauncher.launch(Unit)
                        }
                    ) {
                        Text("Log In")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLoggedIn = { },
    )
}
