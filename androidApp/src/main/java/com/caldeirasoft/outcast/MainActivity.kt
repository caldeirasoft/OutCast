package com.caldeirasoft.outcast

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.navigation.NavArgs
import com.caldeirasoft.outcast.ui.navigation.Route
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomScreen
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsScreen
import com.caldeirasoft.outcast.ui.screen.storepodcast.StorePodcastScreen
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Locale", applicationContext.resources.configuration.locales.get(0).toString())

        super.onCreate(savedInstanceState)
        setContent {
            OutCastTheme {
                val navController = rememberNavController()
                val actions = remember(navController) { Actions(navController) }
                Providers(
                    ActionsAmbient provides actions
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "inbox",
                    ) {
                        composable(Route.Inbox.name) { navBackStackEntry ->
                            Log.d("Route", Route.Inbox.name + " " + Clock.System.now())
                            InboxScreen()
                        }
                        composable(Route.StoreDirectory.name) { navBackStackEntry ->
                            Log.d("Route", Route.StoreDirectory.name + " " + Clock.System.now())
                            StoreDirectoryScreen()
                        }
                        composable(Route.StoreChartsPage.name,
                            arguments = listOf(
                                navArgument(NavArgs.Charts) { type = NavType.StringType })
                        ) { navBackStackEntry ->
                            TopChartsScreen(
                                topCharts = Route.StoreChartsPage.getCharts(navBackStackEntry),
                            )
                        }
                        composable(Route.StoreRoomPage.name,
                            arguments = listOf(
                                navArgument(NavArgs.Room) { type = NavType.StringType })
                        ) { navBackStackEntry ->
                            StoreRoomScreen(
                                storeRoom = Route.StoreRoomPage.getRoom(navBackStackEntry),
                            )
                        }
                        composable(Route.StorePodcast.name,
                            arguments = listOf(
                                navArgument(NavArgs.Url) { type = NavType.StringType })
                        ) { navBackStackEntry ->
                            StorePodcastScreen(
                                url = Route.StorePodcast.getUrl(navBackStackEntry)
                            )
                        }
                    }
                }
            }
        }
    }
}
