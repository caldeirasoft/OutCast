package com.caldeirasoft.outcast

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
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
import com.caldeirasoft.outcast.ui.screen.store.StoreScreen
import com.caldeirasoft.outcast.ui.screen.storepodcast.StorePodcastScreen
import com.caldeirasoft.outcast.ui.screen.storepodcast.StorePodcastViewModel
import com.caldeirasoft.outcast.ui.screen.storeroom.StoreCollectionScreen
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private val storePodcastViewModel by viewModels<StorePodcastViewModel>()

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
                            StoreScreen()
                        }
                        composable(
                            "${Route.StoreCollectionPage.name}/{room}",
                            arguments = listOf(
                                navArgument(NavArgs.Room) { type = NavType.StringType })
                        ) { navBackStackEntry ->
                            StoreCollectionScreen(
                                storeRoom = Route.StoreCollectionPage.getRoom(navBackStackEntry),
                            )
                        }
                        composable(
                            "${Route.StorePodcast.name}/{url}",
                            arguments = listOf(
                                navArgument(NavArgs.Url) { type = NavType.StringType })
                        ) { navBackStackEntry ->
                            StorePodcastScreen(
                                viewModel = storePodcastViewModel,
                                url = Route.StorePodcast.getUrl(navBackStackEntry)
                            )
                        }
                    }
                }
            }
        }
    }
}
