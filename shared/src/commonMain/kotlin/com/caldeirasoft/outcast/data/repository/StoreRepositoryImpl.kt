package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.stopwatch
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

class StoreRepositoryImpl (
    val httpClient:HttpClient,
) : StoreRepository {

    /**
     * getPodcastDirectoryDataAsync
     */
    @ExperimentalTime
    override suspend fun getDirectoryDataAsync(storeFront: String): StoreGroupingData {
        // get grouping data
        val storePageDto = stopwatch("getDirectoryDataAsync - getStoreDataApi") { getStoreDataApi(
            "https://itunes.apple.com/genre/id26",
            storeFront
        ) }

        // parse store page data
        val lockupResult = storePageDto.storePlatformData?.lockup?.results ?: emptyMap()
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
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
                                            lockupResult.get(id)?.let {
                                                yield(
                                                    StorePodcastFeatured(
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
                                                        artwork = elementChild.artwork!!.toArtwork(),
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
                                            StoreRoomFeatured(
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
                            StoreCollectionFeatured(items = sequence.toList(), storeFront = storeFront)
                        )
                    }
                    271 -> { // parse podcast collection
                        element.children.firstOrNull()?.let { elementChild ->
                            val ids =
                                elementChild.content.map { content -> content.contentId }
                            when (elementChild.type) {
                                "popularity" -> {
                                }
                                "normal" -> {
                                    yield(
                                        StoreCollectionPodcasts(
                                            label = elementChild.name,
                                            url = elementChild.seeAllUrl,
                                            itemsIds = ids,
                                            storeFront = storeFront
                                        )
                                    )
                                }
                            }
                        }
                    }
                    261 -> { // parse rooms collections / providers collections
                        val roomSequence: Sequence<StoreItemWithArtwork> = sequence {
                            element.children.forEach { elementChild ->
                                when (elementChild.link.type) {
                                    "content" -> {
                                        val id = elementChild.link.contentId
                                        if (lockupResult.containsKey(id)) {
                                            val artist = lockupResult.get(id)
                                            yield(
                                                StoreRoom(
                                                    id = id,
                                                    label = artist?.name.orEmpty(),
                                                    url = artist?.url.orEmpty(),
                                                    storeFront = storeFront,
                                                    artwork = elementChild.artwork!!.toArtwork()
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
                                                artwork = elementChild.artwork!!.toArtwork()
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        if (roomSequence.toList().isNotEmpty()) {
                            yield(
                                StoreCollectionRooms(
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

        return StoreGroupingData(
            id = "0",
            label = storePageDto.pageData?.categoryList?.name.orEmpty(),
            storeList = collectionSequence.toList(),
            storeFront = storeFront,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getStoreDataAsync
     */
    override suspend fun getStoreDataAsync(
        url: String,
        storeFront: String
    ): StoreData {
        val storePageDto = stopwatch("getDirectoryDataAsync - getStoreDataAsync") { getStoreDataApi(url, storeFront) }
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
                when (storePageDto.pageData!!.metricsBase?.pageType) {
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
    private fun getGroupingDataAsync(storePageDto: StorePageDto): StoreGroupingData {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.fcStructure?.model?.children
                ?.first { element -> element.token == "allPodcasts" }?.children
                ?.first()?.children;
            entries?.forEach { element ->
                when (element.fcKind) {
                    271 -> { // parse podcast collection
                        element.children.firstOrNull()?.let { elementChild ->
                            val ids =
                                elementChild.content.map { content -> content.contentId }
                            yield(
                                StoreCollectionPodcasts(
                                    label = elementChild.name,
                                    url = elementChild.seeAllUrl,
                                    itemsIds = ids,
                                    storeFront = storeFront
                                )
                            )
                        }
                    }
                }
            }
        }

        return StoreGroupingData(
            id = "0",
            label = storePageDto.pageData?.categoryList?.name.orEmpty(),
            storeFront = storeFront,
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getArtistPodcastDataAsync
     */
    private fun getArtistPodcastDataAsync(storePageDto: StorePageDto): StoreRoom {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val ids = storePageDto.pageData?.contentData?.first()?.adamIds?.map { id -> id.toLong() }
            ?: emptyList()
        val artistData = StoreRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeIds = ids,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )

        return artistData
    }

    /**
     * getArtistProviderDataAsync
     */
    private fun getArtistProviderDataAsync(storePageDto: StorePageDto): StoreMultiRoom {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.contentData
            entries?.forEach { contentData ->
                when (contentData.dkId) {
                    1 -> { // parse episodes
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreCollectionEpisodes(
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront
                            )
                        )
                    }
                    else -> { // parse podcasts
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreCollectionPodcasts(
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront
                            )
                        )
                    }
                }
            }
        }

        return StoreMultiRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.uber?.masterArt?.firstOrNull()?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getRoomPodcastDataAsync
     */
    fun getRoomPodcastDataAsync(storePageDto: StorePageDto): StoreRoom {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()

        return StoreRoom(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty(),
            storeIds = ids,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getMultiRoomDataAsync
     */
    private fun getMultiRoomDataAsync(storePageDto: StorePageDto): StoreMultiRoom {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val collectionSequence: Sequence<StoreCollectionPodcasts> = sequence {
            val entries = storePageDto.pageData?.segments
            entries?.forEach { segmentData ->
                val ids = segmentData.adamIds.map { id -> id.toLong() }
                yield(
                    StoreCollectionPodcasts(
                        label = segmentData.title,
                        url = segmentData.seeAllUrl?.url,
                        itemsIds = ids,
                        storeFront = storeFront
                    )
                )
            }
        }

        return StoreMultiRoom(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getPodcastDataAsync
     */
    override suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcast {
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
                val podcastData = StorePodcast(
                    id = key,
                    name = podcastEntry.name.orEmpty(),
                    url = podcastEntry.url.orEmpty(),
                    artistName = podcastEntry.artistName.orEmpty(),
                    artistId = podcastEntry.artistId?.toLong(),
                    artistUrl = podcastEntry.artistUrl,
                    description = podcastEntry.description?.standard,
                    feedUrl = podcastEntry.feedUrl.orEmpty(),
                    releaseDate = podcastEntry.releaseDateTime ?: Clock.System.now(),
                    releaseDateTime = podcastEntry.releaseDateTime ?: Clock.System.now(),
                    artwork = podcastEntry.artwork?.toArtwork(),
                    trackCount = podcastEntry.trackCount ?: 0,
                    podcastWebsiteUrl = podcastEntry.podcastWebsiteUrl,
                    copyright = podcastEntry.copyright,
                    contentAdvisoryRating = podcastEntry.contentRatingsBySystem?.riaa?.name,
                    userRating = podcastEntry.userRating?.value?.toFloat() ?: 0f,
                    genre = podcastEntry.genres.firstOrNull()?.toGenre(),
                    storeFront = storeFront,
                    podcastsByArtist = StoreCollectionPodcasts(
                        "",
                        itemsIds = moreByArtist.toList(),
                        storeFront = storeFront
                    ),
                    podcastsListenersAlsoFollow = StoreCollectionPodcasts(
                        "",
                        itemsIds = listenersAlsoBought.toList(),
                        storeFront = storeFront
                    ),
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
                        )
                    },
                )

                return podcastData
            }
            ?: throw Exception("missing podcast entry")
    }

    /**
     * getTopChartsAsync
     */
    override suspend fun getTopChartsAsync(storeFront: String): StoreTopCharts
    {
        val podcastIds: MutableList<Long> = mutableListOf();
        val episodeIds: MutableList<Long> = mutableListOf();

        // get top charts data
        val storePageDto = stopwatch("getDirectoryDataAsync - getTopChartsAsync") { getStoreDataApi(
            "https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewTop?genreId=26",
            storeFront
        ) }
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
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
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
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
                    storeFront = storeFront
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
                    resultMap.put(it.key, value)
                }
            }

        return resultMap
    }

    /**
     * getListStoreItemDataAsync
     */
    override suspend fun getListStoreItemDataAsync(
        lookupIds: List<Long>,
        storePage: StorePage): List<StoreItemWithArtwork>
    {
        val newLookup: MutableMap<Long, StoreItemWithArtwork> = mutableMapOf()
        newLookup.putAll(storePage.lookup)
        // get current lookup ids
        val currentLookupIds: Set<Long> = newLookup.keys
        // get missing lookup ids for room
        val dataIds: List<Long> = lookupIds.subtract(currentLookupIds).toList()

        // retrieve missing lookups
        if (dataIds.isNotEmpty()) {
            val lookupItems = stopwatch("getListStoreItemDataAsync - getLookupDataAsync") { getLookupDataAsync(dataIds, storePage.storeFront) }
            lookupItems.results
                .mapValues { v -> getStoreItemFromLookupResultItem(v.value, storePage.storeFront) }
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
    override suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): LockupResult =
        httpClient.get {
            url("https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup")
            parameter("version", 2)
            parameter("p", "lockup")
            parameter("caller", "DI6")
            parameter("id", lookupIds.joinToString(","))
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * getStoreDataApi
     */
    override suspend fun getStoreDataApi(url: String, storeFront: String): StorePageDto =
        httpClient.get {
            url(url)
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * Fetch data from network
     */
    private suspend inline fun <T: Any> fetchData(crossinline networkCall: suspend () -> T): T {
        val data = networkCall.invoke()
        return data
    }

    /**
     * Fetch data from network
     */
    private suspend inline fun <T: Any> fetchNetworkData(crossinline networkCall: suspend () -> T): NetworkResponse<T> {
        try {
            val data = networkCall.invoke()
            return NetworkResponse.Success(data)
        }
        catch (e: ResponseException) {
            val code = e.response?.status?.value ?: 0
            val body = e.response?.readText()
            return NetworkResponse.ServerError(body, e.response?.status?.value ?: 0)
        }
        catch (e: Exception) {
            return NetworkResponse.UnknownError(Throwable(e))
        }
        catch (t: Throwable) {
            return NetworkResponse.UnknownError(t)
        }
    }
}