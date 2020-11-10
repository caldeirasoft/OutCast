package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.enum.PodcastEpisodesFilterType
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.models.StoreDataGrouping
import com.caldeirasoft.outcast.domain.usecase.FetchEpisodesFromPodcastUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchInboxUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class InboxViewModel(
    val fetchInboxUseCase: FetchInboxUseCase
) : ViewModel()
{
    val filterGenre: SharedFlow<Int?> = MutableSharedFlow()

    private val episodesData
            = MutableSharedFlow<List<EpisodeSummary>>()

    val episodesDataState
            = episodesData

    fun fetchEpisodes(genre: Int?) {
        viewModelScope.launch {
            fetchInboxUseCase
                .invoke(FetchInboxUseCase.Params(genreId = genre))
                .onEach { episodesData.emit(it) }
        }
    }

    val textData = "Inbox Text"
}