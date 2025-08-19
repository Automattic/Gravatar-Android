package com.gravatar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.presentation.home.HomeScreen
import com.gravatar.app.loginUi.presentation.login.LoginScreen
import com.gravatar.app.usercomponent.domain.model.UserSession.LOGGED_IN
import com.gravatar.app.usercomponent.domain.model.UserSession.LOGGED_OUT
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedIn
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isUserLoggedIn: IsUserLoggedIn = koinInject<IsUserLoggedIn>()

    LaunchedEffect(Unit) {
        isUserLoggedIn()
            .collect { userSession ->
                val lastRoute =
                    backStackEntry?.destination?.rootDestination ?: RootDestination.Splash

                when (userSession) {
                    LOGGED_IN -> navController.navigateToRootDestination(
                        destination = RootDestination.Home,
                        popTo = lastRoute,
                        shouldSaveState = true
                    )

                    LOGGED_OUT -> navController.navigateToRootDestination(
                        destination = RootDestination.Login,
                        popTo = lastRoute,
                        shouldSaveState = lastRoute == RootDestination.Login
                    )
                }
            }
    }

    NavHost(navController = navController, startDestination = RootDestination.Splash) {
        composable<RootDestination.Splash> {
        }
        composable<RootDestination.Login> {
            LoginScreen()
        }
        composable<RootDestination.Home> {
            HomeScreen()
        }
    }
}

private fun NavHostController.navigateToRootDestination(
    destination: RootDestination,
    popTo: RootDestination,
    shouldSaveState: Boolean = true
) {
    if (popTo != destination) {
        navigate(destination) {
            popUpTo(popTo) {
                inclusive = true
                saveState = shouldSaveState
            }
            launchSingleTop = true
            restoreState = shouldSaveState
        }
    }
}

private val NavDestination.rootDestination: RootDestination?
    get() = when {
        hasRoute(RootDestination.Splash::class) -> RootDestination.Splash
        hasRoute(RootDestination.Login::class) -> RootDestination.Login
        hasRoute(RootDestination.Home::class) -> RootDestination.Home
        else -> null
    }

internal sealed class RootDestination {
    @Serializable
    data object Splash : RootDestination()

    @Serializable
    data object Login : RootDestination()

    @Serializable
    data object Home : RootDestination()
}
