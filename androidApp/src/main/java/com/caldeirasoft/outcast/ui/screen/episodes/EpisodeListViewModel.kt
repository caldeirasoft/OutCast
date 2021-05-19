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

abstract class EpisodeListViewModel<State: Any, Event: EpisodesEvent>(
    initialState: State,
) : BaseViewModelEvents<State, EpisodesEvent>(initialState)
{
    @OptIn(FlowPreview::class)
    abstract val episodes: Flow<PagingData<EpisodeUiModel>>

    fun openPodcastDetails(episode: Episode) {

    }

    fun playEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayEpisodeEvent)
        }
    }

    fun playNext(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayNextEpisodeEvent)
        }
    }

    fun playLast(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.PlayLastEpisodeEvent)
        }
    }

    fun downloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.DownloadEpisodeEvent)
        }
    }

    fun cancelDownloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.CancelDownloadEpisodeEvent)
        }
    }

    fun removeDownloadEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.RemoveDownloadEpisodeEvent)
        }
    }

    fun saveEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.SaveEpisodeEvent)
        }
    }

    fun removeSavedEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.RemoveFromSavedEpisodesEvent)
        }
    }

    fun shareEpisode(episode: Episode) {
        viewModelScope.launch {
            emitEvent(EpisodesEvent.ShareEpisodeEvent)
        }
    }

}