package com.caldeirasoft.outcast.ui.screen.episodes

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

abstract class EpisodeListViewModel<State: Any, Event: Any>(
    initialState: State,
) : BaseViewModelEvents<State, Event>(initialState)
{
    @OptIn(FlowPreview::class)
    abstract val episodes: Flow<PagingData<EpisodeUiModel>>

    fun openPodcastDetails(episode: Episode) {

    }

    fun playEpisode(episode: Episode) {

    }

    fun playNext(episode: Episode) {

    }

    fun playLast(episode: Episode) {

    }

    fun downloadEpisode(episode: Episode) {

    }

    fun cancelDownloadEpisode(episode: Episode) {

    }

    fun removeDownloadEpisode(episode: Episode) {

    }

    fun saveEpisode(episode: Episode) {

    }

    fun shareEpisode(episode: Episode) {

    }

    fun removeSavedEpisode(episode: Episode) {

    }
}