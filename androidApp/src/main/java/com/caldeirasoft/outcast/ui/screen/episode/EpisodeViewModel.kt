package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.usecase.FetchStoreEpisodeDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class EpisodeViewModel(
    initialState: EpisodeViewState
) : MavericksViewModel<EpisodeViewState>(initialState), KoinComponent
{
    private val fetchStoreEpisodeDataUseCase: FetchStoreEpisodeDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // get paged list
    @OptIn(FlowPreview::class)
    suspend fun getEpisodeInfo(episode: Episode) {
        setState { copy(episode = episode) }
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
        fetchStoreEpisodeDataUseCase
            .execute(episode = episode, storeFront = storeFront)
            .setOnEach {
                when (it) {
                    is Resource.Loading ->
                        copy(isLoading = true)
                    is Resource.Success -> {
                        copy(isLoading = false,
                            episode = it.data.episode,
                            isFavorite = it.data.isFavorite == 1L,
                            isPlayed = it.data.isPlayed == 1L,
                            playbackPosition = it.data.playbackPosition
                        )
                    }
                    is Resource.Error ->
                        copy(isLoading = false)
                }
            }
    }
}