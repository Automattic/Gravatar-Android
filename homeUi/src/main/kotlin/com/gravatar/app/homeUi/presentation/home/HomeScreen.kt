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
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.navigation.GravatarDest
import com.gravatar.app.homeUi.navigation.HomeNavigation
import com.gravatar.app.homeUi.navigation.ProfileDest
import com.gravatar.app.homeUi.navigation.ShareDest

@Composable
fun HomeScreen(
    onLoggedOut: () -> Unit,
) {
    val navController = rememberNavController()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem = when (destination.route) {
                GravatarDest::class.qualifiedName -> 0
                ProfileDest::class.qualifiedName -> 1
                ShareDest::class.qualifiedName -> 2
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
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.gravatar),
                            contentDescription = stringResource(R.string.gravatar)
                        )
                    },
                    label = { Text(stringResource(R.string.gravatar)) },
                    selected = selectedItem == 0,
                    onClick = {
                        if (selectedItem != 0) {
                            navController.navigate(GravatarDest) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.profile_icon),
                            contentDescription = stringResource(R.string.profile)
                        )
                    },
                    label = { Text(stringResource(R.string.profile)) },
                    selected = selectedItem == 1,
                    onClick = {
                        if (selectedItem != 1) {
                            navController.navigate(ProfileDest) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.qr_code_icon),
                            contentDescription = stringResource(R.string.share)
                        )
                    },
                    label = { Text(stringResource(R.string.share)) },
                    selected = selectedItem == 2,
                    onClick = {
                        if (selectedItem != 2) {
                            navController.navigate(ShareDest) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
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
