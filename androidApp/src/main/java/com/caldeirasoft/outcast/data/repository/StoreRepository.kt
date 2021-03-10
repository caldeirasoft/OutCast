package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.util.PodcastRemoteMediator
import com.caldeirasoft.outcast.data.util.StoreChartsPagingSource
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.data.util.local.DiskCache
import com.caldeirasoft.outcast.data.util.local.MemoryCache
import com.caldeirasoft.outcast.data.util.local.Source
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.dto.GenreResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.*
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import timber.log.Timber
import kotlin.time.days

class StoreRepository (
    val itunesAPI: ItunesAPI,
    val searchAPI: ItunesSearchAPI,
    val context: Context,
    val json: Json,
    val database: Database,
) {

    companion object {
        const val DEFAULT_GENRE = 26
        const val CACHE_STORE_FILE_NAME = "storeCache.db"
    }

    // Setup datacache
    private val cache = MemoryCache(DiskCache(json, context.cacheDir))

    /**
     * getStoreDataAsync{
     */
    suspend fun getStoreDataAsync(
        url: String,
        storeFront: String
    ): StorePage {
        val storeResponse = itunesAPI.storeData(storeFront = storeFront, url = url)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)
        return getStoreData(storePageDto)
    }


    /**
     * getGroupingDataAsync
     */
    private suspend fun getGroupingDataAsync(genre: Int?, storeFront: String): StoreGroupingPage {
        Timber.d("DBG - getGroupingData")
        val storeResponse =
            itunesAPI.groupingData(storeFront = storeFront, genre = genre ?: DEFAULT_GENRE)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        Timber.d("DBG - getGroupingData body")
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)
        Timber.d("DBG - getStoreData")
        val storePage = getStoreData(storePageDto)
        if (storePage !is StoreGroupingPage)
            throw NullPointerException("DBG - Invalid cast to StoreGroupingPage")
        Timber.d("DBG - return StoreData")
        return storePage
    }

    /**
     * getStorePagingData
     */
    fun getStoreRoomPagingData(
        scope: CoroutineScope,
        storeRoom: StoreRoom,
        dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                StoreDataPagingSource(
                    scope = scope,
                    loadDataFromNetwork = {
                        if (storeRoom.url.isEmpty()) storeRoom.getPage()
                        else getStoreDataAsync(storeRoom.url, storeRoom.storeFront)
                    },
                    dataLoadedCallback = dataLoadedCallback,
                    getStoreItems = { ids, storeFront, storeData -> getListStoreItemDataAsync(ids, storeFront, storeData) }
                )
            }
        ).flow

    /**
     * getGroupingDataPagingSource
     */
    fun getGroupingPagingData(
        scope: CoroutineScope,
        genre: Int?,
        storeFront: String,
        dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                StoreDataPagingSource(
                    scope = scope,
                    loadDataFromNetwork = { getGroupingDataAsync(genre, storeFront) },
                    dataLoadedCallback = dataLoadedCallback,
                    getStoreItems = { ids, storeFront, storeData -> getListStoreItemDataAsync(ids, storeFront, storeData) }
                )
            }
        ).flow

    /**
     * loadDirectoryPagingData
     */
    fun loadDirectoryPagingData(
        scope: CoroutineScope,
        storeFront: String,
        newVersionAvailable: () -> Unit,
        dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                Timber.d("DBG - create pagingSourceFactory")
                StoreDataPagingSource(
                    scope = scope,
                    loadDataFromNetwork = {
                        loadStoreDirectoryData(scope,
                            storeFront,
                            newVersionAvailable)
                            .also {
                                dataLoadedCallback?.invoke(it)
                            }
                    },
                    dataLoadedCallback = null,
                    getStoreItems = { ids, storeFront, storeData -> getListStoreItemDataAsync(ids, storeFront, storeData) }
                )
            }
        ).flow

    /**
     * loadGroupingData
     */
    suspend fun loadStoreDirectoryData(
        scope: CoroutineScope,
        storeFront: String,
        newVersionAvailable: (() -> Unit)?
    ): StoreGroupingPage {
        Timber.d("DBG - load GroupingData from cache")
        val groupingPageCacheEntry = cache.getEntry("storeDirectory", useEntryEvenIfExpired = true, timeLimit = 1.days) {
            Timber.d("DBG - load GroupingData from network")
            getGroupingDataAsync(null, storeFront)
        }
        Timber.d("DBG - load GroupingData done")

        return newVersionAvailable?.let {
            val groupingPageCache = groupingPageCacheEntry.data
            if (groupingPageCacheEntry.source != Source.ORIGIN &&
                (groupingPageCacheEntry.isExpired || groupingPageCache.storeFront != storeFront)
            ) {
                Timber.d("DBG - cached GroupingData is too old")
                scope.launch {
                    Timber.d("DBG - get new GroupingData from network")
                    val newGroupingPage = getGroupingDataAsync(null, storeFront)
                    // if network version is newer/different than cached version -> notify
                    if ((newGroupingPage.storeFront != groupingPageCache.storeFront) ||
                        (newGroupingPage.timestamp != groupingPageCache.timestamp)
                    ) {
                        cache.set("storeDirectory", newGroupingPage)
                        newVersionAvailable.invoke()
                    } else {
                        Timber.d("DBG - new Grouping data is the same")
                        //groupingPageCache.fetchedAt = now
                        cache.set("storeDirectory", groupingPageCache)
                    }
                }
            }
            groupingPageCache
        }
            ?: groupingPageCacheEntry.data
    }

    /**
     * getStoreData
     */
    private fun getStoreData(storePageDto: StorePageDto): StorePage {
        val pageData = storePageDto.pageData
        // retrieve data
        return when (pageData?.componentName) {
            "grouping_page" -> {
                getGroupingDataAsync(storePageDto)
            }
            "room_page" -> {
                getRoomPodcastDataAsync(storePageDto)
            }
            "multi_room_page" -> {
                getMultiRoomDataAsync(storePageDto)
            }
            "artist_page" -> {
                when (pageData.metricsBase?.pageType) {
                    "Artist" ->
                        getArtistPodcastDataAsync(storePageDto)
                    "Provider" ->
                        getArtistProviderDataAsync(storePageDto)
                    else ->
                        throw Exception("Invalid artist")
                }
            }
            else -> throw Exception("Invalid store data")
        }
    }

    /**
     * getGroupingDataAsync
     */
    private fun getGroupingDataAsync(storePageDto: StorePageDto): StoreGroupingPage {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val lockupResult = storePageDto.storePlatformData?.lockup?.results ?: emptyMap()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val storeLookup =
            getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.fcStructure?.model?.children
                ?.first { element -> element.token == "allPodcasts" }?.children
                ?.first()?.children
            entries?.forEach { element ->
                when (element.fcKind) {
                    258 -> { // parse header collection
                        val sequence: Sequence<StoreItemWithArtwork> = sequence {
                            element.children.forEach { elementChild ->
                                when (elementChild.link.type) {
                                    "content" -> {
                                        val id = elementChild.link.contentId
                                        if (lockupResult.containsKey(id)) {
                                            lockupResult[id]?.let {
                                                yield(
                                                    StorePodcast(
                                                        id = it.id?.toLong() ?: 0,
                                                        name = it.name.orEmpty(),
                                                        url = it.url.orEmpty(),
                                                        artistName = it.artistName.orEmpty(),
                                                        artistId = it.artistId?.toLong(),
                                                        artistUrl = it.artistUrl,
                                                        description = it.description?.standard,
                                                        feedUrl = it.feedUrl.orEmpty(),
                                                        releaseDate = it.releaseDateTime
                                                            ?: Clock.System.now(),
                                                        releaseDateTime = it.releaseDateTime
                                                            ?: Clock.System.now(),
                                                        artwork = it.artwork?.toArtwork(),
                                                        trackCount = it.trackCount ?: 0,
                                                        podcastWebsiteUrl = it.podcastWebsiteUrl,
                                                        copyright = it.copyright,
                                                        contentAdvisoryRating = it.contentRatingsBySystem?.riaa?.name,
                                                        userRating = it.userRating?.value?.toFloat()
                                                            ?: 0f,
                                                        genre = it.genres.firstOrNull()
                                                            ?.toGenre(),
                                                        storeFront = storeFront
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    "link" -> {
                                        yield(
                                            StoreRoom(
                                                id = elementChild.adamId,
                                                label = elementChild.link.label,
                                                url = elementChild.link.url,
                                                artwork = elementChild.artwork!!.toArtwork(),
                                                storeFront = storeFront
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        yield(
                            StoreCollectionFeatured(
                                id = element.adamId,
                                items = sequence.toList(),
                                storeFront = storeFront
                            )
                        )
                    }
                    271 -> { // parse podcast collection
                        element.children.firstOrNull()?.let { elementChild ->
                            val ids =
                                elementChild.content.map { content -> content.contentId }
                            when (elementChild.type) {
                                "popularity" -> { // top podcasts // top episodes
                                    /*
                                    when (elementChild.fcKind) {
                                        // podcast
                                        16 -> yield(
                                            StoreCollectionTopPodcasts(
                                                id = elementChild.adamId,
                                                label = elementChild.name,
                                                genreId = DEFAULT_GENRE,
                                                itemsIds = ids.take(15),
                                                storeFront = storeFront,
                                            )
                                        )
                                        // episodes
                                        /*186 -> yield(
                                            StoreCollectionTopEpisodes(
                                                label = elementChild.name,
                                                genreId = DEFAULT_GENRE,
                                                storeList = ids.take(15)
                                                    .filter { storeLookup.contains(it) }
                                                    .map { storeLookup[it] }
                                                    .filterIsInstance<StoreEpisode>(),
                                                storeFront = storeFront,
                                            )
                                        )*/
                                        else -> {
                                        }
                                    }
                                     */
                                }
                                "normal" -> {
                                    yield(
                                        StoreCollectionItems(
                                            id = elementChild.adamId,
                                            label = elementChild.name,
                                            url = elementChild.seeAllUrl,
                                            itemsIds = ids,
                                            storeFront = storeFront,
                                            sortByPopularity = (elementChild.sort == 4)
                                        )
                                    )
                                }
                                else -> {
                                }
                            }
                        }
                    }
                    261 -> { // parse rooms collections / providers collections
                        val roomSequence: Sequence<StoreRoom> = sequence {
                            element.children.forEach { elementChild ->
                                when (elementChild.link.type) {
                                    "content" -> {
                                        val id = elementChild.link.contentId
                                        if (lockupResult.containsKey(id)) {
                                            val artist = lockupResult[id]
                                            yield(
                                                StoreRoom(
                                                    id = id,
                                                    label = artist?.name.orEmpty(),
                                                    url = artist?.url.orEmpty(),
                                                    storeFront = storeFront,
                                                    artwork = elementChild.artwork!!.toArtwork(),
                                                )
                                            )
                                        }
                                    }
                                    "link" -> {
                                        yield(
                                            StoreRoom(
                                                id = elementChild.adamId,
                                                label = elementChild.link.label,
                                                url = elementChild.link.url,
                                                storeFront = storeFront,
                                                artwork = elementChild.artwork!!.toArtwork(),
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        if (roomSequence.toList().isNotEmpty()) {
                            yield(
                                StoreCollectionRooms(
                                    id = element.adamId,
                                    label = element.name,
                                    items = roomSequence.toList(),
                                    storeFront = storeFront
                                )
                            )
                        }
                    }
                }
            }
        }

        val storeGenres: StoreCollectionGenres? =
            storePageDto.pageData?.categoryList?.let { categoryList ->
                StoreCollectionGenres(
                    label = categoryList.parentCategoryLabel.orEmpty(),
                    genres = categoryList.children.map { child -> child.toGenre() },
                    storeFront = storeFront
                )
            }

        return StoreGroupingPage(
            storeData = StoreGroupingData(
                id = storePageDto.pageData?.contentId.orEmpty(),
                label = storePageDto.pageData?.categoryList?.name.orEmpty(),
                storeFront = storeFront,
                storeList = collectionSequence.toMutableList(),
                genres = storeGenres,
            ),
            storeFront = storeFront,
            lookup = storeLookup,
            timestamp = timestamp,
            fetchedAt = Clock.System.now()
        )
    }


    /**
     * getArtistPodcastDataAsync
     */
    private fun getArtistPodcastDataAsync(storePageDto: StorePageDto): StoreRoomPage {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val ids = storePageDto.pageData?.contentData?.first()?.adamIds?.map { id -> id.toLong() }
            ?: emptyList()

        val storeData = StoreRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeIds = ids,
        )

        return StoreRoomPage(
            storeRoom = storeData,
            storeFront = storeFront,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup,
                storeFront)
        )
    }

    /**
     * getArtistProviderDataAsync
     */
    private fun getArtistProviderDataAsync(storePageDto: StorePageDto): StoreMultiRoomPage {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.contentData
            entries?.forEach { contentData ->
                val ids = contentData.adamIds.map { id -> id.toLong() }
                val dkId = contentData.dkId
                val chunkId = contentData.chunkId
                when {
                    dkId != null -> {
                        // popular episodes
                        yield(
                            StoreCollectionTopEpisodes(
                                id = dkId.toLong(),
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront,
                            )
                        )
                    }
                    chunkId.isNullOrEmpty() -> {
                        // popular podcasts
                        yield(
                            StoreCollectionTopPodcasts(
                                id = 0L,
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront,
                            )
                        )
                    }
                    else -> {
                        // regular podcasts
                        yield(
                            StoreCollectionItems(
                                id = chunkId.toLong(),
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront,
                            )
                        )
                    }
                }
            }
        }

        val storeData = StoreMultiRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.uber?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toMutableList(),
        )

        return StoreMultiRoomPage(
            storeRoom = storeData,
            storeFront = storeFront,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup,
                storeFront)
        )
    }

    /**
     * getRoomPodcastDataAsync
     */
    private fun getRoomPodcastDataAsync(storePageDto: StorePageDto): StoreRoomPage {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val isIndexed = (storePageDto.pageData?.defaultSort == 18)
        Timber.d("defaultSort: ${storePageDto.pageData?.defaultSort}")

        val storeData = StoreRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeIds = ids,
            isIndexed = isIndexed
        )

        return StoreRoomPage(
            storeRoom = storeData,
            storeFront = storeFront,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup,
                storeFront)
        )
    }

    /**
     * getMultiRoomDataAsync
     */
    private fun getMultiRoomDataAsync(storePageDto: StorePageDto): StoreMultiRoomPage {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.segments
            entries?.forEach { segmentData ->
                val ids = segmentData.adamIds
                yield(
                    StoreCollectionItems(
                        id = segmentData.adamId.toLong(),
                        label = segmentData.title,
                        url = segmentData.seeAllUrl?.url,
                        itemsIds = ids,
                        storeFront = storeFront
                    )
                )
            }
        }

        val storeData = StoreMultiRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.uber?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toMutableList(),
        )

        return StoreMultiRoomPage(
            storeRoom = storeData,
            storeFront = storeFront,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup,
                storeFront)
        )
    }

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): PodcastPage {
        // get grouping data
        val storeResponse = itunesAPI.storeData(storeFront = storeFront, url = url)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)

        // get missing lookup ids
        val moreByArtist =
            storePageDto.pageData?.moreByArtist?.map { it.toLong() }?.toSet() ?: emptySet()
        val listenersAlsoBought =
            storePageDto.pageData?.listenersAlsoBought?.map { it.toLong() }?.toSet()
                ?: emptySet()
        val topPodcastsInGenre =
            storePageDto.pageData?.topPodcastsInGenre?.map { it.toLong() }?.toSet()
                ?: emptySet()

        // parse podcast
        storePageDto.storePlatformData?.producDv?.results?.entries?.firstOrNull()
            ?.let { (_, podcastEntry) ->
                val podcastData =
                    getStoreItemFromLookupResultItem(podcastEntry, storeFront) as StorePodcast
                val podcastPage = PodcastPage(
                    podcast = podcastData.podcast,
                    storeFront = storeFront,
                    otherPodcasts = sequence<StoreCollection> {
                        if (moreByArtist.isEmpty().not()) {
                            yield(
                                StoreCollectionItems(
                                    0L,
                                    "podcastsByArtist",
                                    itemsIds = moreByArtist.toList(),
                                    storeFront = storeFront
                                )
                            )
                        }
                        if (listenersAlsoBought.isEmpty().not()) {
                            yield(
                                StoreCollectionItems(
                                    0L,
                                    "podcastsListenersAlsoFollow",
                                    itemsIds = listenersAlsoBought.toList(),
                                    storeFront = storeFront
                                )
                            )
                        }
                        if (topPodcastsInGenre.isEmpty().not()) {
                            yield(
                                StoreCollectionItems(
                                    0L,
                                    "topPodcastsInGenre",
                                    itemsIds = topPodcastsInGenre.toList(),
                                    storeFront = storeFront
                                )
                            )
                        }
                    }.toMutableList(),
                    episodes = podcastEntry.children.map { (key, episodeEntry) ->
                        Episode(
                            episodeId = key.toLong(),
                            name = episodeEntry.name.orEmpty(),
                            url = episodeEntry.url.orEmpty(),
                            podcastId = episodeEntry.collectionId?.toLong() ?: 0,
                            podcastName = episodeEntry.collectionName.orEmpty(),
                            artistName = episodeEntry.artistName.orEmpty(),
                            artistId = episodeEntry.artistId?.toLong(),
                            description = episodeEntry.description?.standard,
                            genre = emptyList(),//episodeEntry.genres.map { it.toGenre() }.first(),
                            feedUrl = episodeEntry.feedUrl.orEmpty(),
                            releaseDateTime = episodeEntry.releaseDateTime
                                ?: Clock.System.now(),
                            artwork = episodeEntry.artwork?.toArtwork(),
                            contentAdvisoryRating = episodeEntry.contentRatingsBySystem?.riaa?.name,
                            mediaUrl = episodeEntry.offers.firstOrNull()?.download?.url.orEmpty(),
                            mediaType = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.fileExtension.orEmpty(),
                            duration = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.duration?.toLong()
                                ?: 0L,
                            podcastEpisodeNumber = episodeEntry.podcastEpisodeNumber?.toLong(),
                            podcastEpisodeSeason = episodeEntry.podcastEpisodeSeason?.toLong(),
                            podcastEpisodeType = episodeEntry.podcastEpisodeType.orEmpty(),
                            podcastEpisodeWebsiteUrl = episodeEntry.podcastEpisodeWebsiteUrl,
                            updatedAt = Clock.System.now()
                        )
                    },
                    timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
                )

                return podcastPage
            }
            ?: throw Exception("missing podcast entry")
    }

    /**
     * loadDirectoryPagingData
     */
    @OptIn(ExperimentalPagingApi::class)
    fun loadPodcastEpisodesPagingData(
        podcast: Podcast,
        storeFront: String,
    ): Flow<PagingData<EpisodeSummary>> {
        val mediator = PodcastRemoteMediator(
            podcast = podcast, storeFront = storeFront, storeRepository = this,
            database = database
        )
        val pagingSourceFactory =
            QueryDataSourceFactory(
                queryProvider = { limit, offset ->
                    database.episodeQueries.getAllPagedByPodcastId(podcastId = podcast.podcastId,
                        limit = limit,
                        offset = offset)
                },
                countQuery = database.episodeQueries.countAllByPodcastId(podcastId = podcast.podcastId),
            ).asPagingSourceFactory()

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = mediator,
            pagingSourceFactory = { pagingSourceFactory.invoke() }
        ).flow
    }


    /**
     * getTopChartsAsync
     */
    suspend fun getTopChartsAsync(storeFront: String, genreId: Int?): StoreTopCharts
    {
        val podcastIds: MutableList<Long> = mutableListOf();
        val episodeIds: MutableList<Long> = mutableListOf();

        // get top charts data
        val storeResponse = itunesAPI.topCharts(storeFront, genreId ?: DEFAULT_GENRE)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        Timber.d("DBG - getTopChartsAsync body")
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        storePageDto.pageData
            ?.segmentedControl?.segments
            ?.firstOrNull()
            ?.pageData?.topCharts?.forEach {
                if(it.kinds?.podcast == true) {
                    podcastIds += it.adamIds
                }
                else if (it.kinds?.podcastEpisode == true) {
                    episodeIds += it.adamIds
                }
            }

        return StoreTopCharts(
            id = 0,
            label = storePageDto.pageData
                ?.segmentedControl?.segments
                ?.firstOrNull()?.title.orEmpty(),
            storeFront = storeFront,
            storeEpisodesIds = episodeIds,
            storePodcastsIds = podcastIds,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront),
            timestamp = timestamp
        )
    }

    /**
     * getTopChartsPodcastsIdsAsync
     */
    suspend fun getTopChartsIdsAsync(
        genre: Int?,
        storeFront: String,
        storeItemType: StoreItemType,
        limit: Int
    ): List<Long> {
        // get top charts data
        val type = when (storeItemType) {
            StoreItemType.PODCAST -> "Podcasts"
            StoreItemType.EPISODE -> "PodcastEpisodes"
        }
        val storeResponse = itunesAPI.topChartsIds(storeFront = storeFront,
            genre = genre ?: DEFAULT_GENRE,
            limit = limit,
            name = type)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val resultIdsResult = storeResponse.body() ?: throw HttpException(storeResponse)
        return resultIdsResult.resultIds
    }

    /*
     * getTopChartPagingData
     */
    fun getTopChartPagingData(
        scope: CoroutineScope,
        genreId: Int?,
        type: StoreItemType,
        storeFront: String,
        dataLoadedCallback: ((StoreTopCharts) -> Unit)?): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            )
        ) {
            StoreChartsPagingSource(
                scope = scope,
                itemType = type,
                loadDataFromNetwork = { getTopChartsAsync(storeFront, genreId) },
                getStoreItems = { ids, storeFront, storeData -> getListStoreItemDataAsync(ids, storeFront, storeData) },
                dataLoadedCallback = dataLoadedCallback
            )
        }.flow

    /**
     * getStoreItemFromLookupResultItem
     */
    private fun getStoreItemFromLookupResultItem(
        item: com.caldeirasoft.outcast.domain.dto.LookupResultItem,
        storeFront: String
    ): StoreItemWithArtwork? =
        when (item.kind) {
            "podcast" -> {
                StorePodcast(
                    id = item.id?.toLong() ?: 0,
                    name = item.name.orEmpty(),
                    url = item.url.orEmpty(),
                    artistName = item.artistName.orEmpty(),
                    artistId = item.artistId?.toLong(),
                    artistUrl = item.artistUrl,
                    description = item.description?.standard,
                    feedUrl = item.feedUrl.orEmpty(),
                    releaseDate = item.releaseDateTime ?: Clock.System.now(),
                    releaseDateTime = item.releaseDateTime ?: Clock.System.now(),
                    artwork = item.artwork?.toArtwork(),
                    trackCount = item.trackCount ?: 0,
                    podcastWebsiteUrl = item.podcastWebsiteUrl,
                    copyright = item.copyright,
                    contentAdvisoryRating = item.contentRatingsBySystem?.riaa?.name,
                    userRating = item.userRating?.value?.toFloat() ?: 0f,
                    genre = item.genres.firstOrNull()?.toGenre(),
                    storeFront = storeFront
                )
            }
            "podcastEpisode" -> {
                StoreEpisode(
                    id = item.id?.toLong() ?: 0,
                    name = item.name.orEmpty(),
                    url = item.url.orEmpty(),
                    podcastId = item.collectionId?.toLong() ?: 0,
                    podcastName = item.collectionName.orEmpty(),
                    artistName = item.artistName.orEmpty(),
                    artistId = item.artistId?.toLong(),
                    description = item.description?.standard,
                    genres = item.genres.map { it.toGenre() },
                    feedUrl = item.feedUrl.orEmpty(),
                    releaseDateTime = item.releaseDateTime ?: Clock.System.now(),
                    artwork = item.artwork?.toArtwork(),
                    contentAdvisoryRating = item.contentRatingsBySystem?.riaa?.name,
                    mediaUrl = item.offers.firstOrNull()?.download?.url.orEmpty(),
                    mediaType = item.offers.firstOrNull()?.assets?.firstOrNull()?.fileExtension.orEmpty(),
                    duration = item.offers.firstOrNull()?.assets?.firstOrNull()?.duration ?: 0,
                    podcastEpisodeNumber = item.podcastEpisodeNumber,
                    podcastEpisodeSeason = item.podcastEpisodeSeason,
                    podcastEpisodeType = item.podcastEpisodeType.orEmpty(),
                    podcastEpisodeWebsiteUrl = item.podcastEpisodeWebsiteUrl,
                    storeFront = storeFront,
                    isComplete = false,
                    podcast = requireNotNull(
                        getStoreItemFromLookupResultItem(item.collection.values.first(),
                            storeFront) as StorePodcast)
                )
            }
            else -> null
        }

    /**
     * getStoreLookupFromLookupResult
     */
    private fun getStoreLookupFromLookupResult(
        lockupResult: com.caldeirasoft.outcast.domain.dto.LockupResult?,
        storeFront: String
    ): Map<Long, StoreItemWithArtwork> {
        val resultMap: HashMap<Long, StoreItemWithArtwork> = hashMapOf()
        lockupResult?.results
            ?.mapValues { it -> getStoreItemFromLookupResultItem(it.value, storeFront) }
            ?.forEach {
                it.value?.let { value ->
                    resultMap[it.key] = value
                }
            }

        return resultMap
    }

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(
        lookupIds: List<Long>,
        storeFront: String,
        storePage: StorePage?
    ): List<StoreItemWithArtwork> {
        val newLookup: MutableMap<Long, StoreItemWithArtwork> = mutableMapOf()
        storePage?.lookup?.let { map ->
            newLookup.putAll(map)
        }
        // get current lookup ids
        val currentLookupIds: Set<Long> = newLookup.keys
        // get missing lookup ids for room
        val dataIds: List<Long> = lookupIds.subtract(currentLookupIds).toList()

        // retrieve missing lookups
        if (dataIds.isNotEmpty()) {
            val storeResponse =
                itunesAPI.lookup(storeFront = storeFront, ids = dataIds.joinToString(","))
            if (storeResponse.isSuccessful.not())
                throw HttpException(storeResponse)
            val lookupItems = storeResponse.body() ?: throw HttpException(storeResponse)
            lookupItems.results
                .mapValues { v -> getStoreItemFromLookupResultItem(v.value, storeFront) }
                .forEach {
                    it.value?.let { value -> newLookup.put(it.key, value) }
                }
        }

        return lookupIds
            .filter { id -> newLookup.containsKey(id) }
            .mapNotNull { id -> newLookup.get(id) }
    }

    /**
     * getGenresDataAsync
     */
    suspend fun getStoreGenreDataAsync(storeFront: String): StoreGenreData {
        val storeResponse = itunesAPI.genres(storeFront)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val map: Map<Int, GenreResult> = storeResponse.body() ?: throw HttpException(storeResponse)

        val rootEntry = map.values.first()
        return StoreGenreData(
            root = rootEntry.toStoreGenre(storeFront),
            genres = rootEntry.subgenres.values.map { it.toStoreGenre(storeFront) }
        )
    }

    /**
     * getGenresDataAsync
     */
    suspend fun loadStoreGenreData(storeFront: String): StoreGenreData {
        val storeDataCache = cache.get("genres") { getStoreGenreDataAsync(storeFront) }
        if (storeDataCache.root.storeFront != storeFront) {
            return getStoreGenreDataAsync(storeFront).also {
                cache.set("genres", storeFront)
            }
        }
        return storeDataCache
    }
}