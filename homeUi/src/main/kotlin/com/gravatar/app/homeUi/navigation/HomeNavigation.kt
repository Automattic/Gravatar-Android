package com.gravatar.app.homeUi.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gravatar.app.homeUi.presentation.home.gravatar.GravatarScreen
import com.gravatar.app.homeUi.presentation.home.profile.ProfileScreen
import com.gravatar.app.homeUi.presentation.home.share.ShareScreen
import kotlinx.serialization.Serializable

@Composable
internal fun HomeNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navController,
        route = HomeRoute::class,
        startDestination = HomeDestination.Gravatar
    ) {
        composable<HomeDestination.Gravatar> {
            GravatarScreen(
                viewModelStoreOwner = navController.getBackStackEntry(HomeRoute::class),
                snackbarHostState = snackbarHostState
            )
        }

        composable<HomeDestination.Profile> {
            ProfileScreen(
                viewModelStoreOwner = navController.getBackStackEntry(HomeRoute::class),
                snackbarHostState = snackbarHostState
            )
        }

        composable<HomeDestination.Share> {
            ShareScreen(
                viewModelStoreOwner = navController.getBackStackEntry(HomeRoute::class),
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Serializable
internal object HomeRoute

@Serializable
internal sealed class HomeDestination(
    val iconRes: Int,
    val labelRes: Int,
    val position: Int,
) {
    val route: String =
        this::class.qualifiedName ?: error("Route name is not defined for $this")

    @Serializable
    data object Gravatar : HomeDestination(
        iconRes = com.gravatar.app.homeUi.R.drawable.gravatar,
        labelRes = com.gravatar.app.homeUi.R.string.home_screen_navigation_item_gravatar,
        position = 0
    )

    @Serializable
    data object Profile : HomeDestination(
        iconRes = com.gravatar.app.homeUi.R.drawable.profile_icon,
        labelRes = com.gravatar.app.homeUi.R.string.home_screen_navigation_item_profile,
        position = 1
    )

    @Serializable
    data object Share : HomeDestination(
        iconRes = com.gravatar.app.homeUi.R.drawable.qr_code_icon,
        labelRes = com.gravatar.app.homeUi.R.string.home_screen_navigation_item_share,
        position = 2
    )

    companion object {
        val allDestinations = listOf(Gravatar, Profile, Share)
    }
}
