package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.db.PodcastSummary
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastsSubscribedUseCase
import kotlinx.coroutines.flow.Flow

class PodcastsViewModel(
    val fetchPodcastsSubscribedUseCase: FetchPodcastsSubscribedUseCase
) : ViewModel()
{
    val podcastEpisodes: Flow<List<PodcastSummary>> =
        fetchPodcastsSubscribedUseCase.invoke()
}