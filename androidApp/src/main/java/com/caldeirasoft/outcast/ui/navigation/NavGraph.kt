package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastArg
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverScreen
import com.caldeirasoft.outcast.ui.screen.store.discover.StoreDataArg
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
                composable(ScreenName.DISCOVER.name) {
                    DiscoverScreen(
                        storeDataArg = null,
                        navigateTo = actions.select
                    )
                }
                composable(route = "${ScreenName.DISCOVER.name}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                    val storeDataArg =
                        navController.previousBackStackEntry
                            ?.arguments
                            ?.getParcelable<StoreDataArg>("storeData")
                    DiscoverScreen(
                        storeDataArg = storeDataArg,
                        navigateTo = actions.select
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
                composable(ScreenName.PODCAST.name) {
                    val podcastArg =
                        requireNotNull(navController.previousBackStackEntry
                            ?.arguments
                            ?.getParcelable<PodcastArg>("podcast"))
                    PodcastScreen(
                        podcastArg = podcastArg,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    route = "${ScreenName.PODCAST.name}/{podcast}",
                    arguments = listOf(navArgument("podcast") { type = NavType.StringType })
                ) { backStackEntry ->
                    val podcastArg = backStackEntry.getObjectNotNull<PodcastArg>("podcast")
                    PodcastScreen(
                        podcastArg = podcastArg,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    route = "${ScreenName.EPISODE.name}/{episode}",
                    arguments = listOf(navArgument("episode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val episodeArg = backStackEntry.getObjectNotNull<EpisodeArg>("episode")
                    EpisodeScreen(
                        episodeArg = episodeArg,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
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