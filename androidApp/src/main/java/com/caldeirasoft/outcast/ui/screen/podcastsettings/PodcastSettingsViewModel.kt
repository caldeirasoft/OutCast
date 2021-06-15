package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.SettingsRepository
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeViewState
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodesEvent
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.Podcast_settingsRouteArgs
import com.caldeirasoft.outcast.ui.util.urlDecode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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