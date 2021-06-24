package com.caldeirasoft.outcast.ui.screen.saved_episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.domain.usecase.LoadSavedEpisodesPagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadSavedEpisodesPagingDataUseCase: LoadSavedEpisodesPagingDataUseCase,
    episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel<EpisodeListViewModel.State, EpisodeListViewModel.Event, EpisodeListViewModel.Action>(
    initialState = State(),
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {
    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        loadSavedEpisodesPagingDataUseCase
            .execute()
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)

    override fun activate() {
        downloadsFlow
            .setOnEach { downloads ->
                copy(downloads = downloads)
            }
    }
}