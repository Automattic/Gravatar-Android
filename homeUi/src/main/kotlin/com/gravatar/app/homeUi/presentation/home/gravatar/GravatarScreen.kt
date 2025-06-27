package com.gravatar.app.homeUi.presentation.home.gravatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun GravatarScreen() {
    val scope = rememberCoroutineScope()
    val authRepository = koinInject<AuthRepository>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Gravatar Screen")
        Button(
            onClick = {
                scope.launch {
                    authRepository.logout()
                }
            }
        ) {
            Text("Log out")
        }
    }
}
