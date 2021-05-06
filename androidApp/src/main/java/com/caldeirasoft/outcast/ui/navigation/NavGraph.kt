package com.caldeirasoft.outcast.ui.navigation

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataScreen
import com.caldeirasoft.outcast.ui.screen.store.search.StoreSearchScreen
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder

object NavArgs {
    const val PodcastId = "podcastId"
    const val Title = "title"
    const val Url = "url"
    const val Podcast = "podcast"
    const val Episode = "episode"
    const val Room = "room"
    const val Charts = "charts"
    const val Categories = "categories"
    const val Genre = "genre"
    const val ItemType = "itemType"
    const val StoreFront = "storeFront"
}

inline fun <reified T> NavBackStackEntry.getObjectNotNull(key: String): T {
    val chartsEncoded = arguments?.getString(key)
    return requireNotNull(Json.decodeFromString(serializer(), URLDecoder.decode(chartsEncoded, "UTF-8")))
}

inline fun <reified T> NavBackStackEntry.getObject(key: String): T? =
    arguments?.getString(key)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    }

inline fun <reified T> SavedStateHandle.getObject(key: String): T? =
    (get(key) as String?)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    }

inline fun <reified T> SavedStateHandle.getObjectNotNull(key: String): T =
    requireNotNull((get(key) as String?)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    })

inline fun <reified T> Bundle.getObject(key: String): T? =
    getString(key)?.let {
        Json.decodeFromString(serializer(), it)
    }


@FlowPreview
@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavHost(startScreen: ScreenName) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }

    ModalBottomSheetHost() {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            bottomBar = {
                SetupBottomNavBar(
                    navController = navController,
                    items = listOf(
                        BottomNavigationScreen.Inbox,
                        BottomNavigationScreen.Library,
                        BottomNavigationScreen.Discover,
                        BottomNavigationScreen.Search,
                        BottomNavigationScreen.More,
                    ),
                    navigateTo = actions.selectBottomNav
                )
            })
        {
            NavHost(
                navController = navController,
                startDestination = startScreen.name,
            ) {
                composable(ScreenName.INBOX.name) {
                    InboxScreen(
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
                composable(ScreenName.LIBRARY.name) {
                    LibraryScreen(
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(ScreenName.PROFILE.name) { Text(text = "Profile") }
                composable(ScreenName.STORE_DATA.name) {
                    StoreDataScreen(
                        viewModel = hiltNavGraphViewModel(),
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
                composable(route = "${ScreenName.STORE_DATA.name}/{storeData}",
                    arguments = listOf(navArgument("storeData") { type = NavType.StringType })) {
                    StoreDataScreen(
                        viewModel = hiltNavGraphViewModel(),
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
                composable(ScreenName.STORE_SEARCH.name) {
                    StoreSearchScreen(
                        navigateTo = actions.select)
                }
                composable(
                    route = "${ScreenName.PODCAST.name}/{feedUrl}",
                    arguments = listOf(navArgument("feedUrl") { type = NavType.StringType })
                ) {
                    val storePodcast = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getObject<StorePodcast>("podcast")

                    PodcastScreen(
                        viewModel = hiltNavGraphViewModel(),
                        storePodcast = storePodcast,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    route = "${ScreenName.EPISODE.name}/{feedUrl}/{guid}",
                    arguments = listOf(
                        navArgument("feedUrl") { type = NavType.StringType },
                        navArgument("guid") { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val fromSamePodcast = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getBoolean("fromSamePodcast")
                        ?: false

                    val storeEpisode = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getObject<StoreEpisode>("episode")

                    EpisodeScreen(
                        viewModel = hiltNavGraphViewModel(),
                        storeEpisode = storeEpisode,
                        fromSamePodcast = fromSamePodcast,
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
            }
        }
    }
}

@Composable
fun SetupBottomNavBar(
    navController: NavController,
    items: List<BottomNavigationScreen>,
    navigateTo: (BottomNavigationScreen, String?) -> Unit,
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == screen.id.name) screen.selectedIcon else screen.icon,
                        contentDescription = stringResource(id = screen.resourceId),
                    )
                },
                selected = currentRoute == screen.id.name,
                onClick = {
                    navigateTo(screen, currentRoute)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
            )
        }
    }
}