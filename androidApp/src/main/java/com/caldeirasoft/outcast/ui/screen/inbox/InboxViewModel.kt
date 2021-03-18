package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.usecase.FetchInboxUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class InboxViewModel(val fetchInboxUseCase: FetchInboxUseCase) : ViewModel(), LifecycleObserver
{
    val filterGenre: SharedFlow<Int?> = MutableSharedFlow()

    private val episodesData = MutableSharedFlow<List<Episode>>()

    val episodesDataState
            = episodesData

    fun fetchEpisodes(genre: Int?) {
        viewModelScope.launch {
            fetchInboxUseCase
                .invoke()
                .onEach { episodesData.emit(it) }
        }
    }

    val textData = "Inbox Text"
}