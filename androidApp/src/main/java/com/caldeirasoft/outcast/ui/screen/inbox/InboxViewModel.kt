package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.base.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodesEvent
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodesState
import com.caldeirasoft.outcast.ui.screen.episodes.base.*
import com.caldeirasoft.outcast.ui.util.isDateTheSame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLatestEpisodeCategoriesUseCase: LoadLatestEpisodeCategoriesUseCase,
    saveEpisodeUseCase: SaveEpisodeUseCase,
    removeSaveEpisodeUseCase: RemoveSaveEpisodeUseCase,
    private val episodesRepository: EpisodesRepository,
    private val downloadRepository: DownloadRepository,
) : EpisodeListViewModel<EpisodesState, EpisodesEvent>(
    initialState = EpisodesState(),
    saveEpisodeUseCase = saveEpisodeUseCase,
    removeSaveEpisodeUseCase = removeSaveEpisodeUseCase,
    downloadRepository = downloadRepository
) {
    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        getLatestEpisodes()
            .map { pagingData ->
                state.value.category
                    ?.let {
                        pagingData.filter { episode ->
                            episode.category == state.value.category?.ordinal
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
            .cachedIn(viewModelScope)

    init {
        episodesRepository
            .getInboxEpisodesCategories()
            .map { it.filterNotNull() }
            .setOnEach { categories ->
                copy(
                    categories = categories,
                    category = this.category.takeIf { categories.contains(it) }
                )
            }

        downloadsFlow
            .setOnEach { downloads ->
                copy(downloads = downloads)
            }
    }

    fun filterByCategory(category: Category?) {
        viewModelScope.setState {
            copy(category = category)
        }
        emitEvent(EpisodesEvent.RefreshList)
    }

    private fun getLatestEpisodes(): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 4000,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = episodesRepository.getInboxEpisodesDataSource().asPagingSourceFactory()
        ).flow


    private fun Flow<PagingData<EpisodeUiModel.EpisodeItem>>.insertDateSeparators(): Flow<PagingData<EpisodeUiModel>> =
        map {
            it.insertSeparators { before, after ->
                if (after == null) {
                    // end of the list
                    return@insertSeparators null
                }

                val releaseDate = after.episode.releaseDateTime
                if (before == null) {
                    // we're at the beginning of the lis
                    return@insertSeparators EpisodeUiModel.SeparatorItem(releaseDate)
                }
                // check between 2 items
                if (before.episode.releaseDateTime.isDateTheSame(after.episode.releaseDateTime)) {
                    EpisodeUiModel.SeparatorItem(releaseDate)
                }
                else {
                    // no separator
                    null
                }
            }
        }

}