package com.caldeirasoft.outcast.ui.screen.episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.domain.usecase.LoadSavedEpisodesPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.RemoveSaveEpisodeUseCase
import com.caldeirasoft.outcast.domain.usecase.SaveEpisodeUseCase
import com.caldeirasoft.outcast.ui.screen.base.EpisodeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadSavedEpisodesPagingDataUseCase: LoadSavedEpisodesPagingDataUseCase,
    saveEpisodeUseCase: SaveEpisodeUseCase,
    removeSaveEpisodeUseCase: RemoveSaveEpisodeUseCase,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel<EpisodesState, EpisodesEvent>(
    initialState = EpisodesState(),
    saveEpisodeUseCase = saveEpisodeUseCase,
    removeSaveEpisodeUseCase = removeSaveEpisodeUseCase,
    downloadRepository = downloadRepository
) {
    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        loadSavedEpisodesPagingDataUseCase
            .execute()
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)
}