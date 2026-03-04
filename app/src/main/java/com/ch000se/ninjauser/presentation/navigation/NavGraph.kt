package com.ch000se.ninjauser.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ch000se.ninjauser.presentation.screens.detail.DetailScreen
import com.ch000se.ninjauser.presentation.screens.home.HomeScreen


@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home
    ) {
        composable<Screens.Home> {
            HomeScreen(
                onUserClick = { userId ->
                    navController.navigate(Screens.Detail(userId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Screens.Detail> {
            DetailScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}