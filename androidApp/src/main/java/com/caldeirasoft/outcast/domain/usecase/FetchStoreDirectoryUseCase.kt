package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class FetchStoreDirectoryUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : NetworkUseCase<FetchStoreDirectoryUseCase.Params, StoreGroupingData>(
    fetchNetworkCall = { storeRepository.getDirectoryDataAsync(it.storeFront) }
) {
    data class Params(val storeFront: String)
}