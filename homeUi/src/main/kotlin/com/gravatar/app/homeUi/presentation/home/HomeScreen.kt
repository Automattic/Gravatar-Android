package com.gravatar.app.homeUi.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gravatar.app.homeUi.navigation.HomeDestination
import com.gravatar.app.homeUi.navigation.HomeNavigation

@Composable
fun HomeScreen() {
    HomeScreen { navController ->
        HomeNavigation(navController)
    }
}

@Composable
internal fun HomeScreen(
    content: @Composable (NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

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
                            selected = destination.route == backStackEntry.value?.destination?.route,
                            onClick = {
                                if (backStackEntry.value?.destination?.route != destination.route) {
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
            content(navController)
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
