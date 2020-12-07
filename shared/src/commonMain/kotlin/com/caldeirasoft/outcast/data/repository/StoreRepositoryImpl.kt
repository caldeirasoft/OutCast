package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.ResultIdsResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.stopwatch
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

class StoreRepositoryImpl (
    val httpClient:HttpClient,
) : StoreRepository {

    companion object {
        const val DIRECTORY_URL = "https://itunes.apple.com/genre/id26"
        const val TOP_CHARTS_PODCASTS_URL = "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts?name=Podcasts"
        const val TOP_CHARTS_EPISODES_URL = "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts?name=PodcastEpisodes"
        const val LOOKUP_URL = "https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup"
    }

    /**
     * getPodcastDirectoryDataAsync
     */
    @ExperimentalTime
    override suspend fun getDirectoryDataAsync(storeFront: String): StoreDirectory {
        // get grouping data
        val storePageDto = stopwatch("getDirectoryDataAsync - getStoreDataApi") {
            getStoreDataApi(DIRECTORY_URL, storeFront) }

        // parse store page data
        val lockupResult = storePageDto.storePlatformData?.lockup?.results ?: emptyMap()
        val storeFrontResult = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        var topPodcasts: StoreCollectionPodcasts? = null
        var topEpisodes: StoreCollectionEpisodes? = null
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
                                    when (elementChild.fcKind) {
                                        // podcast
                                        16 -> topPodcasts = StoreCollectionPodcasts(
                                                label = elementChild.name,
                                                url = TOP_CHARTS_PODCASTS_URL,
                                                itemsIds = ids.take(30), /*elementChild.displayCount*/
                                                storeFront = storeFront
                                            )
                                        186 -> topEpisodes = StoreCollectionEpisodes(
                                            label = elementChild.name,
                                            url = TOP_CHARTS_EPISODES_URL,
                                            itemsIds = ids.take(30), /*elementChild.displayCount*/
                                            storeFront = storeFront
                                        )
                                        else -> { }
                                    }
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
                                else -> { }
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

        return StoreDirectory(
            id = "0",
            label = storePageDto.pageData?.categoryList?.name.orEmpty(),
            storeList = collectionSequence.toList(),
            storeFront = storeFront,
            topPodcasts = topPodcasts ?: throw Exception("missing top podcasts"),
            topEpisodes = topEpisodes ?: throw Exception("missing top episodes"),
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getStoreDataAsync
     */
    override suspend fun getStoreDataAsync(
        url: String,
        storeFront: String
    ): StorePage {
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
    private fun getGroupingDataAsync(storePageDto: StorePageDto): StoreGroupingData {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
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
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
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
        val artistData = StoreRoomPage(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeIds = ids,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )

        return artistData
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

        return StoreMultiRoomPage(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.uber?.masterArt?.firstOrNull()?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toList(),
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        )
    }

    /**
     * getRoomPodcastDataAsync
     */
    fun getRoomPodcastDataAsync(storePageDto: StorePageDto): StoreRoomPage {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()

        return StoreRoomPage(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty(),
            storeIds = ids,
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

        return StoreMultiRoomPage(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeList = collectionSequence.toList(),
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
                val podcastData = StorePodcastPage(
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
    override suspend fun getTopChartsIdsAsync(url: String, storeFront: String): List<Long>
    {
        // get top charts data
        val resultIdsResult = stopwatch("getDirectoryDataAsync - getTopChartsIdsAsync") { getResultIdsAsync(
            url,
            storeFront
        ) }

        return resultIdsResult.results
    }

    /**
     * getUrlTopChartsPodcastsIds
     */
    private fun getUrlTopChartsPodcastsIds(limit: Int): String =
        "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts?limit=${limit}&name=Podcasts"


    /**
     * getUrlTopChartsEpisodesIds
     */
    private fun getUrlTopChartsEpisodesIds(limit: Int): String =
        "https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts?limit=${limit}&name=PodcastEpisodes"

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
    private suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): LockupResult =
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
    private suspend fun getStoreDataApi(url: String, storeFront: String): StorePageDto =
        httpClient.get {
            url(url)
            header("X-Apple-Store-Front", storeFront)
        }

    /**
     * getStoreDataApi
     */
    suspend fun getResultIdsAsync(url: String, storeFront: String): ResultIdsResult =
        httpClient.get {
            url(url)
            header("X-Apple-Store-Front", storeFront)
        }
}