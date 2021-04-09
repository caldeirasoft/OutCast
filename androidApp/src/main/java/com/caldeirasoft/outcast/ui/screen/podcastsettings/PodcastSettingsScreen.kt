package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResourceZero
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewModel
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@Composable
fun PodcastSettingsBottomSheet(viewModel: StorePodcastViewModel, state: StorePodcastViewState) {
    val listState = rememberLazyListState(0)
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.podcast_settings))
            },
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp
            //elevation = if (scrollState.value > 0) 1.dp else 0.dp
        )

        val prefs = state.prefs
        val preferenceKeys = state.podcastPreferenceKeys
        val customEffectsEnabled =
            (prefs?.get(preferenceKeys.customPlaybackEffects) == true)
        PreferenceScreen(
            prefs = prefs,
            viewModel = viewModel,
            items = listOf(
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_new_episodes),
                    summary = stringResource(R.string.settings_new_episodes_desc),
                    key = preferenceKeys.newEpisodes.name,
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    entries = mapOf(
                        "INBOX" to stringResource(R.string.settings_new_episodes_inbox),
                        "QUEUE_NEXT" to stringResource(R.string.settings_new_episodes_queue_next),
                        "QUEUE_LAST" to stringResource(R.string.settings_new_episodes_queue_last),
                        "ARCHIVE" to stringResource(R.string.settings_new_episodes_archive)
                    )
                ),
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_notifications),
                    summary = stringResource(R.string.settings_notifications_desc),
                    key = preferenceKeys.notifications.name,
                    singleLineTitle = true,
                    icon = Icons.Default.Notifications,
                ),
                // episode limit : no limit/1/2/5/10 most recents
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_episode_limit),
                    summary = stringResource(R.string.settings_episode_limit_desc),
                    key = preferenceKeys.episodeLimit.name,
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    defaultValue = "0",
                    entries =
                    listOf(0, 1, 2, 3, 5, 10)
                        .map {
                            it.toString() to quantityStringResourceZero(
                                R.plurals.settings_episode_limit_x_episodes,
                                R.string.settings_episode_no_limit,
                                it, it)
                        }
                        .toMap()
                ),
                // playback effects (custom)
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_playback_effects),
                    summary = stringResource(R.string.settings_playback_effects_desc),
                    key = preferenceKeys.customPlaybackEffects.name,
                    singleLineTitle = true,
                    icon = Icons.Default.Notifications,
                    defaultValue = false,
                ),
                //  -> playback speed
                NumberRangePreferenceItem(
                    title = stringResource(R.string.settings_playback_speed),
                    summary = "",
                    key = preferenceKeys.customPlaybackSpeed.name,
                    singleLineTitle = true,
                    icon = Icons.Default.Speed,
                    visible = customEffectsEnabled,
                    defaultValue = 1.0f,
                    steps = 0.1f,
                    valueRange = 0.5f..3.0f,
                    valueRepresentation = { value -> "%.1f x".format(value) }
                ),
                //  -> trim silence
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_trim_silence),
                    summary = "",
                    key = preferenceKeys.trimSilence.name,
                    singleLineTitle = true,
                    icon = Icons.Default.ContentCut,
                    defaultValue = false,
                    visible = customEffectsEnabled
                ),
                // skip intro
                NumberPreferenceItem(
                    title = stringResource(R.string.settings_skip_intro),
                    summary = "",
                    key = preferenceKeys.skipIntro.name,
                    singleLineTitle = true,
                    icon = Icons.Default.SkipNext,
                    valueRepresentation = { value ->
                        quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                    }
                ),
                // skip end
                NumberPreferenceItem(
                    title = stringResource(R.string.settings_skip_ending),
                    summary = "",
                    key = preferenceKeys.skipEnding.name,
                    singleLineTitle = true,
                    icon = Icons.Default.SkipNext,
                    valueRepresentation = { value ->
                        quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                    }
                ),
                // unfollow
                ActionPreferenceItem(
                    title = stringResource(id = R.string.settings_unfollow),
                    key = "unfollow",
                    singleLineTitle = true,
                    icon = Icons.Default.Unsubscribe,
                    action = {
                        viewModel.unfollow()
                        coroutineScope.launch {
                            drawerState.hide()
                        }
                    }
                )
            ))
    }
}
