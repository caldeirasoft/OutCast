package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import com.caldeirasoft.outcast.domain.model.NumberPreferenceItem
import com.caldeirasoft.outcast.domain.model.SingleListPreferenceItem
import com.caldeirasoft.outcast.domain.model.SwitchPreferenceItem
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun PodcastSettingsScreen(
    podcastId: Long,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: PodcastSettingsViewModel = mavericksViewModel(initialArgument = podcastId)
    val listState = rememberLazyListState(0)

    PreferenceScreen(dataStore = viewModel.dataStore, items = listOf(
        SingleListPreferenceItem(
            title = "New episodes",
            summary = "When a new episode is released",
            key = "$podcastId:pref_new_episodes",
            singleLineTitle = true,
            icon = Icons.Default.Notifications,
            entries = mapOf(
                "INBOX" to "Add to Inbox",
                "QUEUE_NEXT" to "Queue Next",
                "QUEUE_LAST" to "Queue Last",
                "ARCHIVE" to "Archive"
            )
        ),
        SwitchPreferenceItem(
            title = "Notifications",
            summary = "Get notified of new episodes",
            key = "$podcastId:pref_notify",
            singleLineTitle = true,
            icon = Icons.Default.Notifications,
        ),
        // episode limit : no/1/2/5/10 most recents
        // playback speed
        // trim silence
        NumberPreferenceItem(
            title = "Skip intro",
            summary = "",
            key = "$podcastId:pref_skip_intros",
            singleLineTitle = true,
            icon = Icons.Default.SkipNext,
            valueRepresentation = { value -> "$value seconds" } //TODO: plural
        ),
    ))
}