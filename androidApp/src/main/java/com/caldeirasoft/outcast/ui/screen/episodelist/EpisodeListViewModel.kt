package com.caldeirasoft.outcast.ui.screen.episodelist

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.PodcastWithCount
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import kotlinx.coroutines.flow.*

abstract class EpisodeListViewModel(
    episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : BaseEpisodeListViewModel<BaseEpisodeListViewModel.State, BaseEpisodeListViewModel.Event, BaseEpisodeListViewModel.Action>(
    initialState = State(),
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {

    override fun activate() {
        super.activate()

        getPodcastCount()
            .distinctUntilChanged()
            .setOnEach { podcastsWithCount ->
                copy(podcastsWithCount = podcastsWithCount)
            }

        downloadsFlow
            .setOnEach { downloads ->
                copy(downloads = downloads)
            }
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.FilterByPodcast ->
            filterByPodcast(action.feedUrl)
        else -> super.performAction(action)
    }

    protected abstract fun getPodcastCount(): Flow<List<PodcastWithCount>>

    fun filterByPodcast(feedUrl: String?) {
        viewModelScope.setState {
            copy(podcastFilter = feedUrl)
        }
        emitEvent(Event.RefreshList)
    }

    override fun getEpisodes(): Flow<PagingData<EpisodeUiModel>> =
        getEpisodesPagingData()
            .map { pagingData ->
                state.value.podcastFilter
                    ?.let {
                        pagingData.filter { episode ->
                            episode.feedUrl == it
                        }
                    }
                    ?: pagingData
            }
            .map { pagingData ->
                pagingData.map {
                    EpisodeUiModel.EpisodeItem(it)
                }
            }
            .insertDateSeparators()


    protected open fun Flow<PagingData<EpisodeUiModel.EpisodeItem>>.insertDateSeparators(): Flow<PagingData<EpisodeUiModel>> =
        this.map { pagingData ->
            pagingData.map {
                it as EpisodeUiModel
            }
        }
}