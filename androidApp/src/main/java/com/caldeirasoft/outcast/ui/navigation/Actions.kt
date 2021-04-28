package com.caldeirasoft.outcast.ui.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.ui.navigation.Screen.*
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.encodeObject

class Actions(navController: NavController) {
    val select: (Screen) -> Unit = { screen ->
        when (screen) {
            is PodcastScreen -> {
                val screenName = screen.id.name
                val podcastEncoded = encodeObject(screen.podcast)
                navController.navigate("$screenName/$podcastEncoded")
            }
            is EpisodeScreen -> {
                navController.currentBackStackEntry
                    ?.arguments
                    ?.putBoolean("fromSamePodcast", screen.fromSamePodcast)
                val screenName = screen.id.name
                val episodeEncoded = encodeObject(screen.episode)
                navController.navigate("$screenName/$episodeEncoded")
            }
            is EpisodeStoreScreen -> {
                val screenName = screen.id.name
                val episodeEncoded = encodeObject(screen.episode)
                val podcastEncoded = encodeObject(screen.podcast)
                navController.navigate("$screenName/$episodeEncoded/$podcastEncoded")
            }
            is StoreDataScreen -> {
                val storeData = screen.storeData
                if (storeData != null) {
                    val storeDataEncoded = encodeObject(storeData)
                    navController.navigate("${screen.id.name}/${storeDataEncoded}")
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
