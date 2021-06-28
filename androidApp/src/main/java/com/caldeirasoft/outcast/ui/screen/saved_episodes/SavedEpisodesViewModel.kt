package com.caldeirasoft.outcast.ui.screen.saved_episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel<EpisodeListViewModel.State, EpisodeListViewModel.Event, EpisodeListViewModel.Action>(
    initialState = State(),
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {
    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        getSavedEpisodes()
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)

    override fun activate() {
        downloadsFlow
            .setOnEach { downloads ->
                copy(downloads = downloads)
            }
    }

    private fun getSavedEpisodes(): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = episodesRepository.getSavedEpisodesDataSource().asPagingSourceFactory()
        ).flow
}