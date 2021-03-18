package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.usecase.FetchEpisodesHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge

@FlowPreview
@ExperimentalCoroutinesApi
class EpisodesHistoryViewModel(
    val fetchEpisodesHistoryUseCase: FetchEpisodesHistoryUseCase
) : ViewModel()
{
    val filter: StateFlow<Podcast?> = MutableStateFlow(null)

    val podcastEpisodes: Flow<List<Episode>> =
        filter.flatMapMerge {
            fetchEpisodesHistoryUseCase.invoke()
        }
}