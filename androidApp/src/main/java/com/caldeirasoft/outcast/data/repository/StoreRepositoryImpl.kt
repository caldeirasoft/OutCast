package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor (val httpClient:HttpClient) : StoreRepository {

    private val storeFrontCountries: Map<String, String> =
        mapOf("AL" to "143575",
            "DZ" to "143563",
            "AO" to "143564",
            "AI" to "143538",
            "AG" to "143540",
            "AR" to "143505",
            "AM" to "143524",
            "AU" to "143460",
            "AT" to "143445",
            "AZ" to "143568",
            "BS" to "143539",
            "BH" to "143559",
            "BB" to "143541",
            "BD" to "143490",
            "BY" to "143565",
            "BE" to "143446",
            "BZ" to "143555",
            "BJ" to "143576",
            "BM" to "143542",
            "BT" to "143577",
            "BO" to "143556",
            "BW" to "143525",
            "BR" to "143503",
            "VG" to "143543",
            "BN" to "143560",
            "BG" to "143526",
            "BF" to "143578",
            "KH" to "143579",
            "CA" to "143455",
            "CV" to "143580",
            "CI" to "143527",
            "KY" to "143544",
            "TD" to "143581",
            "CL" to "143483",
            "CN" to "143465",
            "CO" to "143501",
            "CG" to "143582",
            "CR" to "143495",
            "HR" to "143494",
            "CY" to "143557",
            "CZ" to "143489",
            "DK" to "143458",
            "DM" to "143545",
            "DO" to "143508",
            "EC" to "143509",
            "EG" to "143516",
            "SV" to "143506",
            "EE" to "143518",
            "FJ" to "143583",
            "FI" to "143447",
            "FR" to "143442",
            "GM" to "143584",
            "DE" to "143443",
            "GH" to "143573",
            "GR" to "143448",
            "GD" to "143546",
            "GT" to "143504",
            "GW" to "143585",
            "GY" to "143553",
            "HN" to "143510",
            "HK" to "143463",
            "HU" to "143482",
            "IS" to "143558",
            "IN" to "143467",
            "ID" to "143476",
            "IE" to "143449",
            "IL" to "143491",
            "IT" to "143450",
            "JM" to "143511",
            "JP" to "143462",
            "JO" to "143528",
            "KR" to "143466",
            "KZ" to "143517",
            "KE" to "143529",
            "KW" to "143493",
            "KG" to "143586",
            "LA" to "143587",
            "LV" to "143519",
            "LB" to "143497",
            "LR" to "143588",
            "LT" to "143520",
            "LI" to "143522",
            "LU" to "143451",
            "MO" to "143515",
            "MK" to "143530",
            "MG" to "143531",
            "MW" to "143589",
            "MY" to "143473",
            "MV" to "143488",
            "ML" to "143532",
            "MT" to "143521",
            "MR" to "143590",
            "MU" to "143533",
            "MX" to "143468",
            "FM" to "143591",
            "MD" to "143523",
            "MN" to "143592",
            "MS" to "143547",
            "MZ" to "143593",
            "NA" to "143594",
            "NP" to "143484",
            "NL" to "143452",
            "NZ" to "143461",
            "NI" to "143512",
            "NE" to "143534",
            "NG" to "143561",
            "NO" to "143457",
            "OM" to "143562",
            "PK" to "143477",
            "PW" to "143595",
            "PA" to "143485",
            "PG" to "143597",
            "PY" to "143513",
            "PE" to "143507",
            "PH" to "143474",
            "PL" to "143478",
            "PT" to "143453",
            "QA" to "143498",
            "RO" to "143487",
            "RU" to "143469",
            "ST" to "143598",
            "SA" to "143479",
            "SN" to "143535",
            "SC" to "143599",
            "SL" to "143600",
            "SG" to "143464",
            "SK" to "143496",
            "SI" to "143499",
            "SB" to "143601",
            "ZA" to "143472",
            "KP" to "143466",
            "ES" to "143454",
            "LK" to "143486",
            "KN" to "143548",
            "LC" to "143549",
            "VC" to "143550",
            "SR" to "143554",
            "SZ" to "143602",
            "SE" to "143456",
            "CH" to "143459",
            "TW" to "143470",
            "TJ" to "143603",
            "TZ" to "143572",
            "TH" to "143475",
            "TT" to "143551",
            "TN" to "143536",
            "TR" to "143480",
            "TM" to "143604",
            "TC" to "143552",
            "AE" to "143481",
            "UG" to "143537",
            "UA" to "143492",
            "GB" to "143444",
            "US" to "143441",
            "UY" to "143514",
            "UZ" to "143566",
            "VE" to "143502",
            "VN" to "143471",
            "YE" to "143571",
            "ZW" to "143605")

    /**
     * getPodcastDirectoryDataAsync
     */
    override suspend fun getDirectoryDataAsync(storeFront: String): NetworkResponse<StoreGroupingData> =
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
                                            yield(StoreCollectionPodcasts(
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
                                                        StoreRoom(
                                                            id = id,
                                                            label = artist?.name.orEmpty(),
                                                            url = artist?.url.orEmpty(),
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

                val dataGrouping = StoreGroupingData(
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
    fun getGroupingDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreGroupingData> {
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
                                StoreCollectionPodcasts(
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

        val dataGrouping = StoreGroupingData(
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
    fun getArtistPodcastDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreRoom> {
        // parse store page data
        val ids = storePageDto.pageData?.contentData?.first()?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val artistData = StoreRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
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
    fun getArtistProviderDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreMultiRoom> {
        val collectionSequence: Sequence<StoreCollection> = sequence {
            val entries = storePageDto.pageData?.contentData
            entries?.forEach { contentData ->
                when (contentData.dkId) {
                    1 -> { // parse episodes
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreCollectionEpisodes(
                                label = contentData.title,
                                itemsIds = ids
                            )
                        )
                    }
                    else -> { // parse podcasts
                        val ids = contentData.adamIds.map { id -> id.toLong() }
                        yield(
                            StoreCollectionPodcasts(
                                label = contentData.title,
                                itemsIds = ids
                            )
                        )
                    }
                }
            }
        }
        val artistData = StoreMultiRoom(
            id = storePageDto.pageData?.artist?.adamId?.toLong() ?: 0,
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
    fun getRoomPodcastDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreRoom> {
        // parse store page data
        val ids = storePageDto.pageData?.adamIds?.map { id -> id.toLong() } ?: emptyList()
        val roomData = StoreRoom(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
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
    fun getMultiRoomDataAsync(storePageDto: StorePageDto): NetworkResponse<StoreMultiRoom> {
        val collectionSequence: Sequence<StoreCollectionPodcasts> = sequence {
            val entries = storePageDto.pageData?.segments
            entries?.forEach { segmentData ->
                val ids = segmentData.adamIds.map { id -> id.toLong() }
                yield(
                    StoreCollectionPodcasts(
                        label = segmentData.title,
                        url = segmentData.seeAllUrl?.url,
                        itemsIds = ids
                    )
                )
            }
        }

        val roomData = StoreMultiRoom(
            id = storePageDto.pageData?.adamId?.toLong() ?: 0,
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
    override suspend fun getPodcastDataAsync(url: String, storeFront: String): NetworkResponse<StorePodcast> =
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
                            podcastsByArtist = StoreCollectionPodcasts("", itemsIds = moreByArtist.toList()),
                            podcastsListenersAlsoFollow = StoreCollectionPodcasts("", itemsIds = listenersAlsoBought.toList()),
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
                    genre = item.genres.firstOrNull()?.toGenre()
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