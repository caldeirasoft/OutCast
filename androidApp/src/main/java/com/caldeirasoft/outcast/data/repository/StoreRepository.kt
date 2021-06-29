package com.caldeirasoft.outcast.data.repository

import android.content.Context
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.domain.dto.GenreResult
import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.models.store.StoreGenre.Companion.toStoreGenre
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class StoreRepository @Inject constructor(
    val itunesAPI: ItunesAPI,
    val context: Context,
    val json: Json,
    mainDispatcher: CoroutineDispatcher,
) {

    companion object {
        const val DEFAULT_GENRE = 26
        const val DIRECTORY_CACHE_KEY = "directory"
        const val TOP_CHARTS_CACHE_KEY = "charts"
        const val GENRE_URL = "https://podcasts.apple.com/genre/id{genre}"
        const val SEARCH_URL = "https://search.itunes.apple.com/WebObjects/MZStore.woa/wa/search"
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(
        url: String,
        storeFront: String,
    ): StoreData {
        val storeResponse = itunesAPI.storeData(storeFront = storeFront, url = url)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)
        return getStoreData(storePageDto)
    }

    /**
     * getSearchResultsAsync
     */
    suspend fun getSearchResultsAsync(
        term: String,
        storeFront: String,
    ): StoreData {
        val storeResponse = itunesAPI.searchData(storeFront = storeFront, term = term)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)
        return getSearchDataAsync(storePageDto)
    }

    /**
     * getSearchResultsAsync
     */
    suspend fun getSearchTermHintsAsync(
        term: String,
        storeFront: String,
    ): List<String> {
        val storeResponse = itunesAPI.searchHints(storeFront = storeFront, term = term)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val searchHintResult = storeResponse.body() ?: throw HttpException(storeResponse)
        return searchHintResult.map { it.term }
    }

    /**
     * getStoreData
     */
    private fun getStoreData(storePageDto: StorePageDto): StoreData {
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
            "segmented_page" -> {
                getTopChartsDataAsync(storePageDto)
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
    private fun getGroupingDataAsync(storePageDto: StorePageDto): StoreData {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val lockupResult = storePageDto.storePlatformData?.lockup?.results ?: emptyMap()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val unAvailableContentIds = storePageDto.pageData?.unAvailableContentIds ?: emptyMap()
        val storeLookup =
            getStoreLookupFromLookupResult(storePageDto.storePlatformData?.lockup, storeFront)
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.fcStructure?.model?.children
                ?.first { element -> element.token == "allPodcasts" }?.children
                ?.first()?.children
            entries?.forEach { element ->
                when (element.fcKind) {
                    258 -> { // parse header collection
                        val sequence: Sequence<StoreItemArtwork> = sequence {
                            element.children.forEach { elementChild ->
                                when (elementChild.link.type) {
                                    "content" -> {
                                        val id = elementChild.link.contentId
                                        if (lockupResult.containsKey(id)) {
                                            lockupResult[id]?.let {
                                                getStoreItemFromLookupResultItem(it, storeFront)
                                                    ?.apply {
                                                        featuredArtwork =
                                                            elementChild.artwork?.toArtwork()
                                                        yield(this)
                                                    }
                                            }
                                        }
                                    }
                                    "link" -> {
                                        yield(
                                            StoreData(
                                                id = elementChild.adamId,
                                                label = elementChild.link.label,
                                                url = elementChild.link.url,
                                                artwork = elementChild.artwork?.toArtwork(),
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
                            val ids = elementChild.content
                                .map { content -> content.contentId }
                                .filter { !unAvailableContentIds.containsValue(it) }
                            when (elementChild.type) {
                                "popularity" -> { // top podcasts // top episodes
                                    /*yield(
                                        StoreCollectionItems(
                                            id = elementChild.adamId,
                                            label = elementChild.name,
                                            url = elementChild.seeAllUrl,
                                            itemsIds = ids,
                                            storeFront = storeFront,
                                            sortByPopularity = true
                                        )
                                    )*/
                                }
                                "normal" -> {
                                    when (elementChild.content.first().kindIds.first()) {
                                        4, 15 -> // podcast, episodes
                                            yield(
                                                StoreCollectionItems(
                                                    id = elementChild.adamId,
                                                    label = elementChild.name,
                                                    url = elementChild.seeAllUrl,
                                                    itemsIds = ids,
                                                    storeFront = storeFront,
                                                )
                                            )
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                    261 -> { // parse rooms collections / providers collections
                        val roomSequence: Sequence<StoreItemArtwork> = sequence {
                            element.children.forEach { elementChild ->
                                when (elementChild.link.type) {
                                    "content" -> {
                                        val id = elementChild.link.contentId
                                        if (lockupResult.containsKey(id)) {
                                            lockupResult[id]?.let {
                                                when (it.kind) {
                                                    "podcast", "episode" ->
                                                        getStoreItemFromLookupResultItem(
                                                            it,
                                                            storeFront
                                                        )
                                                            ?.apply {
                                                                featuredArtwork =
                                                                    elementChild.artwork?.toArtwork()
                                                                yield(this)
                                                            }
                                                    else -> // artist, room
                                                        yield(
                                                            StoreData(
                                                                id = id,
                                                                label = it.name.orEmpty(),
                                                                url = it.url.orEmpty(),
                                                                storeFront = storeFront,
                                                                artwork = elementChild.artwork!!.toArtwork(),
                                                            )
                                                        )
                                                }
                                            }
                                        }
                                    }
                                    "link" -> {
                                        yield(
                                            StoreData(
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
                                StoreCollectionData(
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

        return StoreData(
            id = storePageDto.pageData?.contentId?.toLong() ?: 0L,
            label = storePageDto.pageData?.categoryList?.name.orEmpty(),
            storeFront = storeFront,
            storeList = collectionSequence.toMutableList(),
            lookup = storeLookup,
            timestamp = timestamp
        )
    }


    /**
     * getArtistPodcastDataAsync
     */
    private fun getArtistPodcastDataAsync(storePageDto: StorePageDto): StoreData {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val ids = storePageDto.pageData?.contentData?.first()?.adamIds?.map { id -> id.toLong() }
            ?: emptyList()

        return StoreData(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = storePageDto.pageData?.artist
                ?.editorialArtwork?.storeFlowcase
                ?.firstOrNull()
                ?.toArtwork(),
            storeFront = storeFront,
            storeIds = ids,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = storeFront
            )
        )
    }

    /**
     * getArtistProviderDataAsync
     */
    private fun getArtistProviderDataAsync(storePageDto: StorePageDto): StoreData {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.contentData
            entries?.forEach { contentData ->
                val ids = contentData.adamIds.map { id -> id.toLong() }
                val dkId = contentData.dkId
                val chunkId = contentData.chunkId
                when {
                    dkId != null || chunkId.isNullOrEmpty() -> {
                        // popular episodes
                        yield(
                            StoreCollectionItems(
                                id = dkId?.toLong() ?: 0L,
                                label = contentData.title,
                                itemsIds = ids,
                                storeFront = storeFront,
                                sortByPopularity = true,
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
        val uberArtwork = storePageDto.pageData?.uber?.toArtwork()
        val editorialArtwork =
            storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork()

        return StoreData(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.artist?.name.orEmpty(),
            artwork = editorialArtwork ?: uberArtwork,
            storeFront = storeFront,
            storeList = collectionSequence.toMutableList(),
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = storeFront)
        )
    }

    /**
     * getRoomPodcastDataAsync
     */
    private fun getRoomPodcastDataAsync(storePageDto: StorePageDto): StoreData {
        // parse store page data
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val uberArtwork = storePageDto.pageData?.uber?.toArtwork()
        val editorialArtwork =
            storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork()

        return StoreData(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = editorialArtwork ?: uberArtwork,
            storeFront = storeFront,
            storeIds = ids,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = storeFront)
        )
    }

    /**
     * getMultiRoomDataAsync
     */
    private fun getMultiRoomDataAsync(storePageDto: StorePageDto): StoreData {
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
        val uberArtwork = storePageDto.pageData?.uber?.toArtwork()
        val editorialArtwork =
            storePageDto.pageData?.artist?.editorialArtwork?.storeFlowcase?.firstOrNull()
                ?.toArtwork()

        return StoreData(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = storePageDto.pageData?.description,
            artwork = editorialArtwork ?: uberArtwork,
            storeFront = storeFront,
            storeList = collectionSequence.toMutableList(),
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = storeFront)
        )
    }

    /**
     * getTopChartsAsync
     */
    private fun getTopChartsDataAsync(storePageDto: StorePageDto): StoreData
    {
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData
                ?.segmentedControl?.segments
                ?.firstOrNull()
                ?.pageData
                ?.topCharts
            entries?.forEach { element ->
                yield(
                    StoreCollectionItems(
                        id = element.id,
                        label = element.title,
                        itemsIds = element.adamIds,
                        storeFront = "",
                        sortByPopularity = true
                    )
                )
            }
        }

        val storeCategories: List<StoreCategory> =
            storePageDto.pageData
                ?.segmentedControl?.segments
                ?.firstOrNull()
                ?.pageData
                ?.categoryList
                ?.let { categoryList ->
                    listOf(
                        StoreCategory(
                            id = categoryList.genreId,
                            name = categoryList.parentCategoryLabel.orEmpty(),
                            storeFront = "",
                            url = categoryList.url.orEmpty()
                        )
                    ) + (categoryList.children.map { child -> child.toStoreCategory() })
                }
                ?: emptyList()

        return StoreData(
            id = 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = null,
            artwork = null,
            storeFront = "",
            storeList = collectionSequence.toList(),
            storeCategories = storeCategories,
            sortByPopularity = true,
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = "")
        )
    }

    /**
     * getSearchDataAsync
     */
    private fun getSearchDataAsync(storePageDto: StorePageDto): StoreData
    {
        val storeFront = storePageDto.pageData?.metricsBase?.storeFrontHeader.orEmpty()
        val timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST

        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData
                ?.bubbles
                ?.sortedBy { it.name }
            entries?.forEach { element ->
                when (element.name) {
                    "podcast" ->
                        yield(
                            StoreCollectionItems(
                                id = 0,
                                label = element.name,
                                itemsIds = element.results.map { it.id },
                                storeFront = "",
                            )
                        )
                    "podcastEpisode" ->
                        yield(
                            StoreCollectionEpisodes(
                                id = 0,
                                label = element.name,
                                itemsIds = element.results.map { it.id },
                                storeFront = "",
                            )
                        )
                }
            }
        }

        return StoreData(
            id = 0,
            label = storePageDto.pageData?.pageTitle.orEmpty(),
            description = null,
            artwork = null,
            storeFront = "",
            storeList = collectionSequence.toList(),
            timestamp = timestamp,
            lookup = getStoreLookupFromLookupResult(
                lockupResult = storePageDto.storePlatformData?.lockup,
                storeFront = "")
        )
    }

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcast {
        // get grouping data
        val storeResponse = itunesAPI.storeData(storeFront = storeFront, url = url)
        if (storeResponse.isSuccessful.not())
            throw HttpException(storeResponse)
        val storePageDto = storeResponse.body() ?: throw HttpException(storeResponse)

        // get missing lookup ids
        val moreByArtist = storePageDto.pageData?.moreByArtist?.map { it.toLong() }
        val listenersAlsoFollow = storePageDto.pageData?.listenersAlsoBought?.map { it.toLong() }
        val topPodcastsInGenre = storePageDto.pageData?.topPodcastsInGenre?.map { it.toLong() }

        // parse podcast
        return storePageDto.storePlatformData?.producDv?.results?.entries?.firstOrNull()
            ?.let { (_, podcastEntry) ->
                val podcastData =
                    getStoreItemFromLookupResultItem(podcastEntry, storeFront) as StorePodcast
                podcastData.also {
                    it.moreByArtist = moreByArtist
                    it.listenersAlsoBought = listenersAlsoFollow
                    it.topPodcastsInGenre = topPodcastsInGenre
                    /*
                    it.episodes = podcastEntry.children.map { (key, episodeEntry) ->
                        Episode(
                            guid = episodeEntry.podcastEpisodeGuid.orEmpty(),
                            name = episodeEntry.name.orEmpty(),
                            url = episodeEntry.url.orEmpty(),
                            podcastId = episodeEntry.collectionId?.toLong() ?: 0,
                            podcastName = episodeEntry.collectionName.orEmpty(),
                            artistName = episodeEntry.artistName.orEmpty(),
                            artistId = episodeEntry.artistId?.toLong(),
                            description = episodeEntry.description?.standard,
                            feedUrl = episodeEntry.feedUrl.orEmpty(),
                            releaseDateTime = episodeEntry.releaseDateTime
                                ?: Clock.System.now(),
                            artworkUrl = podcastEntry.artwork?.toArtwork()?.getArtworkPodcast()
                                .orEmpty(),
                            isExplicit = episodeEntry.contentRatingsBySystem?.riaa?.rank == 2,
                            mediaUrl = episodeEntry.offers.firstOrNull()?.download?.url.orEmpty(),
                            mediaType = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.fileExtension.orEmpty(),
                            duration = episodeEntry.offers.firstOrNull()?.assets?.firstOrNull()?.duration
                                ?: 0,
                            podcastEpisodeNumber = episodeEntry.podcastEpisodeNumber,
                            podcastEpisodeSeason = episodeEntry.podcastEpisodeSeason,
                            podcastEpisodeType = episodeEntry.podcastEpisodeType.orEmpty(),
                            podcastEpisodeWebsiteUrl = episodeEntry.podcastEpisodeWebsiteUrl,
                            updatedAt = Clock.System.now(),
                            isPlayed = false,
                            playedAt = Instant.DISTANT_PAST,
                            isFavorite = false,
                            playbackPosition = null
                        )
                    }.sortedByDescending { it.releaseDateTime }
                     */
                }

                //timestamp = storePageDto.properties?.timestamp ?: Instant.DISTANT_PAST
            }
            ?: throw Exception("missing podcast entry")
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

    /**
     * getStoreItemFromLookupResultItem
     */
    private fun getStoreItemFromLookupResultItem(
        item: LookupResultItem,
        storeFront: String,
    ): StoreItemArtwork? =
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
                    editorialArtwork = item.editorialArtwork?.showPageTall?.toArtwork(),
                    trackCount = item.trackCount ?: 0,
                    podcastWebsiteUrl = item.podcastWebsiteUrl,
                    copyright = item.copyright,
                    isExplicit = item.contentRatingsBySystem?.riaa?.rank == 2,
                    userRating = item.userRating?.value?.toFloat() ?: 0f,
                    genre = item.genres.first().toStoreCategory(),
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
                    feedUrl = item.feedUrl.orEmpty(),
                    guid = item.podcastEpisodeGuid.orEmpty(),
                    releaseDateTime = item.releaseDateTime ?: Clock.System.now(),
                    artwork = item.artwork?.toArtwork(),
                    mediaUrl = item.offers.firstOrNull()?.download?.url.orEmpty(),
                    mediaType = item.offers.firstOrNull()?.assets?.firstOrNull()?.fileExtension.orEmpty(),
                    duration = item.offers.firstOrNull()?.assets?.firstOrNull()?.duration ?: 0,
                    podcastEpisodeNumber = item.podcastEpisodeNumber,
                    podcastEpisodeSeason = item.podcastEpisodeSeason,
                    podcastEpisodeType = item.podcastEpisodeType.orEmpty(),
                    podcastEpisodeWebsiteUrl = item.podcastEpisodeWebsiteUrl,
                    storeFront = storeFront,
                    isExplicit = item.contentRatingsBySystem?.riaa?.rank == 2,
                    isComplete = false,
                    storePodcast = if (item.collection.values.isNotEmpty()) {
                        StorePodcast(
                            id = item.collectionId?.toLong() ?: 0,
                            name = item.collectionName.orEmpty(),
                            url = item.url.orEmpty(),
                            artistName = item.artistName.orEmpty(),
                            artistId = item.artistId?.toLong(),
                            artistUrl = item.artistUrl,
                            description = "",
                            feedUrl = item.feedUrl.orEmpty(),
                            releaseDate = item.releaseDateTime ?: Clock.System.now(),
                            releaseDateTime = item.releaseDateTime ?: Clock.System.now(),
                            artwork = item.artwork?.toArtwork(),
                            editorialArtwork = item.editorialArtwork?.showPageTall?.toArtwork(),
                            trackCount = item.trackCount ?: 0,
                            podcastWebsiteUrl = item.podcastWebsiteUrl,
                            copyright = item.copyright,
                            isExplicit = item.contentRatingsBySystem?.riaa?.rank == 2,
                            userRating = item.userRating?.value?.toFloat() ?: 0f,
                            genre = item.genres.first().toStoreCategory(),
                            storeFront = storeFront
                        )
                    } else {
                        requireNotNull(
                            getStoreItemFromLookupResultItem(
                                item.collection.values.first(),
                                storeFront
                            ) as StorePodcast
                        )
                    }
                )
            }
            else -> null
        }

    /**
     * getStoreLookupFromLookupResult
     */
    private fun getStoreLookupFromLookupResult(
        lockupResult: LockupResult?,
        storeFront: String,
    ): Map<Long, StoreItemArtwork> {
        val resultMap: HashMap<Long, StoreItemArtwork> = hashMapOf()
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
        storeData: StoreData?,
    ): List<StoreItemArtwork> {
        val newLookup: MutableMap<Long, StoreItemArtwork> = mutableMapOf()
        storeData?.lookup?.let { map ->
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
            .mapNotNull { id -> newLookup[id] }
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
            root = rootEntry.toStoreGenre(),
            genres = rootEntry.subgenres.values.map { it.toStoreGenre() }
        )
    }
}