package com.caldeirasoft.outcast.ui.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.ui.navigation.Screen.*

class Actions(navController: NavController) {
    val select: (Screen) -> Unit = { screen ->
        when (screen) {
            is PodcastScreen -> {
                navController.currentBackStackEntry
                    ?.arguments
                    ?.putParcelable("podcast", screen.podcastArg)
                navController.navigate(screen.id.name)
            }
            is EpisodeScreen ->
                navController.navigate("${screen.id.name}/${Screen.encodeObject(screen.episodeArg)}")
            is Discover -> {
                val storeData = screen.storeDataArg
                if (storeData != null) {
                    navController
                        .currentBackStackEntry
                        ?.arguments
                        ?.putParcelable("storeData", storeData)
                    navController.navigate("${screen.id.name}/${storeData.id}")
                } else {
                    navController.navigate(screen.id.name)
                }
            }
            is Charts ->
                navController.navigate("${screen.id.name}/${screen.itemType}")
            is PodcastSettings ->
                navController.navigate("${screen.id.name}/${screen.podcastId}")
            else ->
                navController.navigate(screen.id.name)
        }
    }

    val selectBottomNav: (BottomNavigationScreen, String?) -> Unit = { screen, currentRoute ->
        navController.navigate(screen.id.name) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo = navController.graph.startDestination
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
        }
    }

    val up: () -> Unit = {
        navController.navigateUp()
    }


    val openUrl: (context: Context, url: String) -> Unit = { _, _ ->
        //makeOpenUrl(context, url)
    }
}
