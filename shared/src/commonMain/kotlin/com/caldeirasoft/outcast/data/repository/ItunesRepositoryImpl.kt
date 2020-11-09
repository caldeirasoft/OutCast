package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class ItunesRepositoryImpl(val httpClient:HttpClient) : ItunesRepository {

    /**
     * getPodcastDirectoryDataAsync
     */
    override suspend fun getPodcastDirectoryDataAsync(storeFront: String): NetworkResponse<StoreDataGrouping> =
        // get grouping data
        when (val networkResponse = getStoreDataApi("https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewPodcastDirectory", storeFront)) {
            is NetworkResponse.Success -> {
                val storePageDto: StorePageDto = networkResponse.body

                // parse store page data
                val lockupResult = storePageDto.storePlatformData?.lockup?.results ?: emptyMap()
                val collectionSequence: Sequence<StoreCollection> = sequence {
                    val entries = storePageDto.pageData?.fcStructure?.model?.children
                        ?.first { element -> element.token == "allPodcasts" }?.children
                        ?.first()?.children;
                    entries?.forEach { element ->
                        when (element.fcKind) {
                            258 -> { // parse header collection
                                val sequence: Sequence<StoreItem> = sequence {
                                    element.children.forEach { elementChild ->
                                        when (elementChild.link.type) {
                                            "content" -> {
                                                val id = elementChild.link.contentId
                                                if (lockupResult.containsKey(id)) {
                                                    lockupResult.get(id)?.let {
                                                        yield(
                                                            StoreItemPodcastFeatured(
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
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                            "link" -> {
                                                yield(
                                                    StoreItemRoomFeatured(
                                                        label = elementChild.link.label,
                                                        url = elementChild.link.url,
                                                        artwork = elementChild.artwork!!.toArtwork()
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                yield(
                                    StoreCollectionFeatured(items = sequence.toList())
                                )
                            }
                            271 -> { // parse podcast collection
                                element.children.firstOrNull()?.let { elementChild ->
                                    val ids =
                                        elementChild.content.map { content -> content.contentId }
                                    when (elementChild.type) {
                                        "popularity" -> { }
                                        "normal" -> {
                                            yield(StoreIdsPodcasts(
                                                label = elementChild.name,
                                                url = elementChild.seeAllUrl,
                                                itemsIds = ids
                                            ))
                                        }
                                    }
                                }
                            }
                            261 -> { // parse rooms collections / providers collections
                                val roomSequence: Sequence<StoreItem> = sequence {
                                    element.children.forEach { elementChild ->
                                        when (elementChild.link.type) {
                                            "content" -> {
                                                val id = elementChild.link.contentId
                                                if (lockupResult.containsKey(id)) {
                                                    val artist = lockupResult.get(id)
                                                    yield(
                                                        StoreItemRoomFeatured(
                                                            label = artist?.name.orEmpty(),
                                                            url = artist?.url.orEmpty(),
                                                            artwork = elementChild.artwork!!.toArtwork()
                                                        )
                                                    )
                                                }
                                            }
                                            "link" -> {
                                                yield(
                                                    StoreItemRoom(
                                                        label = elementChild.link.label,
                                                        url = elementChild.link.url,
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
                                            items = roomSequence.toList()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                val dataGrouping = StoreDataGrouping(
                    id = "0",
                    label = storePageDto.pageData?.categoryList?.name.orEmpty(),
                    storeList = collectionSequence.toList(),
                    lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
                )
                NetworkResponse.Success(dataGrouping)
            }
            is NetworkResponse.NetworkError ->
                networkResponse
            is NetworkResponse.ServerError ->
                NetworkResponse.ServerError(null, networkResponse.code)
            is NetworkResponse.UnknownError ->
                networkResponse
            else ->
                NetworkResponse.UnknownError(Exception())
        }

    override suspend fun getStoreDataAsync(
        url: String,
        storeFront: String
    ): NetworkResponse<StoreData> =
        when (val networkResponse = getStoreDataApi(url, storeFront)) {
            is NetworkResponse.Success -> {
                val storePageDto: StorePageDto = networkResponse.body
                // retrieve data
                when (storePageDto.pageData?.componentName) {
                    "room_page" -> {
                        getRoomPodcastDataAsync(storePageDto)
                    }
                    "multi_room_page" -> {
                        getMultiRoomDataAsync(storePageDto)
                    }
                    "grouping_page" -> {
                        getGroupingDataAsync(storePageDto)
                    }
                    "artist_page" -> {
                        when (storePageDto.pageData.metricsBase?.pageType) {
                            "Artist" ->
                                getArtistPodcastDataAsync(storePageDto)
                            "Provider" ->
                                getArtistProviderDataAsync(storePageDto)
                            else ->
                                NetworkResponse.UnknownError(Exception())
                        }
                    }
                    else ->
                        NetworkResponse.UnknownError(Exception())
                }
            }
            is NetworkResponse.NetworkError ->
                networkResponse
            is NetworkResponse.ServerError ->
                NetworkResponse.ServerError(null, networkResponse.code)
            is NetworkResponse.UnknownError ->
                networkResponse
            else ->
                NetworkResponse.UnknownError(Exception())
        }


    /**
     * getGroupingDataAsync
     */
    fun getGroupingDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreDataGrouping> {
        // parse store page data
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
                                StoreIdsPodcasts(
                                    label = elementChild.name,
                                    url = elementChild.seeAllUrl,
                                    itemsIds = ids
                                )
                            )
                        }
                    }
                }
            }
        }

        val dataGrouping = StoreDataGrouping(
            id = "0",
            label = storePageDto.pageData?.categoryList?.name.orEmpty(),
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
        )
        return NetworkResponse.Success(dataGrouping)
    }

    /**
     * getArtistPodcastDataAsync
     */
    fun getArtistPodcastDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreDataRoom> {
        // parse store page data
        val ids = storePageDto.pageData?.contentData?.first()?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val artistData = StoreDataRoom(
            id = storePageDto.pageData?.artist?.adamId.orEmpty(),
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork(),
            storeIds = ids,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
        )

        return NetworkResponse.Success(artistData)
    }

    /**
     * getArtistProviderDataAsync
     */
    fun getArtistProviderDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreDataMultiRoom> {
        val collectionSequence: Sequence<StoreIds> = sequence {
            val entries = storePageDto.pageData?.contentData
            entries?.forEach { contentData ->
                when (contentData.dkId) {
                    1 -> { // parse episodes
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreIdsPodcastEpisodes(
                                label = contentData.title,
                                itemsIds = ids
                            )
                        )
                    }
                    else -> { // parse podcasts
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreIdsPodcasts(
                                label = contentData.title,
                                itemsIds = ids
                            )
                        )
                    }
                }
            }
        }
        val artistData = StoreDataMultiRoom(
            id = storePageDto.pageData?.artist?.adamId.orEmpty(),
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.uber?.masterArt?.firstOrNull()?.toArtwork(),
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
        )

        return NetworkResponse.Success(artistData)
    }

    /**
     * getRoomPodcastDataAsync
     */
    fun getRoomPodcastDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreDataRoom> {
        // parse store page data
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val roomData = StoreDataRoom(
            id = storePageDto.pageData?.adamId.toString(),
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeIds = ids,
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
        )

        return NetworkResponse.Success(roomData)
    }

    /**
     * getMultiRoomDataAsync
     */
    fun getMultiRoomDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreDataMultiRoom> {
        val collectionSequence: Sequence<StoreIdsPodcasts> = sequence {
            val entries = storePageDto.pageData?.segments
            entries?.forEach { segmentData ->
                val ids = segmentData.adamIds.map { id -> id.toLong() }
                yield(
                    StoreIdsPodcasts(
                        label = segmentData.title,
                        url = segmentData.seeAllUrl?.url,
                        itemsIds = ids
                    )
                )
            }
        }

        val roomData = StoreDataMultiRoom(
            id = storePageDto.pageData?.adamId.toString(),
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = storePageDto.pageData?.uber?.masterArt?.lastOrNull()
                ?.toArtwork(),
            storeList = collectionSequence.toList(),
            lookup = getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup)
        )
        return NetworkResponse.Success(roomData)
    }

    /**
     * getPodcastDataAsync
     */
    override suspend fun getPodcastDataAsync(url: String, storeFront: String): NetworkResponse<StoreDataPodcast> =
        // get grouping data
        when (val networkResponse = getStoreDataApi(url, storeFront)) {
            is NetworkResponse.Success -> {
                val storePageDto: StorePageDto = networkResponse.body
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
                        val podcastData = StoreDataPodcast(
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
                            podcastsByArtist = StoreIdsPodcasts("", itemsIds = moreByArtist.toList()),
                            podcastsListenersAlsoFollow = StoreIdsPodcasts("", itemsIds = listenersAlsoBought.toList()),
                            episodes = podcastEntry.children.map { (key, episodeEntry) ->
                                StoreItemPodcastEpisode(
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

                        NetworkResponse.Success(podcastData)
                    }
                    ?: NetworkResponse.UnknownError(Exception())
            }
            is NetworkResponse.NetworkError ->
                networkResponse
            is NetworkResponse.ServerError ->
                NetworkResponse.ServerError(null, networkResponse.code)
            is NetworkResponse.UnknownError ->
                networkResponse
            else ->
                NetworkResponse.UnknownError(Exception())
        }

    /**
     * getStoreItemFromLookupResultItem
     */
    private fun getStoreItemFromLookupResultItem(item: LookupResultItem): StoreItem? =
        when (item.kind) {
            "podcast" -> {
                StoreItemPodcast(
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
                    genre = item.genres.firstOrNull()?.toGenre()
                )
            }
            "podcastEpisode" -> {
                StoreItemPodcastEpisode(
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
                )
            }
            else -> null
        }

    /**
     * getStoreLookupFromLookupResult
     */
    private fun getStoreLookupFromLookupResult(lockupResult: LockupResult?) : Map<Long, StoreItem> {
        val resultMap : HashMap<Long, StoreItem> = hashMapOf()
        lockupResult?.results
            ?.mapValues { it -> getStoreItemFromLookupResultItem(it.value) }
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
    override suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storeFront: String, storeDataWithLookup: StoreDataWithLookup): NetworkResponse<List<StoreItem>>
    {
        val newLookup: MutableMap<Long, StoreItem> = mutableMapOf()
        newLookup.putAll(storeDataWithLookup.lookup)
        // get current lookup ids
        val currentLookupIds: Set<Long> = newLookup.keys
        // get missing lookup ids for room
        val dataIds: List<Long> = lookupIds.subtract(currentLookupIds).toList()

        // retrieve missing lookups
        if (dataIds.isNotEmpty()) {
            when (val networkResponseLookupItems =
                getLookupDataAsync(dataIds, storeFront)) {
                is NetworkResponse.Success -> {
                    val lookupItems = networkResponseLookupItems.body
                    lookupItems.results
                        .mapValues { v -> getStoreItemFromLookupResultItem(v.value) }
                        .forEach {
                            it.value?.let { value -> newLookup.put(it.key, value) }
                        }
                }
                is NetworkResponse.NetworkError ->
                    return networkResponseLookupItems
                is NetworkResponse.ServerError ->
                    return NetworkResponse.ServerError(
                        null,
                        networkResponseLookupItems.code
                    )
                is NetworkResponse.UnknownError ->
                    return networkResponseLookupItems
                else ->
                    return NetworkResponse.UnknownError(Exception())
            }
        }

        val storeCollection: List<StoreItem> =
            lookupIds
                .filter { id -> newLookup.containsKey(id) }
                .mapNotNull { id -> newLookup.get(id) }

        return NetworkResponse.Success(storeCollection)
    }

    /**
     * getLookupDataAsync
     */
    override suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): NetworkResponse<LockupResult> =
        fetchNetworkData {
            httpClient.get<LockupResult> {
                url("https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup")
                parameter("version", 2)
                parameter("p", "lockup")
                parameter("caller", "DI6")
                parameter("id", lookupIds.joinToString(","))
                header("X-Apple-Store-Front", storeFront)
            }
        }


    /**
     * getStoreDataApi
     */
    override suspend fun getStoreDataApi(url: String, storeFront: String): NetworkResponse<StorePageDto> =
        fetchNetworkData {
            httpClient.get {
                url(url)
                header("X-Apple-Store-Front", storeFront)
            }
        }

    /**
     * Fetch data from network
     */
    private inline suspend fun <T: Any> fetchNetworkData(crossinline networkCall: suspend () -> T): NetworkResponse<T> {
        try {
            val data = networkCall.invoke()
            return NetworkResponse.Success(data)
        }
        catch (e: ResponseException) {
            return NetworkResponse.ServerError(null, e.response?.status?.value ?: 0)
        }
        catch (t: Throwable) {
            return NetworkResponse.UnknownError(t)
        }
    }
}