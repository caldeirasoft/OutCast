package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.enum.PodcastEpisodesFilterType
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class PodcastDetailViewModel(
    private val fetchPodcastUseCase: FetchPodcastUseCase,
    private val fetchEpisodesFromPodcastUseCase: FetchEpisodesFromPodcastUseCase
) : ViewModel() {
    //private val filterData: StateFlow<PodcastEpisodesFilterType?> = MutableStateFlow(null)
    private val podcastData = MutableSharedFlow<Podcast>()
    private val podcastEpisodesData = MutableSharedFlow<List<EpisodeSummary>>()

    //val filterDataState
    //    get() = filterData

    val podcastDataState
        get() = podcastData

    val podcastEpisodesDataState
        get() = podcastEpisodesData

    fun fetchPodcast(podcastId: Long) {
        viewModelScope.launch {
            fetchPodcastUseCase(param = podcastId)
                .onEach { podcastData.emit(it) }
        }
    }

    fun fetchPodcastEpisodes(podcastId: Long, filterType: PodcastEpisodesFilterType) {
        viewModelScope.launch {
            fetchEpisodesFromPodcastUseCase
                .invoke(FetchEpisodesFromPodcastUseCase.Params(podcastId = podcastId, filter = filterType))
                .onEach { podcastEpisodesData.emit(it) }
        }
    }
}