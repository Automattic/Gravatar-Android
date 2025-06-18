package com.gravatar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.presentation.home.HomeScreen
import com.gravatar.app.loginUi.presentation.login.LoginScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object SplashDest

@Serializable
data object LoginDest

@Serializable
data object HomeDest

@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SplashDest) {
        composable<SplashDest> {
            val scope = rememberCoroutineScope()
            scope.launch {
                delay(2000) // Simulate a loading delay
                navController.navigate(LoginDest) {
                    popUpTo(SplashDest) { inclusive = true }
                }
            }
        }
        composable<LoginDest> {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(HomeDest) {
                        popUpTo(LoginDest) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeDest> {
            HomeScreen(
                onLoggedOut = {
                    navController.navigate(LoginDest) {
                        popUpTo(HomeDest) { inclusive = true }
                    }
                }
            )
        }
    }
}
