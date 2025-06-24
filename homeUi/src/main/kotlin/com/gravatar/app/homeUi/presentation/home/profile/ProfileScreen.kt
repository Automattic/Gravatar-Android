package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            uiState.profile.let { profile ->
                if (profile != null) {
                    Text(profile.displayName)
                } else {
                    Text("There was an error retrieving the profile.")
                }
            }
        }
    }
}
