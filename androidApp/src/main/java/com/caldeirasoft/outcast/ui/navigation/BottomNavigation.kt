package com.caldeirasoft.outcast.ui.navigation

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.twotone.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.InboxScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.PlayedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.SavedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsScreen
import com.caldeirasoft.outcast.ui.screen.store.search.StoreSearchScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.Routes
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataScreen
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder

sealed class BottomNavigationScreen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
) {
    object Inbox : BottomNavigationScreen(
        RoutesActions.toInbox(),
        R.string.screen_inbox,
        Icons.Outlined.Inbox,
        Icons.Filled.Inbox)

    object Library : BottomNavigationScreen(
        RoutesActions.toLibrary(),
        R.string.screen_library,
        Icons.Outlined.Subscriptions,
        Icons.Filled.Subscriptions)

    object Discover : BottomNavigationScreen(
        RoutesActions.toStore(),
        R.string.screen_discover,
        Icons.Outlined.Explore,
        Icons.Filled.Explore
    )

    object Search : BottomNavigationScreen(
        RoutesActions.toSearch(),
        R.string.screen_search,
        Icons.Outlined.Search,
        Icons.TwoTone.Search)
}

val bottomNavItems = listOf(
    BottomNavigationScreen.Inbox,
    BottomNavigationScreen.Library,
    BottomNavigationScreen.Discover,
    BottomNavigationScreen.Search,
)

@Composable
fun SetupBottomNavBar(
    navController: NavController,
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.icon,
                        contentDescription = stringResource(id = screen.resourceId),
                    )
                },
                selected = currentRoute == screen.route,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
            )
        }
    }
}