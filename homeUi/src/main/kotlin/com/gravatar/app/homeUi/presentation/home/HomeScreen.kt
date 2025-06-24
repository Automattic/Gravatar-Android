package com.gravatar.app.homeUi.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.navigation.HomeDestination
import com.gravatar.app.homeUi.navigation.HomeNavigation

@Composable
fun HomeScreen(
    onLoggedOut: () -> Unit,
) {
    val navController = rememberNavController()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem = when (destination.route) {
                HomeDestination.Gravatar.route -> HomeDestination.Gravatar.position
                HomeDestination.Profile.route -> HomeDestination.Profile.position
                HomeDestination.Share.route -> HomeDestination.Share.position
                else -> selectedItem
            }
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
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
                            selected = selectedItem == destination.position,
                            onClick = {
                                if (selectedItem != destination.position) {
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            HomeNavigation(navController, onLoggedOut)
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onLoggedOut = { }
    )
}
