package com.caldeirasoft.outcast.ui.screen.episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.episodes.base.*
import com.caldeirasoft.outcast.ui.util.isDateTheSame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LatestEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLatestEpisodesPagingDataUseCase: LoadLatestEpisodesPagingDataUseCase,
    private val loadLatestEpisodeCategoriesUseCase: LoadLatestEpisodeCategoriesUseCase,
) : EpisodeListViewModel<EpisodesState, EpisodesEvent>(
    initialState = EpisodesState(),
) {
    private var pagingData: Flow<PagingData<Episode>>? = null

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        loadLatestEpisodesPagingDataUseCase.getLatestEpisodes()
            .map { pagingData ->
                state.value.category
                    ?.let {
                        pagingData.filter { episode ->
                            episode.category == state.value.category
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
        pagingData = loadLatestEpisodesPagingDataUseCase.getLatestEpisodes()
            .onEach { Timber.d("LoadLatestEpisodesPagingDataUseCase : $it episodes") }


        loadLatestEpisodeCategoriesUseCase.getLatestEpisodesCategories()
            .map { it.filterNotNull() }
            .setOnEach { categories ->
                copy(
                    categories = categories,
                    category = this.category.takeIf { categories.contains(it) }
                )
            }
    }

    fun filterByCategory(category: Category?) {
        viewModelScope.setState {
            copy(category = category)
        }
        emitEvent(EpisodesEvent.RefreshList)
    }

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