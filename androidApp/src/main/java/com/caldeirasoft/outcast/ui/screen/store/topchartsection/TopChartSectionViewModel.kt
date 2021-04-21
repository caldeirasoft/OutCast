package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
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
    followUseCase: FollowUseCase,
    loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
    val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    val podcastDao: PodcastDao
) : FollowViewModel<TopChartSectionState>(
    initialState,
    followUseCase,
    loadFollowedPodcastsUseCase,
    podcastDao
) {

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
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


