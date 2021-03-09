package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreEpisodeDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            getEpisodeInfo()
        }
    }

    // get paged list
    @OptIn(FlowPreview::class)
    private suspend fun getEpisodeInfo() {
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
        withState { state ->
            val episode = state.storeEpisode.invoke()
            if (episode != null && !episode.isComplete) {
                suspend {
                    fetchStoreEpisodeDataUseCase.execute(
                        storeEpisode = episode,
                        storeFront = storeFront
                    )
                }.execute(retainValue = EpisodeViewState::storeEpisode) {
                    copy(storeEpisode = it)
                }
            }
        }
    }
}