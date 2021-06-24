package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.domain.enums.NewEpisodesOptions
import com.caldeirasoft.outcast.domain.enums.PodcastEpisodeLimitOptions
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.domain.model.NumberPreferenceItem
import com.caldeirasoft.outcast.ui.components.ScaffoldWithLargeHeaderAndLazyColumn
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.preferences.*
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.screen.search_results.SearchResultsViewModel
import com.caldeirasoft.outcast.ui.util.navigateToEpisode
import com.caldeirasoft.outcast.ui.util.navigateToPodcast
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route
import cz.levinzonr.router.core.RouteArg
import cz.levinzonr.router.core.RouteArgType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, InternalCoroutinesApi::class)
@Route(
    name = "podcast_settings_new_episodes",
    args = [
        RouteArg("feedUrl", RouteArgType.StringType, false),
    ]
)
@Composable
fun PodcastSettingsNewEpisodesScreen(
    viewModel: PodcastSettingsViewModel,
    navController: NavController,
) {
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is PodcastSettingsViewModel.Event.Exit ->
                    navController.navigateUp()
            }
        }
    ) {  state, performAction ->
        PodcastSettingsNewEpisodesScreen(
            state = state,
            performAction = performAction)
    }
}

@ExperimentalCoroutinesApi
@Composable
private fun PodcastSettingsNewEpisodesScreen(
    state: PodcastSettingsViewModel.State,
    performAction: (PodcastSettingsViewModel.Action) -> Unit,
) {
    ScaffoldWithLargeHeaderAndLazyColumn(
        title = stringResource(id = R.string.settings_new_episodes),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        navigateUp = {
            performAction(PodcastSettingsViewModel.Action.Exit)
        },
    ) {
        state.podcastSettings?.let { settings ->
            item {
                ListPreference(
                    summary = stringResource(R.string.settings_new_episodes_desc),
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    value = settings.newEpisodesOption,
                    entries = mapOf(
                        NewEpisodesOptions.ADD_TO_INBOX to stringResource(R.string.settings_new_episodes_inbox),
                        NewEpisodesOptions.ADD_TO_QUEUE_NEXT to stringResource(R.string.settings_new_episodes_queue_next),
                        NewEpisodesOptions.ADD_TO_QUEUE_LAST to stringResource(R.string.settings_new_episodes_queue_last),
                        NewEpisodesOptions.ARCHIVE to stringResource(R.string.settings_new_episodes_archive)
                    ),
                    onValueChanged = {
                        performAction(
                            PodcastSettingsViewModel.Action.UpdateSettings(
                                settings.copy(
                                    newEpisodes = it.ordinal
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}