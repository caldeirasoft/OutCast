package com.caldeirasoft.outcast.ui.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.ui.navigation.Screen.*

class Actions(navController: NavController) {
    val select: (Screen) -> Unit = { screen ->
        when (screen) {
            is PodcastScreen ->
                navController.navigate("${screen.id.name}/${Screen.encodeObject(screen.podcast)}")
            is StorePodcastScreen ->
                navController.navigate("${screen.id.name}/${Screen.encodeObject(screen.podcast)}")
            is StoreEpisodesScreen ->
                navController.navigate("${screen.id.name}/${Screen.encodeObject(screen.podcast)}")
            is Room ->
                navController.navigate("${screen.id.name}/${Screen.encodeObject(screen.room)}")
            is Charts ->
                navController.navigate("${screen.id.name}/${screen.itemType}")
            is Genre ->
                navController.navigate("${screen.id.name}/${screen.genreId}/${screen.title}")
            else ->
                navController.navigate(screen.id.name)
        }
    }

    val selectBottomNav: (BottomNavigationScreen, String?) -> Unit = { screen, currentRoute ->
        // This is the equivalent to popUpTo the start destination
        // In order to ensure that each time a BottomNavigationItem is selected the
        // back stack is not continuing to add destinations,
        // we pop the back stack up to the startDestination. This is consistent with
        // the behavior of using NavOptions singleTop=true, popUpTo=startDestination in the navigation runtime library.
        navController.popBackStack(navController.graph.startDestination, false)
        // This if check gives us a "singleTop" behavior where we do not create a
        // second instance of the composable if we are already on that destination
        if (currentRoute != screen.id.name) {
            navController.navigate(screen.id.name)
        }
    }

    val up: () -> Unit = {
        navController.navigateUp()
    }


    val openUrl: (context: Context, url: String) -> Unit = { _, _ ->
        //makeOpenUrl(context, url)
    }
}
