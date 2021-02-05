package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionGenres
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.categories.StoreCategoriesScreen
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.store.genre.StoreGenreScreen
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastScreen
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomScreen
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
enum class Screen(private val root: String, private vararg val arguments: String) {
    QUEUE("queue"),
    INBOX("inbox"),
    LIBRARY("podcasts"),
    PROFILE("profile"),
    STORE_DIRECTORY("store/directory"),
    STORE_CATEGORIES("store/categories", "categories"),
    STORE_GENRE("store/genre", "genre", "title"),
    STORE_CHARTS("store/charts", "itemType"),
    STORE_ROOM("store/room", "room"),
    STORE_PODCAST("store/podcast", "podcast"),
    STORE_PODCAST_EPISODES("store/podcastEpisodes", "podcast")
    ;

    val route = listOf(root)
        .plus(arguments.map { "{$it}"})
        .joinToString("/")

    companion object {
        fun path(screen: Screen) = screen.root

        fun path(screen: Screen, vararg fromToPairs: Pair<String, Any>): String {
            val entries: HashMap<String, Any> = hashMapOf()
            fromToPairs.forEach { pair ->
                entries[pair.first] =
                    pair.second.let {
                        when(it) {
                            is StoreRoom -> encodeObject(it)
                            is StoreCollectionGenres -> encodeObject(it)
                            is StorePodcast -> encodeObject(it)
                            else -> it
                        }
                    }
            }
            return listOf(screen.root)
                .plus( screen.arguments.map { entries[it].toString() } )
                .joinToString("/")
        }

        private inline fun <reified T> encodeObject(item: T): String =
            URLEncoder.encode(Json.encodeToString(item), "UTF-8")
    }
}

enum class BottomNavigationScreen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector)
{
    QUEUE(Screen.QUEUE.route, R.string.screen_queue, Icons.Default.QueueMusic),
    INBOX(Screen.INBOX.route, R.string.screen_inbox, Icons.Default.Inbox),
    LIBRARY(Screen.LIBRARY.route, R.string.screen_queue, Icons.Default.LibraryMusic),
    DISCOVER(Screen.STORE_DIRECTORY.route, R.string.screen_queue, Icons.Default.Search),
    PROFILE(Screen.PROFILE.route, R.string.screen_queue, Icons.Default.Person)
}

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

class Actions(navController: NavController) {
    val navigateToStoreDirectory: () -> Unit = {
        navController.navigate(Screen.STORE_DIRECTORY)
    }

    val navigateToStoreCategories: (StoreCollectionGenres) -> Unit = { categories ->
        navController.navigate(Screen.STORE_CATEGORIES,
        "categories" to categories)
    }

    val navigateToStoreRoom: (StoreRoom) -> Unit = { room ->
        navController.navigate(Screen.STORE_ROOM,
            "room" to room)
    }

    val navigateToStoreGenre: (Int, String) -> Unit = { genreId, title ->
        navController.navigate(Screen.STORE_GENRE,
            "genre" to genreId, "title" to title)
    }

    val navigateToStoreCharts: (StoreItemType) -> Unit = { itemType ->
        navController.navigate(Screen.STORE_CHARTS,
            "itemType" to itemType)
    }

    val navigateToStorePodcast: (StorePodcast) -> Unit = { storePodcast ->
        navController.navigate(Screen.STORE_PODCAST,
            "podcast" to storePodcast)
    }

    val navigateToStorePodcastEpisodes: (StorePodcast) -> Unit = { storePodcast ->
        navController.navigate(Screen.STORE_PODCAST_EPISODES,
            "podcast" to storePodcast)
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}

class BottomSheetActions {
    val navigateToEpisode: (StoreEpisode) -> Unit = { storeEpisode ->
        Unit
    }
}

inline fun <reified T> NavBackStackEntry.getObjectNotNull(key: String): T {
    val chartsEncoded = arguments?.getString(key)
    return requireNotNull(Json.decodeFromString(serializer(), URLDecoder.decode(chartsEncoded, "UTF-8")))
}

fun NavController.navigate(screen: Screen, vararg fromToPairs: Pair<String, Any>) =
    this.navigate(Screen.path(screen, *fromToPairs))

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavHost(startScreen: Screen) {
    val navController = rememberNavController()
    val navigateTo = remember(navController) { Actions(navController) }
    val bottomNavigateTo = remember { BottomSheetActions() }

    CustomBottomDrawerHost()
    {
        Scaffold(
            bottomBar = {
                SetupBottomNavBar(
                    navController = navController,
                    items = BottomNavigationScreen.values().toList(),
                )
            })
        {
            NavHost(
                navController = navController,
                startDestination = startScreen.route,
            ) {
                composable(Screen.QUEUE.route) { Text(text = "Queue") }
                composable(Screen.INBOX.route) { InboxScreen() }
                composable(Screen.LIBRARY.route) { Text(text = "Library") }
                composable(Screen.PROFILE.route) { Text(text = "Profile") }
                composable(Screen.STORE_DIRECTORY.route) {
                    StoreDirectoryScreen(
                        navigateToGenre = navigateTo.navigateToStoreGenre,
                        navigateToRoom = navigateTo.navigateToStoreRoom,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateUp = navigateTo.navigateUp,
                        navigateToCategories = navigateTo.navigateToStoreCategories,
                        navigateToTopCharts = navigateTo.navigateToStoreCharts,
                    )
                }
                composable(
                    Screen.STORE_GENRE.route,
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
                        navigateToRoom = navigateTo.navigateToStoreRoom,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateToEpisode = bottomNavigateTo.navigateToEpisode,
                        navigateUp = navigateTo.navigateUp
                    )
                }
                composable(
                    Screen.STORE_CATEGORIES.route,
                    arguments = listOf(navArgument("categories") { type = NavType.StringType })
                ) { backStackEntry ->
                    val storeCategories = backStackEntry.getObjectNotNull<StoreCollectionGenres>("categories")
                    StoreCategoriesScreen(
                        storeCollection = storeCategories,
                        navigateToGenre = navigateTo.navigateToStoreGenre,
                        navigateUp = navigateTo.navigateUp)
                }
                composable(
                    Screen.STORE_CHARTS.route,
                    arguments = listOf(
                        navArgument(NavArgs.ItemType) { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemType =
                        backStackEntry.arguments?.getString(NavArgs.ItemType)
                            ?.let { StoreItemType.valueOf(it) }
                            ?: StoreItemType.PODCAST
                    TopChartsScreen(
                        storeItemType = itemType,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateToEpisode = bottomNavigateTo.navigateToEpisode,
                        navigateUp = navigateTo.navigateUp)
                }
                composable(
                    Screen.STORE_ROOM.route,
                    arguments = listOf(navArgument(NavArgs.Room) { type = NavType.StringType })
                ) { backStackEntry ->
                    val storeRoom = backStackEntry.getObjectNotNull<StoreRoom>(NavArgs.Room)
                    StoreRoomScreen(
                        storeRoom = storeRoom,
                        navigateToRoom = navigateTo.navigateToStoreRoom,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateToEpisode = bottomNavigateTo.navigateToEpisode,
                        navigateUp = navigateTo.navigateUp)
                }
                composable(
                    Screen.STORE_PODCAST.route,
                    arguments = listOf(navArgument(NavArgs.Podcast) { type = NavType.StringType })
                ) { backStackEntry ->
                    val podcast = backStackEntry.getObjectNotNull<StorePodcast>(NavArgs.Podcast)
                    StorePodcastScreen(
                        storePodcast = podcast,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateToPodcastEpisodes = navigateTo.navigateToStorePodcastEpisodes,
                        navigateToEpisode = bottomNavigateTo.navigateToEpisode,
                        navigateToRoom = navigateTo.navigateToStoreRoom,
                        navigateToGenre = navigateTo.navigateToStoreGenre,
                        navigateUp = navigateTo.navigateUp)
                }
                composable(
                    Screen.STORE_PODCAST_EPISODES.route,
                    arguments = listOf(navArgument(NavArgs.Podcast) { type = NavType.StringType })
                ) { backStackEntry ->
                    val podcast = backStackEntry.getObjectNotNull<StorePodcast>(NavArgs.Podcast)
                    StorePodcastEpisodesScreen(
                        storePodcast = podcast,
                        navigateToPodcast = navigateTo.navigateToStorePodcast,
                        navigateToEpisode = bottomNavigateTo.navigateToEpisode,
                        navigateUp = navigateTo.navigateUp)
                }
            }
        }
    }
}

@Composable
fun SetupBottomNavBar(
    navController: NavController,
    items: List<BottomNavigationScreen>,
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon) },
                label = {
                    Text(text = stringResource(id = screen.resourceId),
                        style = typography.overline)
                },
                selected = currentRoute == screen.route,
                onClick = {
                    // This is the equivalent to popUpTo the start destination
                    // In order to ensure that each time a BottomNavigationItem is selected the
                    // back stack is not continuing to add destinations,
                    // we pop the back stack up to the startDestination. This is consistent with
                    // the behavior of using NavOptions singleTop=true, popUpTo=startDestination in the navigation runtime library.
                    navController.popBackStack(navController.graph.startDestination, false)
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

sealed class HomeBottomSheetState {
    object Empty : HomeBottomSheetState()
    class TopChartsItemType(storeItemType: StoreItemType) : HomeBottomSheetState()
    class TopChartsGenre(val selectedGenre: Int?, val onGenreSelected: (Int?) -> Unit) : HomeBottomSheetState()
}