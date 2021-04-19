package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.util.stopwatch
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityScoped
class FetchStoreTopChartsIdsUseCase @Inject constructor(
    private val storeRepository: StoreRepository,
) {
    fun execute(
        storeGenre: Int?,
        storeItemType: StoreItemType,
        storeFront: String,
    ): Flow<List<Long>> =
        flow {
            emit(stopwatch("FetchStoreTopChartsIdsUseCase - getTopChartsIdsAsync") {
                storeRepository.getTopChartsIdsAsync(storeGenre, storeFront, storeItemType, 200)
            })
        }
}