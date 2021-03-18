package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreEpisodeDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.util.Resource
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
    // get episode data
    @OptIn(FlowPreview::class)
    suspend fun getEpisodeInfo() {
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
        withState { state ->
            fetchStoreEpisodeDataUseCase
                .execute(episode = state.episode, storeFront = storeFront)
                .setOnEach {
                    when (it) {
                        is Resource.Loading ->
                            copy(isLoading = true)
                        is Resource.Success -> {
                            copy(isLoading = false,
                                episode = it.data
                            )
                        }
                        is Resource.Error ->
                            copy(isLoading = false)
                    }
                }
        }
    }
}