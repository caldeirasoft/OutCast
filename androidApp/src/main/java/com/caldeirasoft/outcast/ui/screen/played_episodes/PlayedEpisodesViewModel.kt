package com.caldeirasoft.outcast.ui.screen.played_episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.util.isDateTheSame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayedEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLatestEpisodesPagingDataUseCase: LoadLatestEpisodesPagingDataUseCase,
    episodesRepository: EpisodesRepository,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel<EpisodeListViewModel.State, EpisodeListViewModel.Event, EpisodeListViewModel.Action>(
    initialState = State(),
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
) {

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        loadLatestEpisodesPagingDataUseCase.getLatestEpisodes()
            .onEach { Timber.d("LoadLatestEpisodesPagingDataUseCase : $it episodes") }
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) }}
            .insertDateSeparators()
            .cachedIn(viewModelScope)

    override fun activate() {
        downloadsFlow
            .setOnEach { downloads ->
                copy(downloads = downloads)
            }
    }

    private fun Flow<PagingData<EpisodeUiModel.EpisodeItem>>.insertDateSeparators(): Flow<PagingData<EpisodeUiModel>> =
        map {
            it.insertSeparators { before, after ->
                if (after == null) {
                    // end of the list
                    return@insertSeparators null
                }

                val releaseDate = after.episode.playedAtInstant ?: Instant.DISTANT_PAST
                if (before == null) {
                    // we're at the beginning of the lis
                    return@insertSeparators EpisodeUiModel.SeparatorItem(releaseDate)
                }
                // check between 2 items
                if (before.episode.playedAtInstant?.isDateTheSame(releaseDate) == true) {
                    EpisodeUiModel.SeparatorItem(releaseDate)
                }
                else {
                    // no separator
                    null
                }
            }
        }
}