package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.repository.SettingsRepository
import com.caldeirasoft.outcast.ui.screen.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.Podcast_settingsRouteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<PodcastSettingsState>(
    initialState = PodcastSettingsState(
        feedUrl = Podcast_settingsRouteArgs.fromSavedStatedHandle(savedStateHandle).feedUrl
    )
) {

    init {
        settingsRepository.getSettings().setOnEach {
            copy(settings = it)
        }

        settingsRepository.getPodcastSettings(initialState.feedUrl)
            .setOnEach {
                copy(
                    podcastSettings = it
                )
            }
    }

    fun updateSettings(podcastSettings: PodcastSettings) {
        viewModelScope.launch {
            settingsRepository.updatePodcastSettings(podcastSettings)
        }
    }
}