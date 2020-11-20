package com.caldeirasoft.outcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.ambient.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.navigation.NavigationArguments
import com.caldeirasoft.outcast.ui.navigation.Route
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.storedata.StoreDataScreen
import com.caldeirasoft.outcast.ui.screen.storedata.StoreDataViewModel
import com.caldeirasoft.outcast.ui.screen.storedirectory.StoreDirectoryScreen
import com.caldeirasoft.outcast.ui.screen.storedirectory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val inboxViewModel: InboxViewModel by viewModels()
    val storeDirectoryViewModel: StoreDirectoryViewModel by viewModels()
    val storeDataViewModel: StoreDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        println(applicationContext.resources.configuration.locales.get(0))

        super.onCreate(savedInstanceState)
        setContent {
                OutCastTheme {
                    val navController = rememberNavController()
                    val actions = remember(navController) { Actions(navController) }
                    Providers(
                        InboxViewModelAmbient provides inboxViewModel,
                        StoreDirectoryViewModelAmbient provides storeDirectoryViewModel,
                        StoreDataViewModelAmbient provides storeDataViewModel,
                        ActionsAmbient provides actions
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "inbox",
                        ) {
                            composable(Route.Inbox.name) { navBackStackEntry ->
                                InboxScreen()
                            }
                            composable(Route.Discover.name) { navBackStackEntry ->
                                StoreDirectoryScreen()
                            }
                            composable(
                                "${Route.StoreEntry.name}/{url}",
                                arguments = listOf(navArgument(NavigationArguments.Url) {
                                    type = NavType.StringType
                                })
                            ) { navBackStackEntry ->

                                StoreDataScreen(url = Route.StoreEntry.getUrl(navBackStackEntry))
                            }
                        }
                    }
                }
        }
    }
}
