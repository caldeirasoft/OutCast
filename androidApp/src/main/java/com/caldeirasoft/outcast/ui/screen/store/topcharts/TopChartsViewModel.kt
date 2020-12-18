package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.repository.StoreRepositoryImpl
import com.caldeirasoft.outcast.data.util.StoreChartsPagingSource
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class TopChartsViewModel(val topCharts: StoreTopCharts)
    : ViewModel(), KoinComponent {
    private val fetchStoreTopChartsPodcastsIdsUseCase: FetchStoreTopChartsPodcastsIdsUseCase by inject()
    private val fetchStoreTopChartsEpisodesIdsUseCase: FetchStoreTopChartsEpisodesIdsUseCase by inject()

    val topPodcastsCharts: Flow<PagingData<StoreItem>> =
        getTopChartPagedList(StoreItemType.PODCAST)
            .cachedIn(viewModelScope)

    val topEpisodesCharts: Flow<PagingData<StoreItem>> =
        getTopChartPagedList(StoreItemType.EPISODE)
            .cachedIn(viewModelScope)

    private fun getTopChartPagedList(type: StoreItemType): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            )
        ) {
            StoreChartsPagingSource(
                storeFront = topCharts.storeFront,
                scope = viewModelScope) {
                when (type) {
                    StoreItemType.PODCAST ->
                        fetchStoreTopChartsPodcastsIdsUseCase.execute(storeGenre = topCharts.genreId, storeFront = topCharts.storeFront)
                    StoreItemType.EPISODE ->
                        fetchStoreTopChartsEpisodesIdsUseCase.execute(storeGenre = topCharts.genreId, storeFront = topCharts.storeFront)
                }
            }
        }.flow
}


