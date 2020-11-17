package com.caldeirasoft.outcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.navigation.NavigationArguments
import com.caldeirasoft.outcast.ui.navigation.Route
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDataScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDataViewModel
import com.caldeirasoft.outcast.ui.screen.store.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val model : InboxViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutCastTheme {
                val navController = rememberNavController()
                val actions = remember(navController) { Actions(navController) }
                NavHost(
                    navController = navController,
                    startDestination = "inbox",
                ) {
                    composable(Route.Inbox.name) { navBackStackEntry ->
                        val viewModel: InboxViewModel by viewModels()
                        InboxScreen(
                            scope = lifecycleScope,
                            viewModel = viewModel,
                            navigateToDiscover = actions.navigateToDiscover
                        )
                    }
                    composable(Route.Discover.name) { navBackStackEntry ->
                        val viewModel: StoreDirectoryViewModel by viewModels()
                        StoreDirectoryScreen(
                            viewModel = viewModel,
                            navigateToStoreEntry = actions.navigateToStoreEntry,
                            navigateUp = actions.navigateUp
                        )
                    }
                    composable(
                        "${Route.StoreEntry.name}/{url}",
                        arguments = listOf(navArgument(NavigationArguments.Url) { type = NavType.StringType })
                    ) { navBackStackEntry ->
                        val viewModel: StoreDataViewModel by viewModels()
                        StoreDataScreen(
                            viewModel = viewModel,
                            url = Route.StoreEntry.getUrl(navBackStackEntry),
                            navigateToStoreEntry = actions.navigateToStoreEntry,
                            navigateUp = actions.navigateUp
                        )
                    }
                }
            }
        }
    }
}
