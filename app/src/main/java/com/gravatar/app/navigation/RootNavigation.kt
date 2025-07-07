package com.gravatar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.presentation.home.HomeScreen
import com.gravatar.app.loginUi.presentation.login.LoginScreen
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedIn
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object SplashDest

@Serializable
data object LoginDest

@Serializable
data object HomeDest

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val isUserLoggedIn: IsUserLoggedIn = koinInject<IsUserLoggedIn>()

    LaunchedEffect(Unit) {
        isUserLoggedIn()
            .collect { isLoggedIn ->
                if (isLoggedIn) {
                    navController.navigate(HomeDest) {
                        popUpTo(SplashDest) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                } else {
                    navController.navigate(LoginDest) {
                        popUpTo(SplashDest) { inclusive = true }
                    }
                }
            }
    }
    NavHost(navController = navController, startDestination = SplashDest) {
        composable<SplashDest> {
        }
        composable<LoginDest> {
            LoginScreen()
        }
        composable<HomeDest> {
            HomeScreen()
        }
    }
}
