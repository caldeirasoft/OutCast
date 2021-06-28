package com.caldeirasoft.outcast.ui.screen.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.backgroundSyncOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.deletePlayedEpisodesDelayOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.episodeLimitOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.externalControlsOptions
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.notificationsBadgeTypeOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.skipBackButtonOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.skipForwardButtonOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.streamOnMobileDataOption
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.themeOptions
import com.caldeirasoft.outcast.domain.enums.*
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.screen.base.Screen
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, InternalCoroutinesApi::class)
@Route(
    name = "settings",
)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
) {
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is SettingsViewModel.Event.Exit ->
                    navController.navigateUp()
            }
        }
    ) {  state, performAction ->
        SettingsScreen(
            state = state,
            performAction = performAction)
    }
}

@Composable
private fun SettingsScreen(
    state: SettingsViewModel.State,
    performAction: (SettingsViewModel.Action) -> Unit,
) {
    state.settings?.let { settings ->
        PreferenceScreen(
            title = stringResource(id = R.string.settings),
            navigateUp = {
                performAction(SettingsViewModel.Action.Exit)
            },
            items = getPreferenceItems(
                settings = settings,
                onSettingsChanged = { performAction(SettingsViewModel.Action.UpdateSettings(it)) },
                performAction = performAction
            )
        )
    }
}

@Composable
private fun getPreferenceItems(
    settings: Settings,
    onSettingsChanged: (Settings) -> Unit,
    performAction: (SettingsViewModel.Action) -> Unit,
): List<BasePreferenceItem> {
    return listOf(
        PreferenceGroupItem(
            title = stringResource(R.string.settings_general),
            items = listOf(
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_background_refresh),
                    summary = stringResource(R.string.settings_background_refresh_desc),
                    singleLineTitle = true,
                    icon = Icons.Default.Sync,
                    value = settings.backgroundSyncOption,
                    entries = mapOf(
                        BackgroundRefreshOptions.EVERY_1_HOUR to stringResource(R.string.settings_background_refresh_every_one_hour),
                        BackgroundRefreshOptions.EVERY_6_HOUR to stringResource(R.string.settings_background_refresh_every_six_hours),
                        BackgroundRefreshOptions.EVERY_1_DAY to stringResource(R.string.settings_background_refresh_every_one_day),
                        BackgroundRefreshOptions.MANUALLY to stringResource(R.string.settings_background_refresh_manually)
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(backgroundSync = it.ordinal))
                    }
                ),
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_background_sync_with_cloud),
                    summary = "",
                    icon = Icons.Default.Sync,
                    value = settings.syncWithCloud,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(syncWithCloud = it))
                    }
                ),
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_background_refresh_on_mobile_data),
                    summary = stringResource(id = R.string.settings_background_refresh_on_mobile_data),
                    icon = Icons.Default.Sync,
                    value = settings.syncOnMobileData,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(syncOnMobileData = it))
                    }
                ),
                // episode limit : no limit/1/2/5/10 most recents
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_episode_limit),
                    summary = stringResource(R.string.settings_episode_limit_desc),
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    value = settings.episodeLimitOption,
                    entries = mapOf(
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
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(episodeLimit = it.ordinal))
                    }
                ),
            )
        ),
        PreferenceGroupItem(
            title = stringResource(R.string.settings_notifications),
            items = listOf(
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_notifications_allow),
                    summary = stringResource(id = R.string.settings_notifications_desc),
                    icon = Icons.Default.Notifications,
                    value = settings.allowNotifications,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(allowNotifications = it))
                    }
                ),
                MultiListPreferenceItem(
                    title = stringResource(R.string.settings_badge),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Badge,
                    value = settings.notificationsBadgeTypeOption,
                    entries = mapOf(
                        NotificationsBadgeOptions.INBOX_COUNT to stringResource(id = R.string.settings_badge_inbox),
                        NotificationsBadgeOptions.QUEUE_COUNT to stringResource(id = R.string.settings_badge_queue),
                    ),
                    onValueChanged = {
                        it.map { notificationsBadgeOptions ->  notificationsBadgeOptions.id }
                            .sum()
                            .let { sum ->
                                onSettingsChanged(settings.copy(notificationsBadgeType = sum))
                            }
                    }
                ),
            )
        ),
        PreferenceGroupItem(
            title = stringResource(R.string.settings_downloads),
            items = listOf(
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_downloads_queue_episodes),
                    summary = "",
                    icon = Icons.Default.Download,
                    value = settings.downloadQueuedEpisodes,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(downloadQueuedEpisodes = it))
                    }
                ),
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_background_refresh),
                    summary = stringResource(R.string.settings_background_refresh_desc),
                    singleLineTitle = true,
                    icon = Icons.Default.Sync,
                    value = settings.backgroundSyncOption,
                    entries = mapOf(
                        BackgroundRefreshOptions.EVERY_1_HOUR to stringResource(R.string.settings_background_refresh_every_one_hour),
                        BackgroundRefreshOptions.EVERY_6_HOUR to stringResource(R.string.settings_background_refresh_every_six_hours),
                        BackgroundRefreshOptions.EVERY_1_DAY to stringResource(R.string.settings_background_refresh_every_one_day),
                        BackgroundRefreshOptions.MANUALLY to stringResource(R.string.settings_background_refresh_manually)
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(backgroundSync = it.ordinal))
                    }
                ),
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_downloads_delete_episodes),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Sync,
                    value = settings.deletePlayedEpisodesDelayOption,
                    entries = mapOf(
                        DeleteEpisodesDelayOptions.AFTER_LISTENING to stringResource(R.string.settings_downloads_delete_episodes_after_listening),
                        DeleteEpisodesDelayOptions.AFTER_1_DAY to stringResource(R.string.settings_downloads_delete_episodes_after_one_day),
                        DeleteEpisodesDelayOptions.AFTER_7_DAYS to stringResource(R.string.settings_downloads_delete_episodes_after_7_days),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(deletePlayedEpisodesDelay = it.ordinal))
                    }
                ),
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_downloads_starred_episodes),
                    summary = "",
                    icon = Icons.Default.Download,
                    value = settings.downloadStarredEpisodes,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(downloadStarredEpisodes = it))
                    }
                ),
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_downloads_on_mobile_data),
                    summary = "",
                    icon = Icons.Default.Download,
                    value = settings.downloadOnMobileData,
                    onValueChanged = {
                        onSettingsChanged(settings.copy(downloadOnMobileData = it))
                    }
                ),
            ),
        ),
        PreferenceGroupItem(
            title = stringResource(R.string.settings_playback),
            items = listOf(
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_playback_skip_forward),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Forward30,
                    value = settings.skipForwardButtonOption,
                    entries = mapOf(
                        SkipOptions.SKIP_10_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 10, 10),
                        SkipOptions.SKIP_15_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 15, 15),
                        SkipOptions.SKIP_30_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 30, 30),
                        SkipOptions.SKIP_45_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 45, 45),
                        SkipOptions.SKIP_60_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 60, 60),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(skipForwardButton = it.ordinal))
                    }
                ),
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_playback_skip_back),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Replay10,
                    value = settings.skipBackButtonOption,
                    entries = mapOf(
                        SkipOptions.SKIP_10_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 10, 10),
                        SkipOptions.SKIP_15_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 15, 15),
                        SkipOptions.SKIP_30_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 30, 30),
                        SkipOptions.SKIP_45_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 45, 45),
                        SkipOptions.SKIP_60_SECONDS to quantityStringResource(R.plurals.settings_skip_x_seconds, 60, 60),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(skipBackButton = it.ordinal))
                    }
                ),
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_playback_external_controls),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Headphones,
                    value = settings.externalControlsOptions,
                    entries = mapOf(
                        ExternalControlsOptions.SKIP_FORWARD_BACK to stringResource(R.string.settings_playback_external_controls_skip),
                        ExternalControlsOptions.SKIP_NEXT_PREVIOUS to stringResource(R.string.settings_playback_external_controls_next_prev),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(externalControls = it.ordinal))
                    }
                ),
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_playback_stream_on_mobile_data),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Stream,
                    value = settings.streamOnMobileDataOption,
                    entries = mapOf(
                        StreamOptions.PLAY to stringResource(R.string.settings_playback_stream_play),
                        StreamOptions.ASK_CONFIRM to stringResource(R.string.settings_playback_stream_ask_confirm),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(externalControls = it.ordinal))
                    }
                ),
            ),
        ),
        PreferenceGroupItem(
            title = stringResource(R.string.settings_theme),
            items = listOf(
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_theme),
                    summary = "",
                    singleLineTitle = true,
                    icon = Icons.Default.Stream,
                    value = settings.themeOptions,
                    entries = mapOf(
                        Theme.LIGHT to stringResource(R.string.settings_theme_light),
                        Theme.DARK to stringResource(R.string.settings_theme_dark),
                        Theme.AUTO to stringResource(R.string.settings_theme_system),
                    ),
                    onValueChanged = {
                        onSettingsChanged(settings.copy(theme = it.ordinal))
                    }
                ),
            ),
        ),
        PreferenceGroupItem(
            title = stringResource(R.string.settings_import_export),
            items = listOf(
                EmptyPreferenceItem(
                    title = stringResource(id = R.string.settings_import_opml),
                    summary = "",
                    icon = Icons.Default.ImportExport,
                    onClick = {

                    }
                ),
                EmptyPreferenceItem(
                    title = stringResource(id = R.string.settings_export_opml),
                    summary = "",
                    icon = Icons.Default.ImportExport,
                    onClick = {

                    }
                )
            ),
        ),
        /*PreferenceGroupItem(
            title = stringResource(R.string.settings_about),
            items = listOf(
            ),
        ),*/
    )
}
