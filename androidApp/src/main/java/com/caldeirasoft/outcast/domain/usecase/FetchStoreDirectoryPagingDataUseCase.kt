package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FetchStoreDirectoryPagingDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun executeAsync(
        scope: CoroutineScope,
        storeFront: String, newVersionAvailable: () -> Unit, dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        storeRepository.getDirectoryPagingData(scope, storeFront, newVersionAvailable, dataLoadedCallback)

}