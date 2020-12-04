package com.caldeirasoft.outcast.ui.screen.store.topchartcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TopChartsCategoryViewModel(
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val fetchStoreTopChartsUseCase: FetchStoreTopChartsUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
    private val topChartsType: TopChartsType,
) : ViewModel() {

    private val storeFront =
        fetchStoreFrontUseCase.getStoreFront()

    val topCharts: Flow<PagingData<StoreItem>> = flowOf(
        storeFront.flatMapLatest {
            getTopChartsPagedList(storeFront = it)
        }
    ).flattenMerge()
        .cachedIn(viewModelScope)

    private fun getTopChartsPagedList(
        storeFront: String,
    ): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 3
            )
        ) {
            val pagingSource = object : StoreDataPagingSource(getStoreItemsUseCase) {
                override fun getStoreData(): Flow<StoreData> =
                    fetchStoreTopChartsUseCase.execute(storeFront)
                        .map {
                            StoreRoom(
                                id = it.id,
                                label = it.label,
                                storeFront = storeFront,
                                storeIds = when {
                                    topChartsType.podcast -> it.storePodcastsIds
                                    topChartsType.podcastEpisode -> it.storeEpisodesIds
                                    else -> emptyList()
                                }
                            )
                        }
            }
            pagingSource
        }.flow
}