package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastEpisodesPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class PodcastViewModel(
    initialState: PodcastViewState
) : MavericksViewModel<PodcastViewState>(initialState), KoinComponent {
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    val loadPodcastUseCase: LoadPodcastUseCase by inject()
    val loadPodcastEpisodesPagingDataUseCase: LoadPodcastEpisodesPagingDataUseCase by inject()

    val podcastFromDb: Flow<Podcast> =
        loadPodcastUseCase.execute(initialState.podcast.podcastId)


    init {
        viewModelScope.launch {
            loadPodcast()
        }
    }

    private suspend fun loadPodcast() {
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
        withState { state ->
            loadPodcastUseCase
                .execute(state.podcast.podcastId)
                .distinctUntilChanged()
                .onEach { /*getStoreDataPagedList()*/ }
                .setOnEach { copy(podcast = it) }

            loadPodcastEpisodesPagingDataUseCase
                .execute(state.podcast, storeFront)
                .cachedIn(viewModelScope)
                .setOnEach { copy(episodes = it) }
        }
    }
}