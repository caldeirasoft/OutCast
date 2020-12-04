package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.domain.models.StoreRoom

class Actions(navController: NavController) {
    val navigateToStoreDirectory: () -> Unit = {
        navController.navigate(Route.StoreDirectory.name)
    }

    val navigateToStoreCollection: (StoreRoom) -> Unit = { room ->
        navController.navigate(Route.StoreCollectionPage.buildRoute(room))
    }

    val navigateToStorePodcast: (String) -> Unit = { url ->
        navController.navigate(Route.StorePodcast.buildRoute(url))
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}