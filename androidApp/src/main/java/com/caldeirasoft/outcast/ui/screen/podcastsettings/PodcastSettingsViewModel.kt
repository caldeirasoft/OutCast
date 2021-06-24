package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.data.repository.SettingsRepository
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.Podcast_settingsRouteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<PodcastSettingsViewModel.State, PodcastSettingsViewModel.Event, PodcastSettingsViewModel.Action>(
    initialState = State(
        feedUrl = Podcast_settingsRouteArgs.fromSavedStatedHandle(savedStateHandle).feedUrl
    )
) {

    override fun activate() {
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

    override suspend fun performAction(action: Action) = when (action) {
        is Action.UpdateSettings -> updateSettings(action.settings)
        is Action.NavigateToNewEpisodes -> emitEvent(Event.NavigateToNewEpisodes(feedUrl = initialState.feedUrl))
        is Action.NavigateToEpisodeLimit -> emitEvent(Event.NavigateToEpisodeLimit(feedUrl = initialState.feedUrl))
        is Action.Exit -> emitEvent(Event.Exit)
    }

    private fun updateSettings(podcastSettings: PodcastSettings) {
        viewModelScope.launch {
            settingsRepository.updatePodcastSettings(podcastSettings)
        }
    }

    data class State(
        val feedUrl: String,
        val settings: Settings? = null,
        val podcastSettings: PodcastSettings? = null,
    )

    sealed class Event {
        data class NavigateToNewEpisodes(val feedUrl: String) : Event()
        data class NavigateToEpisodeLimit(val feedUrl: String) : Event()
        object Exit : Event()
    }

    sealed class Action {
        data class UpdateSettings(val settings: PodcastSettings) : Action()
        object NavigateToNewEpisodes : Action()
        object NavigateToEpisodeLimit : Action()
        object Exit : Action()
    }
}