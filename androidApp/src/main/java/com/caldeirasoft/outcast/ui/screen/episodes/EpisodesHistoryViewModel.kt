package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.FetchEpisodesHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class EpisodesHistoryViewModel(
    val fetchEpisodesHistoryUseCase: FetchEpisodesHistoryUseCase
) : ViewModel()
{
    val filter: StateFlow<Podcast?> = MutableStateFlow(null)

    val podcastEpisodes: Flow<List<EpisodeSummary>> =
        filter.flatMapMerge {
            fetchEpisodesHistoryUseCase
                .invoke(FetchEpisodesHistoryUseCase.Params(it?.podcastId))
        }
}