package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionChartsIds
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionPodcastIds
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.*

class FetchStoreGroupingUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
)
{
    fun executeAsync(genreId: Int?, storeFront: String): Flow<Resource> {
        val groupingDataFlow = fetchGroupingData(genreId, storeFront)
        val topPodcastsFlow = fetchTopItems(genreId, storeFront, StoreItemType.PODCAST)
        val topEpisodesFlow = fetchTopItems(genreId, storeFront, StoreItemType.EPISODE)
        return combine(
            groupingDataFlow,
            topPodcastsFlow,
            topEpisodesFlow
        ) { groupingData, topPodcasts, topEpisodes ->
            if (genreId != null && !groupingData.storeList.any { it is StoreCollectionChartsIds })
            {
                // try to insert Top Charts in the 3rd position
                val index = groupingData
                    .storeList
                    .withIndex()
                    .filter { it.value is StoreCollectionPodcastIds }
                    .take(2)
                    .last()
                    .index
                Log_D("FetchStoreGroupingUseCase : index2", index.toString())
                val topCharts = StoreCollectionChartsIds(
                    topPodcastsIds = topPodcasts,
                    topEpisodesIds = topEpisodes,
                    genreId = genreId,
                    storeFront = storeFront,
                )
                groupingData.storeList.add(index + 1, topCharts)
            }
            else if (genreId == null && groupingData.genres != null) {
                groupingData.storeList.add(2, groupingData.genres)
            }
            groupingData
        }.map {
            Resource.Success(it) as Resource
        }.onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it)) }
    }

    private fun fetchGroupingData(genreId: Int?, storeFront: String): Flow<StoreGroupingData> = flow {
        emit(
            stopwatch("FetchStoreGroupingUseCase - fetchGroupingData") {
                storeRepository.getGroupingDataAsync(genreId, storeFront)
            }
        )
    }

    private fun fetchTopItems(genreId: Int?, storeFront: String, storeItemType: StoreItemType): Flow<List<Long>> = flow {
        emit(
            stopwatch("FetchStoreGroupingUseCase - fetchTopPodcasts") {
                storeRepository.getTopChartsIdsAsync(genreId, storeFront, storeItemType, 5)
            }
        )
    }
}