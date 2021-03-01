package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingPagingDataUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

@FlowPreview
@ExperimentalCoroutinesApi
class StoreGenreViewModel(
    val genreId: Int,
    val fetchStoreGroupingPagingDataUseCase: FetchStoreGroupingPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {

    // storefront
    protected val storeFront = fetchStoreFrontUseCase.getStoreFront()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapConcat {
                fetchStoreGroupingPagingDataUseCase.executeAsync(
                    genre = genreId,
                    storeFront = it,
                    dataLoadedCallback = null
                )
            }

}