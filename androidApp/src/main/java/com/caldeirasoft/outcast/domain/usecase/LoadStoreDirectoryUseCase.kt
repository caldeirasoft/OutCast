package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import kotlinx.coroutines.CoroutineScope

class LoadStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
) {
    suspend fun executeAsync(
        scope: CoroutineScope,
        storeFront: String,
        newVersionAvailable: (() -> Unit)?
    ): StoreGroupingPage =
        storeRepository.loadStoreDirectoryData(scope, storeFront, newVersionAvailable)
}