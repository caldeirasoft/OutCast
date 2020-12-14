package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.usecase.FetchInboxUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class InboxViewModel(val fetchInboxUseCase: FetchInboxUseCase) : ViewModel(), LifecycleObserver
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