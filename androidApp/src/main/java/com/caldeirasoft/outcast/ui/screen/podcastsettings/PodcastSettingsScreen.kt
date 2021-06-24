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
    name = "podcast_settings",
    args = [
        RouteArg("feedUrl", RouteArgType.StringType, false),
    ]
)
@Composable
fun PodcastSettingsScreen(
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
        PodcastSettingsScreen(
            state = state,
            performAction = performAction)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun PodcastSettingsScreen(
    state: PodcastSettingsViewModel.State,
    performAction: (PodcastSettingsViewModel.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.podcast_settings))
                },
                navigationIcon = {
                    IconButton(onClick = { performAction(PodcastSettingsViewModel.Action.Exit) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
                //elevation = if (scrollState.value > 0) 1.dp else 0.dp
            )
        })
    {
        state.podcastSettings?.let { settings ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    ListPreference(
                        title = stringResource(R.string.settings_new_episodes),
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
                            performAction(PodcastSettingsViewModel.Action.UpdateSettings(settings.copy(newEpisodes = it.ordinal)))
                        }
                    )
                }
                item {
                    ListPreference(
                        title = stringResource(R.string.settings_episode_limit),
                        singleLineTitle = true,
                        icon = Icons.Default.Inbox,
                        value = settings.episodeLimitOption,
                        entries = mapOf(
                            PodcastEpisodeLimitOptions.DEFAULT_SETTING to stringResource(
                                id = R.string.settings_episode_limit_default_x,
                            ),
                            PodcastEpisodeLimitOptions.OFF to stringResource(id = R.string.settings_episode_no_limit),
                            PodcastEpisodeLimitOptions.MOST_RECENT to quantityStringResource(
                                R.plurals.settings_episode_limit_x_episodes,
                                1, 1),
                            PodcastEpisodeLimitOptions.MOST_2_RECENT to quantityStringResource(
                                R.plurals.settings_episode_limit_x_episodes,
                                2, 2),
                            PodcastEpisodeLimitOptions.MOST_3_RECENT to quantityStringResource(
                                R.plurals.settings_episode_limit_x_episodes,
                                3, 3),
                            PodcastEpisodeLimitOptions.MOST_5_RECENT to quantityStringResource(
                                R.plurals.settings_episode_limit_x_episodes,
                                5, 5),
                            PodcastEpisodeLimitOptions.MOST_10_RECENT to quantityStringResource(
                                R.plurals.settings_episode_limit_x_episodes,
                                10, 10),
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
                            performAction(PodcastSettingsViewModel.Action.UpdateSettings(settings.copy(episodeLimit = it.ordinal)))
                        }
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(R.string.settings_notifications),
                        summary = stringResource(R.string.settings_notifications_desc),
                        singleLineTitle = true,
                        icon = Icons.Default.Notifications,
                        value = settings.notifications,
                        onValueChanged = {
                            performAction(PodcastSettingsViewModel.Action.UpdateSettings(settings.copy(notifications = it)))
                        }
                    )
                }
                item {
                    // skip intro
                    NumberPreference(
                        title = stringResource(R.string.settings_skip_intro),
                        singleLineTitle = true,
                        icon = Icons.Default.SkipNext,
                        value = settings.skipIntro,
                        valueRepresentation = { value ->
                            quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                        },
                        onValueChanged = {
                            performAction(PodcastSettingsViewModel.Action.UpdateSettings(settings.copy(skipIntro = it)))
                        }
                    )
                }
                item {
                    // skip end
                    NumberPreference(
                        title = stringResource(R.string.settings_skip_ending),
                        singleLineTitle = true,
                        icon = Icons.Default.SkipNext,
                        value = settings.skipOutro,
                        valueRepresentation = { value ->
                            quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                        },
                        onValueChanged = {
                            performAction(PodcastSettingsViewModel.Action.UpdateSettings(settings.copy(skipOutro = it)))
                        }
                    )
                }
                item {
                    // unfollow
                    Preference(
                        title = stringResource(id = R.string.settings_unfollow),
                        summary = "",
                        singleLineTitle = true,
                        icon = Icons.Default.Unsubscribe,
                        onClick = {
                            //actioner(PodcastActions.UnfollowPodcast)
                            //actioner(PodcastActions.NavigateUp)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun getPreferenceItems(
    podcastSettings: PodcastSettings,
    settings: Settings?,
    onPodcastSettingsChanged: (PodcastSettings) -> Unit,
) = listOf(
    SingleListPreferenceItem(
        title = stringResource(R.string.settings_new_episodes),
        summary = stringResource(R.string.settings_new_episodes_desc),
        prefKey = "newEpisodes",
        singleLineTitle = true,
        icon = Icons.Default.Inbox,
        defaultValue = podcastSettings.newEpisodesOption,
        entries = mapOf(
            NewEpisodesOptions.ADD_TO_INBOX to stringResource(R.string.settings_new_episodes_inbox),
            NewEpisodesOptions.ADD_TO_QUEUE_NEXT to stringResource(R.string.settings_new_episodes_queue_next),
            NewEpisodesOptions.ADD_TO_QUEUE_LAST to stringResource(R.string.settings_new_episodes_queue_last),
            NewEpisodesOptions.ARCHIVE to stringResource(R.string.settings_new_episodes_archive)
        ),
        onValueChanged = {
            onPodcastSettingsChanged(podcastSettings.copy(newEpisodes = it.ordinal))
        }
    ),
    // episode limit : no limit/1/2/5/10 most recents
    SingleListPreferenceItem(
        title = stringResource(R.string.settings_episode_limit),
        summary = stringResource(R.string.settings_episode_limit_desc),
        prefKey = "episodeLimit",
        singleLineTitle = true,
        icon = Icons.Default.Inbox,
        defaultValue = podcastSettings.episodeLimitOption,
        entries = mapOf(
            PodcastEpisodeLimitOptions.DEFAULT_SETTING to stringResource(
                id = R.string.settings_episode_limit_default_x,
            ),
            PodcastEpisodeLimitOptions.OFF to stringResource(id = R.string.settings_episode_no_limit),
            PodcastEpisodeLimitOptions.MOST_RECENT to quantityStringResource(
                R.plurals.settings_episode_limit_x_episodes,
                1, 1),
            PodcastEpisodeLimitOptions.MOST_2_RECENT to quantityStringResource(
                R.plurals.settings_episode_limit_x_episodes,
                2, 2),
            PodcastEpisodeLimitOptions.MOST_3_RECENT to quantityStringResource(
                R.plurals.settings_episode_limit_x_episodes,
                3, 3),
            PodcastEpisodeLimitOptions.MOST_5_RECENT to quantityStringResource(
                R.plurals.settings_episode_limit_x_episodes,
                5, 5),
            PodcastEpisodeLimitOptions.MOST_10_RECENT to quantityStringResource(
                R.plurals.settings_episode_limit_x_episodes,
                10, 10),
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
            onPodcastSettingsChanged(podcastSettings.copy(episodeLimit = it.ordinal))
        }
    ),

    SwitchPreferenceItem(
        title = stringResource(R.string.settings_notifications),
        summary = stringResource(R.string.settings_notifications_desc),
        prefKey = "notifications",
        singleLineTitle = true,
        icon = Icons.Default.Notifications,
        defaultValue = podcastSettings.notifications,
        onValueChanged = {
            onPodcastSettingsChanged(podcastSettings.copy(notifications = it))
        }
    ),

    // skip intro
    NumberPreferenceItem(
        title = stringResource(R.string.settings_skip_intro),
        summary = "",
        prefKey = "skipIntro",
        singleLineTitle = true,
        icon = Icons.Default.SkipNext,
        defaultValue = podcastSettings.skipIntro,
        valueRepresentation = { value ->
            quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
        },
        onValueChanged = {
            onPodcastSettingsChanged(podcastSettings.copy(skipIntro = it))
        }
    ),
    // skip end
    NumberPreferenceItem(
        title = stringResource(R.string.settings_skip_ending),
        summary = "",
        prefKey = "skipOutro",
        singleLineTitle = true,
        icon = Icons.Default.SkipNext,
        defaultValue = podcastSettings.skipOutro,
        valueRepresentation = { value ->
            quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
        },
        onValueChanged = {
            onPodcastSettingsChanged(podcastSettings.copy(skipOutro = it))
        }
    ),
    // unfollow
    EmptyPreferenceItem(
        title = stringResource(id = R.string.settings_unfollow),
        summary = "",
        singleLineTitle = true,
        icon = Icons.Default.Unsubscribe,
        onClick = {
            //actioner(PodcastActions.UnfollowPodcast)
            //actioner(PodcastActions.NavigateUp)
        }
    )
)
