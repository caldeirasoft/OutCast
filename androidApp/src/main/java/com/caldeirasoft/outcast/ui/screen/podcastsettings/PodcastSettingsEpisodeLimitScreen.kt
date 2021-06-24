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
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.episodeLimitOption
import com.caldeirasoft.outcast.domain.enums.EpisodeLimitOptions
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
    name = "podcast_settings_episode_limit",
    args = [
        RouteArg("feedUrl", RouteArgType.StringType, false),
    ]
)
@Composable
fun PodcastSettingsEpisodeLimitScreen(
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
        PodcastSettingsEpisodeLimitScreen(
            state = state,
            performAction = performAction)
    }
}

@ExperimentalCoroutinesApi
@Composable
private fun PodcastSettingsEpisodeLimitScreen(
    state: PodcastSettingsViewModel.State,
    performAction: (PodcastSettingsViewModel.Action) -> Unit,
) {
    ScaffoldWithLargeHeaderAndLazyColumn(
        title = stringResource(id = R.string.settings_episode_limit),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        navigateUp = {
            performAction(PodcastSettingsViewModel.Action.Exit)
        },
    ) {
        state.podcastSettings?.let { settings ->
            item {
                val defaultEntries = mapOf(
                    EpisodeLimitOptions.OFF to stringResource(id = R.string.settings_episode_no_limit),
                    EpisodeLimitOptions.MOST_RECENT to quantityStringResource(
                        R.plurals.settings_episode_limit_x_episodes,
                        1, 1
                    ),
                    EpisodeLimitOptions.MOST_2_RECENT to quantityStringResource(
                        R.plurals.settings_episode_limit_x_episodes,
                        2, 2
                    ),
                    EpisodeLimitOptions.MOST_3_RECENT to quantityStringResource(
                        R.plurals.settings_episode_limit_x_episodes,
                        3, 3
                    ),
                    EpisodeLimitOptions.MOST_5_RECENT to quantityStringResource(
                        R.plurals.settings_episode_limit_x_episodes,
                        5, 5
                    ),
                    EpisodeLimitOptions.MOST_10_RECENT to quantityStringResource(
                        R.plurals.settings_episode_limit_x_episodes,
                        10, 10
                    ),
                    EpisodeLimitOptions.ONE_DAY to stringResource(
                        id = R.string.settings_episode_limit_one_day
                    ),
                    EpisodeLimitOptions.ONE_WEEK to stringResource(
                        id = R.string.settings_episode_limit_one_week
                    ),
                    EpisodeLimitOptions.TWO_WEEKS to stringResource(
                        id = R.string.settings_episode_limit_two_weeks
                    ),
                    EpisodeLimitOptions.ONE_MONTH to stringResource(
                        id = R.string.settings_episode_limit_one_month
                    ),
                )
                val defaultLimitOptionsText =
                    state.settings?.let { defaultEntries[it.episodeLimitOption] }.orEmpty()
                ListPreference(
                    summary = stringResource(R.string.settings_episode_limit_desc),
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    value = settings.episodeLimitOption,
                    entries = mapOf(
                        PodcastEpisodeLimitOptions.DEFAULT_SETTING to stringResource(
                            id = R.string.settings_episode_limit_default_x,
                            defaultLimitOptionsText
                        ),
                        PodcastEpisodeLimitOptions.OFF to stringResource(id = R.string.settings_episode_no_limit),
                        PodcastEpisodeLimitOptions.MOST_RECENT to quantityStringResource(
                            R.plurals.settings_episode_limit_x_episodes,
                            1, 1
                        ),
                        PodcastEpisodeLimitOptions.MOST_2_RECENT to quantityStringResource(
                            R.plurals.settings_episode_limit_x_episodes,
                            2, 2
                        ),
                        PodcastEpisodeLimitOptions.MOST_3_RECENT to quantityStringResource(
                            R.plurals.settings_episode_limit_x_episodes,
                            3, 3
                        ),
                        PodcastEpisodeLimitOptions.MOST_5_RECENT to quantityStringResource(
                            R.plurals.settings_episode_limit_x_episodes,
                            5, 5
                        ),
                        PodcastEpisodeLimitOptions.MOST_10_RECENT to quantityStringResource(
                            R.plurals.settings_episode_limit_x_episodes,
                            10, 10
                        ),
                        PodcastEpisodeLimitOptions.ONE_DAY to stringResource(
                            id = R.string.settings_episode_limit_one_day
                        ),
                        PodcastEpisodeLimitOptions.ONE_WEEK to stringResource(
                            id = R.string.settings_episode_limit_one_week
                        ),
                        PodcastEpisodeLimitOptions.TWO_WEEKS to stringResource(
                            id = R.string.settings_episode_limit_two_weeks
                        ),
                        PodcastEpisodeLimitOptions.ONE_MONTH to stringResource(
                            id = R.string.settings_episode_limit_one_month
                        ),
                    ),
                    onValueChanged = {
                        performAction(
                            PodcastSettingsViewModel.Action.UpdateSettings(
                                settings.copy(
                                    episodeLimit = it.ordinal
                                )
                            )
                        )
                    }
                )
            }

        }
    }
}
