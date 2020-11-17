package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate

class Actions(navController: NavController) {
    val navigateToDiscover: () -> Unit = {
        navController.navigate(Route.Discover.name)
    }

    val navigateToStoreEntry: (String) -> Unit = { url ->
        navController.navigate(Route.StoreEntry.buildRoute(url))
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}