package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.SubscribeUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
abstract class TopChartSectionViewModel(
    initialState: TopChartSectionState,
    val storeItemType: StoreItemType,
    followUseCase: SubscribeUseCase,
    loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
    val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : FollowViewModel<TopChartSectionState>(
    initialState,
    followUseCase,
    loadFollowedPodcastsUseCase
) {

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val topCharts: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .map { storeFront ->
                loadStoreTopChartsPagingDataUseCase.execute(
                    scope = viewModelScope,
                    genreId = null, // genre
                    storeFront = storeFront,
                    storeItemType = storeItemType, // item type
                )
            }
            .flattenMerge()
            .cachedIn(viewModelScope)
}


