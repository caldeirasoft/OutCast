package com.caldeirasoft.outcast.ui.screen.store.topchartitem

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
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TopChartsEpisodeViewModel(
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    fetchStoreTopChartsUseCase: FetchStoreTopChartsUseCase,
    getStoreItemsUseCase: GetStoreItemsUseCase,
) : TopChartsItemViewModel(
    fetchStoreTopChartsUseCase = fetchStoreTopChartsUseCase,
    fetchStoreFrontUseCase = fetchStoreFrontUseCase,
    getStoreItemsUseCase = getStoreItemsUseCase,
) {
    override fun getStoreTopChartsIds(storeData: StoreTopCharts): List<Long> =
        storeData.storeEpisodesIds
}