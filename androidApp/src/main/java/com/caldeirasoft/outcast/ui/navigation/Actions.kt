package com.caldeirasoft.outcast.ui.navigation

import android.content.Context
import androidx.navigation.NavController
import com.caldeirasoft.outcast.ui.navigation.Screen.*
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.encodeObject
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.jsonUrlEncodeObject
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.urlEncode

class Actions(navController: NavController) {
    val select: (Screen) -> Unit = { screen ->
        when (screen) {
            is PodcastScreen -> {
                val screenName = screen.id.name
                screen.storePodcast?.let {
                    navController.currentBackStackEntry
                        ?.arguments
                        ?.putString("podcast", encodeObject(it))
                }
                val feedUrl = screen.feedUrl.urlEncode()
                navController.navigate("$screenName/$feedUrl")
            }
            is EpisodeScreen -> {
                navController.currentBackStackEntry
                    ?.arguments
                    ?.putBoolean("fromSamePodcast", screen.fromSamePodcast)

                screen.storeEpisode?.let {
                    navController.currentBackStackEntry
                        ?.arguments
                        ?.putString("episode", encodeObject(it))
                }
                val screenName = screen.id.name
                val feedUrl = screen.feedUrl.urlEncode()
                val guid = screen.guid.urlEncode()
                navController.navigate("$screenName/$feedUrl/$guid")
            }
            is StoreDataScreen -> {
                val storeData = screen.storeData
                if (storeData != null) {
                    val storeDataEncoded = jsonUrlEncodeObject(storeData)
                    navController.navigate("${screen.id.name}/${storeDataEncoded}")
                } else {
                    navController.navigate(screen.id.name)
                }
            }
            is PodcastSettings -> {
                val feedUrl = screen.feedUrl.urlEncode()
                navController.navigate("${screen.id.name}/$feedUrl")
            }
            else ->
                navController.navigate(screen.id.name)
        }
    }

    val selectBottomNav: (BottomNavigationScreen, String?) -> Unit = { screen, currentRoute ->
        navController.navigate(screen.id.name) {
            launchSingleTop = true
            restoreState = true
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    val up: () -> Unit = {
        navController.navigateUp()
    }


    val openUrl: (context: Context, url: String) -> Unit = { _, _ ->
        //makeOpenUrl(context, url)
    }
}
