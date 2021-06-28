package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Unsubscribe
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.episodeLimitOption
import com.caldeirasoft.outcast.domain.enums.EpisodeLimitOptions
import com.caldeirasoft.outcast.domain.enums.NewEpisodesOptions
import com.caldeirasoft.outcast.domain.enums.PodcastEpisodeLimitOptions
import com.caldeirasoft.outcast.domain.model.EmptyPreferenceItem
import com.caldeirasoft.outcast.domain.model.NumberPreferenceItem
import com.caldeirasoft.outcast.domain.model.PreferenceItem
import com.caldeirasoft.outcast.domain.model.SingleListPreferenceItem
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.screen.base.Screen
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
private fun PodcastSettingsScreen(
    state: PodcastSettingsViewModel.State,
    performAction: (PodcastSettingsViewModel.Action) -> Unit,
) {
    state.podcastSettings?.let { podcastSettings ->
        PreferenceScreen(
            title = stringResource(id = R.string.podcast_settings),
            navigateUp = {
                performAction(PodcastSettingsViewModel.Action.Exit)
            },
            items = getPreferenceItems(
                podcastSettings = podcastSettings,
                settings = state.settings,
                onPodcastSettingsChanged = { performAction(PodcastSettingsViewModel.Action.UpdateSettings(it)) },
                performAction = performAction
            )
        )
    }
}

@Composable
private fun getPreferenceItems(
    podcastSettings: PodcastSettings,
    settings: Settings?,
    onPodcastSettingsChanged: (PodcastSettings) -> Unit,
    performAction: (PodcastSettingsViewModel.Action) -> Unit,
): List<PreferenceItem> {
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
        settings?.let { defaultEntries[it.episodeLimitOption] }.orEmpty()

    return listOf(
        SingleListPreferenceItem(
            title = stringResource(R.string.settings_new_episodes),
            summary = stringResource(R.string.settings_new_episodes_desc),
            singleLineTitle = true,
            icon = Icons.Default.Inbox,
            value = podcastSettings.newEpisodesOption,
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
            singleLineTitle = true,
            icon = Icons.Default.Inbox,
            value = podcastSettings.episodeLimitOption,
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
                onPodcastSettingsChanged(podcastSettings.copy(episodeLimit = it.ordinal))
            }
        ),

        // skip intro
        NumberPreferenceItem(
            title = stringResource(R.string.settings_skip_intro),
            summary = "",
            singleLineTitle = true,
            icon = Icons.Default.SkipNext,
            value = podcastSettings.skipIntro,
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
            singleLineTitle = true,
            icon = Icons.Default.SkipNext,
            value = podcastSettings.skipOutro,
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
                performAction(PodcastSettingsViewModel.Action.UnfollowPodcast)
                performAction(PodcastSettingsViewModel.Action.Exit)
            }
        )
    )
}
