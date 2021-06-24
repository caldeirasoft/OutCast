package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.*
import androidx.navigation.navigation
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetHost
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeScreen
import com.caldeirasoft.outcast.ui.screen.episodelist.base.InboxScreen
import com.caldeirasoft.outcast.ui.screen.library.LibraryScreen
import com.caldeirasoft.outcast.ui.screen.played_episodes.PlayedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsEpisodeLimitScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsNewEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsScreen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsViewModel
import com.caldeirasoft.outcast.ui.screen.saved_episodes.SavedEpisodesScreen
import com.caldeirasoft.outcast.ui.screen.search.SearchScreen
import com.caldeirasoft.outcast.ui.screen.search_results.SearchResultsScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.DiscoverScreen
import com.caldeirasoft.outcast.ui.screen.store.storedata.Routes
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreScreen
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
                startDestination = RootScreen.Inbox.route,
            ) {
                addInbox(navController)
                addLibrary(navController)
                addDiscover(navController)
                addSearch(navController)
                addEpisode(navController)
                addPodcast(navController)
                addPodcastSettings(navController)
                addPodcastSettingsNewEpisodes(navController)
                addPodcastSettingsEpisodeLimit(navController)
                addSavedEpisodes(navController)
                addPlayedEpisodes(navController)
                addStore(navController)
                addSearchResults(navController)
            }
        }
    }
}

@FlowPreview
private fun NavGraphBuilder.addInbox(navController: NavController) {
    composable(Routes.inbox.path) {
        InboxScreen(
            viewModel = hiltViewModel(),
            navController = navController)
    }
}

@FlowPreview
private fun NavGraphBuilder.addLibrary(navController: NavController) {
    composable(Routes.library.path) {
        // library
        LibraryScreen(
            viewModel = hiltViewModel(),
            navController = navController)
    }
}

@FlowPreview
private fun NavGraphBuilder.addPodcast(navController: NavController) {
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
}

@FlowPreview
private fun NavGraphBuilder.addEpisode(navController: NavController) {
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
}

@FlowPreview
private fun NavGraphBuilder.addPodcastSettings(navController: NavController) {
    composable(
        route = Routes.podcast_settings.path,
        arguments = Routes.podcast_settings.navArgs
    ) {
        PodcastSettingsScreen(
            viewModel = hiltViewModel(),
            navController = navController
        )
    }
}

@FlowPreview
private fun NavGraphBuilder.addPodcastSettingsNewEpisodes(navController: NavController) {
    composable(
        route = Routes.podcast_settings_new_episodes.path,
        arguments = Routes.podcast_settings_new_episodes.navArgs
    ) {
        val parentViewModel = hiltViewModel<PodcastSettingsViewModel>(
            navController.getBackStackEntry(Routes.podcast_settings.path)
        )
        PodcastSettingsNewEpisodesScreen(
            viewModel = parentViewModel,
            navController = navController
        )
    }
}

@FlowPreview
private fun NavGraphBuilder.addPodcastSettingsEpisodeLimit(navController: NavController) {
    composable(
        route = Routes.podcast_settings_episode_limit.path,
        arguments = Routes.podcast_settings_episode_limit.navArgs
    ) {
        val parentViewModel = hiltViewModel<PodcastSettingsViewModel>(
            navController.getBackStackEntry(Routes.podcast_settings.path)
        )
        PodcastSettingsEpisodeLimitScreen(
            viewModel = parentViewModel,
            navController = navController
        )
    }
}

@FlowPreview
private fun NavGraphBuilder.addPlayedEpisodes(navController: NavController) {
    composable(Routes.played_episodes.path) {
        PlayedEpisodesScreen(
            viewModel = hiltViewModel(),
            navController = navController)
    }
}

@FlowPreview
private fun NavGraphBuilder.addSavedEpisodes(navController: NavController) {
    composable(Routes.saved_episodes.path) {
        SavedEpisodesScreen(
            viewModel = hiltViewModel(),
            navController = navController)
    }
}

@FlowPreview
private fun NavGraphBuilder.addDiscover(navController: NavController) {
    composable(
        route = Routes.discover.path,
    ) {
        DiscoverScreen(
            viewModel = hiltViewModel(),
            navController = navController
        )
    }
}

@FlowPreview
private fun NavGraphBuilder.addStore(navController: NavController) {
    composable(
        route = Routes.store.path,
        arguments = Routes.store.navArgs
    ) {
        StoreScreen(
            viewModel = hiltViewModel(),
            navController = navController
        )
    }
}

@FlowPreview
private fun NavGraphBuilder.addSearch(navController: NavController) {
    composable(Routes.search.path) {
        // search
        SearchScreen(
            navController = navController)
    }
}

@FlowPreview
private fun NavGraphBuilder.addSearchResults(navController: NavController) {
    composable(Routes.search_results.path) {
        // search
        SearchResultsScreen(
            viewModel = hiltViewModel(),
            navController = navController)
    }
}