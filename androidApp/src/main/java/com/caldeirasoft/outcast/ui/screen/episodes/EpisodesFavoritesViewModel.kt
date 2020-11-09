package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.FetchEpisodesFavoritesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class EpisodesFavoritesViewModel(
    val fetchEpisodesFavoritesUseCase: FetchEpisodesFavoritesUseCase
) : ViewModel()
{
    val filter: StateFlow<Podcast?> = MutableStateFlow(null)

    val podcastEpisodes: Flow<List<EpisodeSummary>> =
        filter.flatMapMerge {
            fetchEpisodesFavoritesUseCase
                .invoke(FetchEpisodesFavoritesUseCase.Params(it?.podcastId))
        }
}