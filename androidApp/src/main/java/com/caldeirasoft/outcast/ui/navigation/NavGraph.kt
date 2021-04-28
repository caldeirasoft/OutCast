package com.caldeirasoft.outcast.ui.navigation

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
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataScreen
import com.caldeirasoft.outcast.ui.screen.store.search.StoreSearchScreen
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsScreen
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
                    route = "${ScreenName.STORE_CHARTS.name}/{itemType}",
                    arguments = listOf(
                        navArgument("itemType") { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemType =
                        backStackEntry.arguments?.getString("itemType")
                            ?.let { StoreItemType.valueOf(it) }
                            ?: StoreItemType.PODCAST
                    TopChartsScreen(
                        storeItemType = itemType,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    route = "${ScreenName.PODCAST.name}/{podcast}",
                    arguments = listOf(navArgument("podcast") { type = NavType.StringType })
                ) {
                    PodcastScreen(
                        viewModel = hiltNavGraphViewModel(),
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    route = "${ScreenName.EPISODE.name}/{episode}",
                    arguments = listOf(
                        navArgument("episode") { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val episode = backStackEntry.getObjectNotNull<Episode>("episode")

                    val fromSamePodcast = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getBoolean("fromSamePodcast")
                        ?: false

                    EpisodeScreen(
                        viewModel = hiltNavGraphViewModel(),
                        fromSamePodcast = fromSamePodcast,
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
                composable(
                    route = "${ScreenName.EPISODE_STORE.name}/{episode}/{podcast}",
                    arguments = listOf(
                        navArgument("episode") { type = NavType.StringType },
                        navArgument("podcast") { type = NavType.StringType },
                    )
                ) {
                    EpisodeScreen(
                        viewModel = hiltNavGraphViewModel(),
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