package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.data.util.PodcastRelatedPagingSource
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StorePodcastViewModel(
    initialState: StorePodcastViewState
) : MavericksViewModel<StorePodcastViewState>(initialState), KoinComponent {
    val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    init {
        viewModelScope.launch {
            getPodcastPage()
        }
    }

    // get paged list
    @OptIn(FlowPreview::class)
    private suspend fun getPodcastPage() {
        withState { state ->
            suspend {
                fetchStorePodcastDataUseCase.execute(
                    state.storePodcast.url,
                    state.storePodcast.storeFront)
            }.execute {
                copy(storePodcastPage = it)
            }
        }

        onAsync(StorePodcastViewState::storePodcastPage,
            onSuccess = { page -> getStoreDataPagedList(page) }
        )
    }

    private fun getStoreDataPagedList(storePodcastPage: PodcastPage) {
        Pager(
            PagingConfig(
                pageSize = 20,
                maxSize = 100,
            )
        ) {
            PodcastRelatedPagingSource(
                scope = viewModelScope,
                otherPodcasts = storePodcastPage,
                getStoreItemsUseCase = getStoreItemsUseCase
            )
        }.flow
            .setOnEach { copy(otherPodcasts = it) }
    }
}