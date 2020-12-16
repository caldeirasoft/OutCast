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

@ExperimentalCoroutinesApi
class TopChartViewModel(storeChart: StoreChart, storePage: StorePage) : ViewModel() {
    val pagedList: Flow<PagingData<StoreItem>> =
        getTopChartPagedList(storeChart, storePage)
            .cachedIn(viewModelScope)

    private fun getTopChartPagedList(storeChart: StoreChart, storePage: StorePage): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            )
        ) {
            StoreChartsPagingSource(
                storeChart = storeChart,
                storePage = storePage,
                scope = viewModelScope)
        }.flow
}


