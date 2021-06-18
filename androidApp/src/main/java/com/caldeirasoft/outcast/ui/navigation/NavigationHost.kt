package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.InboxScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.PlayedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.episodes.base.SavedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsScreen
import com.caldeirasoft.outcast.ui.screen.search.SearchScreen
import com.caldeirasoft.outcast.ui.screen.search_results.SearchResultsScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.Routes
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataScreen
import com.caldeirasoft.outcast.ui.util.getObject
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationHost() {
    val navController = rememberNavController()

    ModalBottomSheetHost {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            bottomBar = {
                SetupBottomNavBar(
                    navController = navController
                )
            })
        {
            NavHost(
                navController = navController,
                startDestination = Routes.inbox.path,
            ) {
                composable(Routes.library.path) {
                    // library
                    LibraryScreen(
                        viewModel = hiltViewModel(),
                        navController = navController)
                }
                composable(Routes.store.path) {
                    // discover
                    StoreDataScreen(
                        viewModel = hiltViewModel(),
                        navController = navController
                    )
                }
                composable(
                    route = Routes.store.path,
                    arguments = Routes.store.navArgs
                ) {
                    StoreDataScreen(
                        viewModel = hiltViewModel(),
                        navController = navController
                    )
                }
                composable(Routes.search.path) {
                    // search
                    SearchScreen(
                        navController = navController)
                }
                composable(Routes.search_results.path) {
                    // search
                    SearchResultsScreen(
                        viewModel = hiltViewModel(),
                        navController = navController)
                }
                composable(ScreenName.PROFILE.name) {
                    Text(text = "Profile")
                }
                composable(
                    route = Routes.podcast.path,
                    arguments = Routes.podcast.navArgs
                ) {
                    val storePodcast = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getObject<StorePodcast>("podcast")

                    PodcastScreen(
                        viewModel = hiltViewModel(),
                        storePodcast = storePodcast,
                        navController = navController)
                }
                composable(
                    route = Routes.episode.path,
                    arguments = Routes.episode.navArgs
                ) { _ ->
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
                        viewModel = hiltViewModel(),
                        storeEpisode = storeEpisode,
                        fromSamePodcast = fromSamePodcast,
                        navController = navController
                    )
                }
                composable(
                    route = Routes.podcast_settings.path,
                    arguments = Routes.podcast_settings.navArgs
                ) {
                    PodcastSettingsScreen(
                        viewModel = hiltViewModel(),
                        navController = navController
                    )
                }
                composable(Routes.inbox.path) {
                    InboxScreen(
                        viewModel = hiltViewModel(),
                        navController = navController)
                }
                composable(Routes.saved_episodes.path) {
                    SavedEpisodesScreen(
                        viewModel = hiltViewModel(),
                        navController = navController)
                }
                composable(Routes.played_episodes.path) {
                    PlayedEpisodesScreen(
                        viewModel = hiltViewModel(),
                        navController = navController)
                }
            }
        }
    }
}
