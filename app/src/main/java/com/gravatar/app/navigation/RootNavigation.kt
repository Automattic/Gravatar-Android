package com.gravatar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.presentation.home.HomeScreen
import com.gravatar.app.loginUi.presentation.login.LoginScreen
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.launch
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
    NavHost(navController = navController, startDestination = SplashDest) {
        composable<SplashDest> {
            val authRepository: AuthRepository = koinInject<AuthRepository>()
            LaunchedEffect(Unit) {
                if (authRepository.isUserLoggedIn()) {
                    navController.navigate(HomeDest) {
                        popUpTo(SplashDest) { inclusive = true }
                    }
                } else {
                    navController.navigate(LoginDest) {
                        popUpTo(SplashDest) { inclusive = true }
                    }
                }
            }
        }
        composable<LoginDest> {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(HomeDest) {
                        // Clear the entire back stack to prevent multiple LoginScreen instances
                        popUpTo(LoginDest) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeDest> {
            val scope = rememberCoroutineScope()
            val authRepository: AuthRepository = koinInject<AuthRepository>()
            HomeScreen(
                onLoggedOut = {
                    scope.launch {
                        authRepository.logout()
                        navController.navigate(LoginDest) {
                            popUpTo(HomeDest) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
