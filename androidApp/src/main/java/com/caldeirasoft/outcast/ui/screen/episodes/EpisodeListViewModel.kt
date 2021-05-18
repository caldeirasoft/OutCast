package com.caldeirasoft.outcast.ui.screen.episodes

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.util.castAs
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

abstract class EpisodeListViewModel<State: Any, Event: Any>(
    initialState: State,
) : BaseViewModelEvents<State, Event>(initialState)
{
    @OptIn(FlowPreview::class)
    abstract val episodes: Flow<PagingData<EpisodeUiModel>>

    fun openPodcastDetails(episode: Episode) {

    }

    fun playEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayEpisodeEvent as Event)
        }
    }

    fun playNext(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayNextEpisodeEvent as Event)
        }
    }

    fun playLast(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayLastEpisodeEvent as Event)
        }
    }

    fun downloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.DownloadEpisodeEvent as Event)
        }
    }

    fun cancelDownloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.CancelDownloadEpisodeEvent as Event)
        }
    }

    fun removeDownloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.RemoveDownloadEpisodeEvent as Event)
        }
    }

    fun saveEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.SaveEpisodeEvent as Event)
        }
    }

    fun removeSavedEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.RemoveFromSavedEpisodesEvent as Event)
        }
    }

    fun shareEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.ShareEpisodeEvent as Event)
        }
    }

}