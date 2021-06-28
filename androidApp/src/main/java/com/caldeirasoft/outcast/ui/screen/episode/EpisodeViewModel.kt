package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.EpisodeRouteArgs
import com.caldeirasoft.outcast.ui.util.urlDecode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val podcastsRepository: PodcastsRepository,
    private val downloadRepository: DownloadRepository,
    private val episodesRepository: EpisodesRepository,
) : BaseViewModel<EpisodeViewModel.State, EpisodeViewModel.Event, EpisodeViewModel.Action>(
    // The string "episode" is the name of the argument in the route
    initialState = EpisodeRouteArgs.fromSavedStatedHandle(savedStateHandle).let {
        State(
            feedUrl = it.feedUrl,
            guid = it.guid.urlDecode()
        )
    }
) {

    private var isInitialized: Boolean = false
    private var isEpisodeSet: Boolean = false

    override fun activate() {
        val initialState = state.value
        episodesRepository
            .getEpisodeWithGuid(
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

    override suspend fun performAction(action: Action) = when (action) {
        is Action.SetEpisode -> setEpisode(action.episode)
        is Action.OpenPodcastDetail -> openPodcastDetails()
        is Action.PlayEpisode -> playEpisode()
        is Action.PauseEpisode -> playEpisode()
        is Action.PlayNextEpisode -> playNext()
        is Action.PlayLastEpisode -> playLast()
        is Action.ToggleSaveEpisode -> toggleSaveEpisode()
        is Action.ShareEpisode -> shareEpisode()
        is Action.Exit -> emitEvent(Event.Exit)
    }

    private fun setEpisode(storeEpisode: StoreEpisode) {
        if (!isEpisodeSet) {
            viewModelScope.launch {
                isEpisodeSet = true
                setState {
                    copy(
                        isLoading = true,
                        episode = storeEpisode.episode,
                        podcast = storeEpisode.storePodcast.podcast
                    )
                }
                podcastsRepository
                    .updatePodcastItunesMetadata(storeEpisode.storePodcast.podcast)
            }
        }
    }

    private fun openPodcastDetails() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(Event.OpenPodcastDetail(podcast))
            }
        }
    }

    private fun playEpisode() {
    }

    private fun playNext() {
    }

    private fun playLast() {
    }

    private fun toggleSaveEpisode() {
        viewModelScope.withState {
            it.episode?.let { episode ->
                if (episode.isSaved.not()) {
                    episodesRepository.saveEpisodeToLibrary(episode)
                    downloadRepository.startDownload(episode)
                }
                else {
                    episodesRepository.deleteFromLibrary(episode)
                    downloadRepository.removeDownload(episode)
                }
            }
        }
    }

    private fun shareEpisode() {
        viewModelScope.launch {
            emitEvent(Event.ShareEpisode)
        }
    }

    data class State(
        val feedUrl: String,
        val guid: String,
        val episode: Episode? = null,
        val podcast: Podcast? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )

    sealed class Event {
        data class OpenPodcastDetail(val podcast: Podcast) : Event()
        object ShareEpisode : Event()
        object Exit : Event()
    }

    sealed class Action {
        object OpenPodcastDetail : Action()
        data class SetEpisode(val episode: StoreEpisode) : Action()
        object PlayEpisode : Action()
        object PauseEpisode : Action()
        object PlayNextEpisode : Action()
        object PlayLastEpisode : Action()
        object ToggleSaveEpisode : Action()
        object ShareEpisode : Action()
        object Exit : Action()
    }
}