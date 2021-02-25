package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionGenres
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.categories.StoreCategoriesScreen
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.store.genre.StoreGenreScreen
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastScreen
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomScreen
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsScreen
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder

object NavArgs {
    const val PodcastId = "podcastId"
    const val Title = "title"
    const val Url = "url"
    const val Podcast = "podcast"
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
            bottomBar = {
                SetupBottomNavBar(
                    navController = navController,
                    items = listOf(
                        BottomNavigationScreen.Queue,
                        BottomNavigationScreen.Inbox,
                        BottomNavigationScreen.Library,
                        BottomNavigationScreen.Discover,
                        BottomNavigationScreen.Profile
                    ),
                    navigateTo = actions.selectBottomNav
                )
            })
        {
            NavHost(
                navController = navController,
                startDestination = startScreen.name,
            ) {
                composable(ScreenName.QUEUE.name) { Text(text = "Queue") }
                composable(ScreenName.INBOX.name) { InboxScreen() }
                composable(ScreenName.LIBRARY.name) { Text(text = "Library") }
                composable(ScreenName.PROFILE.name) { Text(text = "Profile") }
                composable(ScreenName.STORE_DISCOVER.name) {
                    StoreDirectoryScreen(
                        navigateTo = actions.select
                    )
                }
                composable(
                    "${ScreenName.STORE_GENRE.name}/{${NavArgs.Genre}}/{${NavArgs.Title}}",
                    arguments = listOf(
                        navArgument(NavArgs.Genre) { type = NavType.IntType },
                        navArgument(NavArgs.Title) { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val genreId = requireNotNull(backStackEntry.arguments?.getInt(NavArgs.Genre))
                    val title = backStackEntry.arguments?.getString(NavArgs.Title).orEmpty()
                    StoreGenreScreen(
                        genreId = genreId,
                        title = title,
                        navigateTo = actions.select,
                        navigateBack = actions.up
                    )
                }
                composable(
                    "${ScreenName.STORE_CATEGORIES.name}/{${NavArgs.Categories}}",
                    arguments = listOf(navArgument(NavArgs.Categories) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val storeCategories = backStackEntry.getObjectNotNull<StoreCollectionGenres>("categories")
                    StoreCategoriesScreen(
                        storeCollection = storeCategories,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    "${ScreenName.STORE_CHARTS.name}/{${NavArgs.ItemType}}",
                    arguments = listOf(
                        navArgument(NavArgs.ItemType) { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemType =
                        backStackEntry.arguments?.getString(NavArgs.ItemType)
                            ?.let { StoreItemType.valueOf(it) }
                            ?: StoreItemType.PODCAST
                    TopChartsScreen(
                        storeItemType = itemType,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    "${ScreenName.STORE_ROOM.name}/{${NavArgs.Room}}",
                    arguments = listOf(navArgument(NavArgs.Room) { type = NavType.StringType })
                ) { backStackEntry ->
                    val storeRoom = backStackEntry.getObjectNotNull<StoreRoom>(NavArgs.Room)
                    StoreRoomScreen(
                        storeRoom = storeRoom,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    "${ScreenName.STORE_PODCAST.name}/{${NavArgs.Podcast}}",
                    arguments = listOf(navArgument(NavArgs.Podcast) { type = NavType.StringType })
                ) { backStackEntry ->
                    val podcast = backStackEntry.getObjectNotNull<StorePodcast>(NavArgs.Podcast)
                    StorePodcastScreen(
                        storePodcast = podcast,
                        navigateTo = actions.select,
                        navigateBack = actions.up)
                }
                composable(
                    "${ScreenName.STORE_EPISODES.name}/{${NavArgs.Podcast}}",
                    arguments = listOf(navArgument(NavArgs.Podcast) { type = NavType.StringType })
                ) { backStackEntry ->
                    val podcast = backStackEntry.getObjectNotNull<StorePodcast>(NavArgs.Podcast)
                    StorePodcastEpisodesScreen(
                        storePodcast = podcast,
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
                icon = { Icon(screen.icon, contentDescription = stringResource(id = screen.resourceId)) },
                label = {
                    Text(text = stringResource(id = screen.resourceId),
                        style = typography.overline)
                },
                selected = currentRoute == screen.id.name,
                onClick = { navigateTo(screen, currentRoute) }
            )
        }
    }
}