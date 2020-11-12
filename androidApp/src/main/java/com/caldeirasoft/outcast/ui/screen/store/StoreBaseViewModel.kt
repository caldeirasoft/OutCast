package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow

abstract class StoreBaseViewModel(
    private val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
) : ViewModel() {
    val storeFront: String = "143442-3,29"

    fun fetchStoreItems(
        lookupIds: List<Long>,
        storeDataWithLookup: StoreDataWithLookup
    ): Flow<Resource<List<StoreItem>>> =
        fetchStoreItemsUseCase.invoke(
            FetchStoreItemsUseCase.Params(
                lookupIds = lookupIds,
                storeFront = storeFront,
                storeDataWithLookup = storeDataWithLookup
            )
        )
}