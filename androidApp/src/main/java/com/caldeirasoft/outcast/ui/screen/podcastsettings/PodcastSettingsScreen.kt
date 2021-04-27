package com.caldeirasoft.outcast.ui.screen.podcastsettings

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResourceZero
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastState
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@Composable
fun PodcastSettingsBottomSheet(
    state: PodcastState,
    dataStore: DataStore<Preferences>,
    actioner : (PodcastActions) -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val listState = rememberLazyListState(0)
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val podcastPreferenceKeys = PodcastPreferenceKeys(state.podcast.feedUrl)

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

        PreferenceScreen(
            dataStore = dataStore,
            items = getPreferenceItems(
                context = context,
                podcastPreferenceKeys = podcastPreferenceKeys,
                actioner = actioner
            )
        )
    }
}

@Composable
private fun getPreferenceItems(
    context: Context,
    podcastPreferenceKeys: PodcastPreferenceKeys,
    actioner : (PodcastActions) -> Unit,
) = listOf(
    SingleListPreferenceItem(
        title = stringResource(R.string.settings_new_episodes),
        summary = stringResource(R.string.settings_new_episodes_desc),
        prefKey = podcastPreferenceKeys.newEpisodes,
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
        prefKey = podcastPreferenceKeys.notifications,
        singleLineTitle = true,
        icon = Icons.Default.Notifications,
    ),
    // episode limit : no limit/1/2/5/10 most recents
    SingleListPreferenceItem(
        title = stringResource(R.string.settings_episode_limit),
        summary = stringResource(R.string.settings_episode_limit_desc),
        prefKey = podcastPreferenceKeys.episodeLimit,
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
        prefKey = podcastPreferenceKeys.customPlaybackEffects,
        singleLineTitle = true,
        icon = Icons.Default.Notifications,
        defaultValue = false,
    ),
    //  -> playback speed
    NumberRangeFloatPreferenceItem(
        title = stringResource(R.string.settings_playback_speed),
        summary = "",
        prefKey = podcastPreferenceKeys.customPlaybackSpeed,
        singleLineTitle = true,
        icon = Icons.Default.Speed,
        dependencyKey = podcastPreferenceKeys.customPlaybackEffects,
        defaultValue = 1.0f,
        steps = 0.1f,
        valueRange = 0.5f..3.0f,
        valueRepresentation = { value -> "%.1f x".format(value) }
    ),
    //  -> trim silence
    SwitchPreferenceItem(
        title = stringResource(R.string.settings_trim_silence),
        summary = "",
        prefKey = podcastPreferenceKeys.trimSilence,
        singleLineTitle = true,
        icon = Icons.Default.ContentCut,
        defaultValue = false,
        dependencyKey = podcastPreferenceKeys.customPlaybackEffects
    ),
    // skip intro
    NumberPreferenceItem(
        title = stringResource(R.string.settings_skip_intro),
        summary = "",
        prefKey = podcastPreferenceKeys.skipIntro,
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
        prefKey = podcastPreferenceKeys.skipEnding,
        singleLineTitle = true,
        icon = Icons.Default.SkipNext,
        valueRepresentation = { value ->
            quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
        }
    ),
    // unfollow
    EmptyPreferenceItem(
        title = stringResource(id = R.string.settings_unfollow),
        summary = "",
        singleLineTitle = true,
        icon = Icons.Default.Unsubscribe,
        onClick = {
            actioner(PodcastActions.UnfollowPodcast)
            actioner(PodcastActions.NavigateUp)
        }
    )
)
