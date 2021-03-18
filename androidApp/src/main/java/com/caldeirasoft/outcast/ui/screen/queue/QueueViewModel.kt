package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.usecase.FetchQueueUseCase
import kotlinx.coroutines.flow.Flow

class QueueViewModel(
    val fetchQueueUseCase: FetchQueueUseCase
) : ViewModel()
{
    val podcastEpisodes: Flow<List<Episode>> =
        fetchQueueUseCase
            .invoke()
}