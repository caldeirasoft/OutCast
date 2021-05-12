package com.caldeirasoft.outcast.ui.screen.episodes.latest

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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LatestEpisodesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLatestEpisodesPagingDataUseCase: LoadLatestEpisodesPagingDataUseCase,
    private val loadLatestEpisodeCategoriesUseCase: LoadLatestEpisodeCategoriesUseCase,
) : EpisodesViewModel(
    initialState = EpisodesState(),
) {
    private var pagingData: Flow<PagingData<Episode>>? = null
    private val categoryFlow = selectSubscribe(EpisodesState::category)
    private val category: Category? = null

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodesUiModel>> =
        loadLatestEpisodesPagingDataUseCase.getLatestEpisodes()
            .onEach { Timber.d("LoadLatestEpisodesPagingDataUseCase : $it episodes") }
            .map { pagingData ->
                pagingData.filter { episode ->
                    episode.category == state.value.category
                }
            }
            .map { pagingData ->
                pagingData.map {
                    EpisodesUiModel.EpisodeItem(it)
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

    override suspend fun performAction(action: EpisodesActions) = when(action) {
        is EpisodesActions.FilterByCategory ->
            filterByCategory(action.category)
        else -> super.performAction(action)
    }

    private suspend fun filterByCategory(category: Category?) {
        setState {
            copy(category = category)
        }
        emitEvent(EpisodesEvent.RefreshList)
    }

    private fun Flow<PagingData<EpisodesUiModel.EpisodeItem>>.insertDateSeparators(): Flow<PagingData<EpisodesUiModel>> =
        map {
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
                if (before.episode.releaseDateTime.isDateTheSame(after.episode.releaseDateTime)) {
                    EpisodesUiModel.SeparatorItem(releaseDate)
                }
                else {
                    // no separator
                    null
                }
            }
        }

}