package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow

abstract class ItunesBaseViewModel(
    private val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ViewModel() {
    val storeFront: String = ""

    fun fetchStoreItems(
        lookupIds: List<Long>,
        storeDataWithLookup: StoreDataWithLookup
    ): Flow<Resource<List<StoreItem>>> =
        fetchItunesListStoreItemsUseCase.invoke(
            FetchItunesListStoreItemsUseCase.Params(
                lookupIds = lookupIds,
                storeFront = storeFront,
                storeDataWithLookup = storeDataWithLookup
            )
        )
}