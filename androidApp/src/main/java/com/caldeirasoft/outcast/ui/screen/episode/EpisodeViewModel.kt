package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadEpisodeFromDbUseCase
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.urlDecode
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadEpisodeFromDbUseCase: LoadEpisodeFromDbUseCase,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
) : BaseViewModelEvents<EpisodeViewState, EpisodeEvent>(
    // The string "episode" is the name of the argument in the route
    EpisodeViewState(
        feedUrl = savedStateHandle.get<String>("feedUrl").orEmpty(),
        guid = savedStateHandle.get<String>("guid")?.urlDecode().orEmpty(),
    )
) {

    private var isInitialized: Boolean = false
    private var isEpisodeSet: Boolean = false

    init {
        val initialState = state.value
        viewModelScope.launch {
            loadEpisodeFromDbUseCase
                .execute(
                    feedUrl = initialState.feedUrl,
                    guid = initialState.guid
                )
                .filterNotNull()
                .setOnEach {
                    copy(
                        isLoading = false,
                        episode = it.episode,
                        podcast = it.podcast
                    )
                }
        }
    }

    fun setEpisode(storeEpisode: StoreEpisode) {
        if (!isEpisodeSet) {
            fetchPodcastDataUseCase
                .execute(storeEpisode.storePodcast.podcast)
                .onStart {
                    isEpisodeSet = true
                    setState {
                        copy(
                            isLoading = true,
                            episode = storeEpisode.episode,
                            podcast = storeEpisode.storePodcast.podcast
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun openPodcastDetails() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(EpisodeEvent.OpenPodcastDetail(podcast))
            }
        }
    }

    fun playEpisode() {

    }

    fun playNext() {

    }

    fun playLast() {

    }

    fun downloadEpisode() {

    }

    fun cancelDownloadEpisode() {

    }

    fun removeDownloadEpisode() {

    }

    fun saveEpisode() {

    }

    fun removeSavedEpisode() {

    }
}