package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreRoom

class Actions(navController: NavController) {
    val navigateToStoreDirectory: () -> Unit = {
        navController.navigate(Route.StoreDirectory.name)
    }

    val navigateToStoreRoom: (StoreRoom) -> Unit = { room ->
        navController.navigate(Route.StoreRoomPage.buildRoute(room))
    }

    val navigateToStoreGenre: (StoreGenre) -> Unit = { genre ->
        navController.navigate(Route.StoreGenrePage.buildRoute(genre))
    }

    val navigateToStorePodcast: (String) -> Unit = { url ->
        navController.navigate(Route.StorePodcast.buildRoute(url))
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}