package com.gravatar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val backStackEntry = navController.currentBackStackEntryAsState()
    val isUserLoggedIn: IsUserLoggedIn = koinInject<IsUserLoggedIn>()

    LaunchedEffect(Unit) {
        isUserLoggedIn()
            .collect { userSession ->
                val lastRoute = backStackEntry.value?.destination?.route?.let {
                    RootDestination.fromRoute(it)
                } ?: RootDestination.Splash

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
    navigate(destination) {
        popUpTo(popTo) {
            inclusive = true
            saveState = shouldSaveState
        }
        launchSingleTop = true
        restoreState = shouldSaveState
    }
}

internal sealed class RootDestination {
    @Serializable
    data object Splash : RootDestination()

    @Serializable
    data object Login : RootDestination()

    @Serializable
    data object Home : RootDestination()

    companion object {
        fun fromRoute(route: String): RootDestination? {
            return when (route) {
                Splash::class.qualifiedName -> Splash
                Login::class.qualifiedName -> Login
                Home::class.qualifiedName -> Home
                else -> null
            }
        }
    }
}
