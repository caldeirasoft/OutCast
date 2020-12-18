package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionCharts
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts

class Actions(navController: NavController) {
    val navigateToStoreDirectory: () -> Unit = {
        navController.navigate(Route.StoreDirectory.name)
    }

    val navigateToStoreRoom: (StoreRoom) -> Unit = { room ->
        navController.navigate(Route.StoreRoomPage.buildRoute(room))
    }

    val navigateToStoreCharts: (StoreTopCharts) -> Unit = { charts ->
        navController.navigate(Route.StoreChartsPage.buildRoute(charts))
    }

    val navigateToStorePodcast: (String) -> Unit = { url ->
        navController.navigate(Route.StorePodcast.buildRoute(url))
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}