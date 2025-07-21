package com.gravatar.app.homeUi.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.design.components.snackbar.GravatarSnackbarHost
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.navigation.HomeDestination
import com.gravatar.app.homeUi.navigation.HomeNavigation
import com.gravatar.app.networkmonitor.NetworkState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen() {
    HomeScreen(viewModel = koinViewModel())
}

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(uiState = uiState) { navController, snackbarHostState ->
        HomeNavigation(navController, snackbarHostState)
    }
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    content: @Composable (NavHostController, SnackbarHostState) -> Unit
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry = navController.currentBackStackEntryAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { GravatarSnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                HomeDestination.allDestinations
                    .sortedBy { it.position }
                    .forEach { destination ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = destination.iconRes),
                                    contentDescription = stringResource(destination.labelRes)
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                            selected = destination.route == backStackEntry.value?.destination?.route,
                            onClick = {
                                if (backStackEntry.value?.destination?.route != destination.route) {
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent,
                            )
                        )
                    }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            Box {
                content(navController, snackbarHostState)
                AnimatedVisibility(
                    visible = uiState.noInternetBannerVisible,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    NoNetworkBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun NoNetworkBanner(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = R.string.home_no_internet_available),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f))
            .padding(16.dp)
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeUiState(
            networkState = null
        )
    ) { navController, snackbarHostState ->
        HomeNavigation(navController, snackbarHostState)
    }
}

@Preview
@Composable
private fun HomeScreenNoInternetPreview() {
    HomeScreen(
        uiState = HomeUiState(
            networkState = NetworkState.DISCONNECTED
        )
    ) { navController, snackbarHostState ->
        HomeNavigation(navController, snackbarHostState)
    }
}
