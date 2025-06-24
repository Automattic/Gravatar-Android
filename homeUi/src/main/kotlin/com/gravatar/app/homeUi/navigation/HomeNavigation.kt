package com.gravatar.app.homeUi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gravatar.app.homeUi.presentation.home.gravatar.GravatarScreen
import com.gravatar.app.homeUi.presentation.home.profile.ProfileScreen
import com.gravatar.app.homeUi.presentation.home.share.ShareScreen
import kotlinx.serialization.Serializable

@Serializable
data object GravatarDest

@Serializable
data object ProfileDest

@Serializable
data object ShareDest

@Composable
fun HomeNavigation(
    navController: NavHostController,
    onLoggedOut: () -> Unit
) {
    NavHost(navController = navController, startDestination = GravatarDest) {
        composable<GravatarDest> {
            GravatarScreen(onLoggedOut)
        }

        composable<ProfileDest> {
            ProfileScreen()
        }

        composable<ShareDest> {
            ShareScreen()
        }
    }
}
