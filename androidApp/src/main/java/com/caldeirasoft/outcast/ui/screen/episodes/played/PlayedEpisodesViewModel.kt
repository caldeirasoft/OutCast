package com.caldeirasoft.outcast.ui.screen.episodes.played

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.episodes.base.EpisodesState
import com.caldeirasoft.outcast.ui.screen.episodes.base.EpisodesUiModel
import com.caldeirasoft.outcast.ui.screen.episodes.base.EpisodesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLatestEpisodesPagingDataUseCase: LoadLatestEpisodesPagingDataUseCase,
) : EpisodesViewModel(
    initialState = EpisodesState(),
) {

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodesUiModel>> =
        loadLatestEpisodesPagingDataUseCase.getLatestEpisodes()
            .onEach { Timber.d("LoadLatestEpisodesPagingDataUseCase : $it episodes") }
            .map { pagingData -> pagingData.map { EpisodesUiModel.EpisodeItem(it) }}
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // end of the list
                        return@insertSeparators null
                    }

                    val releaseDate = after.episode.releaseDateTime
                    if (before == null) {
                        // we're at the beginning of the lis
                        return@insertSeparators EpisodesUiModel.SeparatorItem(releaseDate)
                    }
                    // check between 2 items
                    if (before.episode.releaseDateTime.toLocalDateTime(TimeZone.UTC).date !=
                        after.episode.releaseDateTime.toLocalDateTime(TimeZone.UTC).date) {
                        EpisodesUiModel.SeparatorItem(releaseDate)
                    }
                    else {
                        // no separator
                        null
                    }
                }
            }
            .cachedIn(viewModelScope)
}