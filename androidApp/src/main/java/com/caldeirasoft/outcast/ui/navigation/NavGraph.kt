package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionGenres
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreCategoriesScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.store.genre.StoreGenreScreen
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
    Inbox("inbox"),
    Podcasts("podcasts"),
    STORE_DIRECTORY("store/directory"),
    STORE_CATEGORIES("store/categories", "categories"),
    STORE_GENRE("store/genre", "genre", "title"),
    STORE_CHARTS("store/charts", "genre", "itemType"),
    STORE_ROOM("store/room", "room"),
    STORE_PODCAST("store/podcast", "url");

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

object NavArgs {
    const val PodcastId = "podcastId"
    const val Title = "title"
    const val Url = "url"
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

    val navigateToStoreCharts: (Int, StoreItemType) -> Unit = { genreId, itemType ->
        navController.navigate(Screen.STORE_CHARTS,
            "genre" to genreId, "itemType" to itemType)
    }

    val navigateToStorePodcast: (String) -> Unit = { url ->
        navController.navigate(Screen.STORE_PODCAST,
            "url" to url)
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}

inline fun <reified T> NavBackStackEntry.getObjectNotNull(key: String): T {
    val chartsEncoded = arguments?.getString(key)
    return requireNotNull(Json.decodeFromString(serializer(), URLDecoder.decode(chartsEncoded, "UTF-8")))
}

fun NavController.navigate(screen: Screen, vararg fromToPairs: Pair<String, Any>) =
    this.navigate(Screen.path(screen, *fromToPairs))

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavHost(startScreen: Screen) {
    val navController = rememberNavController()
    val navigateTo = remember(navController) { Actions(navController) }
    NavHost(
        navController = navController,
        startDestination = startScreen.route,
    ) {
        composable(Screen.Inbox.route) { InboxScreen() }
        composable(Screen.STORE_DIRECTORY.route) {
            StoreDirectoryScreen(
                navigateToCategories = navigateTo.navigateToStoreCategories,
                navigateToGenre = navigateTo.navigateToStoreGenre,
                navigateToRoom = navigateTo.navigateToStoreRoom,
                navigateToTopCharts = navigateTo.navigateToStoreCharts,
                navigateToPodcast = navigateTo.navigateToStorePodcast,
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
                navigateToTopCharts = navigateTo.navigateToStoreCharts,
                navigateToPodcast = navigateTo.navigateToStorePodcast,
                navigateUp = navigateTo.navigateUp
            )
        }
        composable(
            Screen.STORE_CHARTS.route,
            arguments = listOf(
                navArgument(NavArgs.Genre) { type = NavType.IntType },
                navArgument(NavArgs.ItemType) { type = NavType.StringType })
        ) { backStackEntry ->
            val genreId = requireNotNull(backStackEntry.arguments?.getInt(NavArgs.Genre))
            val itemType =
                backStackEntry.arguments?.getString(NavArgs.ItemType)?.let { StoreItemType.valueOf(it) }
                    ?: StoreItemType.PODCAST
            TopChartsScreen(
                genreId = genreId,
                storeItemType = itemType,
                navigateToPodcast = navigateTo.navigateToStorePodcast,
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
                navigateUp = navigateTo.navigateUp)
        }
        composable(
            Screen.STORE_PODCAST.route,
            arguments = listOf(navArgument(NavArgs.Url) { type = NavType.StringType })
        ) { backStackEntry ->
            val url = requireNotNull(backStackEntry.arguments?.getString(NavArgs.Url))
            StorePodcastScreen(
                url = url,
                navigateToPodcast = navigateTo.navigateToStorePodcast,
                navigateUp = navigateTo.navigateUp)
        }
    }
}
