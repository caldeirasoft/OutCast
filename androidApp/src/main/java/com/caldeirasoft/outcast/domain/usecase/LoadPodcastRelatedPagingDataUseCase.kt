package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.PodcastRelatedPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.models.PodcastPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoadPodcastRelatedPagingDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun execute(
        scope: CoroutineScope,
        storePodcastPage: PodcastPage,
    ): Flow<PagingData<StoreCollection>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        ) {
            PodcastRelatedPagingSource(
                scope = scope,
                otherPodcasts = storePodcastPage,
                getStoreItems = storeRepository::getListStoreItemDataAsync
            )
        }.flow
}