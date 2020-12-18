package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*

class FetchStoreGroupingUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
)
{
    fun executeAsync(storeGenre: Int?, storeFront: String): Flow<Resource<StoreGroupingData>> {
        val groupingDataFlow = fetchGroupingData(storeGenre, storeFront)
        val topPodcastsFlow = fetchTopPodcasts(storeGenre, storeFront)
        val topEpisodesFlow = fetchTopEpisodes(storeGenre, storeFront)
        return combine(
            groupingDataFlow,
            topPodcastsFlow,
            topEpisodesFlow
        ) { groupingData, topPodcasts, topEpisodes ->
            if (!groupingData.storeList.any { it is StoreCollectionChartsIds })
            {
                val index = groupingData
                    .storeList
                    .withIndex()
                    .filter { it.value is StoreCollectionPodcastIds }
                    .take(3)
                    .last()
                    .index
                Log_D("FetchStoreGroupingUseCase : index2", index.toString())
                val topCharts = StoreCollectionChartsIds(
                    topPodcastsIds = topPodcasts,
                    topEpisodesIds = topEpisodes,
                    genreId = storeGenre,
                    storeFront = storeFront,
                )
                groupingData.storeList.add(index + 1, topCharts)
            }
            groupingData
        }.map {
            Resource.Success(it) as Resource<StoreGroupingData>
        }.onStart { emit(Resource.Loading()) }
            .catch { emit(Resource.Error(it)) }
    }

    private fun fetchGroupingData(storeGenre: Int?, storeFront: String): Flow<StoreGroupingData> = flow {
        emit(
            stopwatch("FetchStoreGroupingUseCase - fetchGroupingData") {
                storeRepository.getGroupingDataAsync(storeGenre, storeFront)
            }
        )
    }

    private fun fetchTopPodcasts(storeGenre: Int?, storeFront: String): Flow<List<Long>> = flow {
        storeGenre
            ?.let {
                emit(
                    stopwatch("FetchStoreGroupingUseCase - fetchTopPodcasts") {
                        storeRepository.getTopChartsPodcastsIdsAsync(storeGenre, storeFront, 5)
                    }
                )
            }
            ?: emit(emptyList<Long>())
    }

    private fun fetchTopEpisodes(storeGenre: Int?, storeFront: String): Flow<List<Long>> = flow {
        storeGenre
            ?.let {
                emit(
                    stopwatch("FetchStoreGroupingUseCase - fetchTopEpisodes") {
                        storeRepository.getTopChartsEpisodesIdsAsync(storeGenre, storeFront, 5)
                    }
                )
            }
            ?: emit(emptyList<Long>())
    }
}