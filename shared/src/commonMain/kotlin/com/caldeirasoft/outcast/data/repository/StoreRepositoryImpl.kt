package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.*
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.checkType
import com.caldeirasoft.outcast.domain.util.stopwatch
import com.caldeirasoft.outcast.domain.util.tryCast
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class StoreRepositoryImpl (
    val httpClient:HttpClient,
) : StoreRepository {

    companion object {
        const val DEFAULT_GENRE = 26
        const val GENRE_URL = "https://podcasts.apple.com/genre/id{genre}"
        const val GENRES_URL = "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/genres"
        const val TOP_CHARTS_IDS_URL = "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts"
        const val TOP_CHARTS_URL = "https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewTop"
        const val LOOKUP_URL = "https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup"
    }

    /**
     * getStoreDataAsync
     */
    override suspend fun getStoreDataAsync(
        url: String,
        storeFront: String
    ): StorePage {
        val storePageDto = getStoreDataApi(url, storeFront)
        // retrieve data
        return when (storePageDto.pageData?.componentName) {
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
                when (storePageDto.pageData.metricsBase?.pageType) {
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
    override suspend fun getGroupingDataAsync(genre: Int?, storeFront: String): StoreGroupingPage {
        val url = GENRE_URL.replace("{genre}", (genre ?: DEFAULT_GENRE).toString())
        val storeData = getStoreDataAsync(url, storeFront)
        if (storeData !is StoreGroupingPage)
            throw NullPointerException("Invalid cast to StoreGroupingPage")
        return storeData
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
                ?.first()?.children;
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
            storePageDto.pageData?.categoryList?.children?.let {
                StoreCollectionGenres(
                    id = storePageDto.pageData.categoryList.genreId.toLong(),
                    label = storePageDto.pageData.categoryList.parentCategoryLabel.orEmpty(),
                    genres = it.map { child -> child.toStoreGenre(storeFront) },
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
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
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
                when {
                    contentData.dkId != null -> {
                        // popular episodes
                        yield(
                            StoreCollectionTopEpisodes(
                                id = contentData.dkId.toLong(),
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront,
                            )
                        )
                    }
                    contentData.chunkId.isNullOrEmpty() -> {
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
                                id = contentData.chunkId.toLong(),
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
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
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
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
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
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getPodcastDataAsync
     */
    override suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcastPage {
        // get grouping data
        val storePageDto = getStoreDataApi(url, storeFront)

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
            ?.let { (key, podcastEntry) ->
                val podcastData = getStoreItemFromLookupResultItem(podcastEntry, storeFront) as StorePodcast
                val podcastPage = StorePodcastPage(
                    storeData = podcastData,
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
                        StoreEpisode(
                            id = key.toLong(),
                            name = episodeEntry.name.orEmpty(),
                            podcastId = episodeEntry.collectionId?.toLong() ?: 0,
                            podcastName = episodeEntry.collectionName.orEmpty(),
                            artistName = episodeEntry.artistName.orEmpty(),
                            artistId = episodeEntry.artistId?.toLong(),
                            description = episodeEntry.description?.standard,
                            genres = episodeEntry.genres.map { it.toGenre() },
                            feedUrl = episodeEntry.feedUrl.orEmpty(),
                            releaseDateTime = episodeEntry.releaseDateTime
                                ?: Clock.System.now(),
                            artwork = episodeEntry.artwork?.toArtwork(),
                            contentAdvisoryRating = episodeEntry.contentRatingsBySystem?.riaa?.name,
                            mediaUrl = episodeEntry.offers.firstOrNull()?.download?.url.orEmpty(),
                            mediaType = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.fileExtension.orEmpty(),
                            duration = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.duration
                                ?: 0,
                            podcastEpisodeNumber = episodeEntry.podcastEpisodeNumber,
                            podcastEpisodeSeason = episodeEntry.podcastEpisodeSeason,
                            podcastEpisodeType = episodeEntry.podcastEpisodeType.orEmpty(),
                            podcastEpisodeWebsiteUrl = episodeEntry.podcastEpisodeWebsiteUrl,
                            podcast = podcastData
                        )
                    },
                    timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
                )

                return podcastPage
            }
            ?: throw Exception("missing podcast entry")
    }

    /**
     * getTopChartsPodcastsIdsAsync
     */
    override suspend fun getTopChartsIdsAsync(
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
        val resultIdsResult = getTopChartsIdsAsync(genre, type, storeFront, limit)
        return resultIdsResult.resultIds
    }

    /**
     * getTopChartsIdsAsync
     */
    private suspend fun getTopChartsIdsAsync(genre: Int?, type: String, storeFront: String, limit: Int): ResultIdsResult =
        httpClient.get {
            url(TOP_CHARTS_IDS_URL)
            parameter("name", type)
            genre?.let {
                parameter("g", genre)
            }
            parameter("limit", limit)
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * getGenresDataAsync
     */
    override suspend fun getGenresDataAsync(storeFront: String): StoreGenreData {
        val map: Map<Int, GenreResult> = getGenreMapAsync(GENRES_URL, storeFront)

        val rootEntry = map.values.first()
        return StoreGenreData(
            root = rootEntry.toStoreGenre(storeFront),
            genres = rootEntry.subgenres.values.map { it.toStoreGenre(storeFront) }
        )
    }

    /**
     * getStoreItemFromLookupResultItem
     */
    private fun getStoreItemFromLookupResultItem(item: LookupResultItem, storeFront: String): StoreItemWithArtwork? =
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
                    podcast = requireNotNull(
                        getStoreItemFromLookupResultItem(item.collection.values.first(), storeFront) as StorePodcast)
                )
            }
            else -> null
        }

    /**
     * getStoreLookupFromLookupResult
     */
    private fun getStoreLookupFromLookupResult(lockupResult: LockupResult?, storeFront: String) : Map<Long, StoreItemWithArtwork> {
        val resultMap : HashMap<Long, StoreItemWithArtwork> = hashMapOf()
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
    override suspend fun getListStoreItemDataAsync(
        lookupIds: List<Long>,
        storeFront: String,
        storePage: StorePage?): List<StoreItemWithArtwork>
    {
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
            val lookupItems = stopwatch("getListStoreItemDataAsync - getLookupDataAsync") { getLookupDataAsync(dataIds, storeFront) }
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
     * getLookupDataAsync
     */
    private suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): LockupResult =
        httpClient.get {
            url(LOOKUP_URL)
            parameter("version", 2)
            parameter("p", "lockup")
            parameter("caller", "DI6")
            parameter("id", lookupIds.joinToString(","))
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * getStoreDataApi
     */
    private suspend fun getStoreDataApi(url: String, storeFront: String): StorePageDto =
        httpClient.get {
            url(url)
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * getGenreMapAsync
     */
    suspend fun getGenreMapAsync(url: String, storeFront: String): Map<Int, GenreResult> =
        httpClient.get {
            url(url)
            parameter("id", 26)
            header("X-Apple-Store-Front", storeFront)
        }
}