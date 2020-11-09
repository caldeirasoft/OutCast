@file:UseSerializers(InstantStringSerializer::class, LocalDateStringSerializer::class)

package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.serializers.InstantStringSerializer
import com.caldeirasoft.outcast.domain.serializers.LocalDateStringSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StorePageDto(
    val storePlatformData: StorePlatformDataResult? = null,
    val pageData: PageDataResult? = null)

@Serializable
class StorePlatformDataResult(
    var lockup: LockupResult? = null,
    @SerialName("product-dv") val producDv: LockupResult? = null,
)

@Serializable
class LockupResult (
    val results: HashMap<Long, LookupResultItem> = hashMapOf()
)

@Serializable
class PageDataResult(
    val componentName: String = "",
    val id: String = "",
    val categoryList: CategoryListResult? = null,
    val pageType: String? = null,
    val pageTitle: String? = null,
    val contentId: String? = null,
    val url: String? = null,
    val fcStructure: PageDataStructureResult? = null,
    val segments: List<SegmentResult> = arrayListOf(),
    val adamIds: List<String> = arrayListOf(),
    val adamId: Int? = null,
    val contentData: List<ContentDataResult> = arrayListOf(),
    val mt: Int? = null,
    val genreId: Int = 0,
    val isNewsstand: Boolean? = null,
    val uber: UberListResult? = null,
    val description: String? = null,
    val websiteUrl: String? = null,
    val moreByArtist: List<String> = arrayListOf(),
    val listenersAlsoBought: List<String> = arrayListOf(),
    val topPodcastsInGenre: List<String> = arrayListOf(),
    val popularityMap: PopularityMapResult? = null,
    val artist: ArtistResult? = null,
    val metricsBase: MetricsBaseResult? = null,
)

@Serializable
class CategoryListResult(
    val name: String = "",
    val url: String? = null,
    val genreId: Int = 0,
    val kind: String? = null,
    val children: List<GenreDto> = arrayListOf()
)

@Serializable
class PageDataStructureResult(
    val version: Int = 0,
    val model: PageDataModelResult? = null)

@Serializable
class PageDataModelResult(
    val fcKind: Int = 0,
    val name: String = "",
    val token: String = "",
    val doNotFilter: Boolean? = null,
    val designLabel: String? = null,
    val type: String = "",
    val sticky: Boolean? = null,
    val adamId: Long = 0,
    val children: List<PageDataModelResult> = arrayListOf(),
    val artwork: ArtworkDto? = null,
    val link: Link = Link(),
    val links: List<Link> = arrayListOf(),
    val description: String? = null,
    val seeAllUrl: String? = null,
    val sort: Int? = null,
    val rows: Int? = null,
    val showOrdinals: Boolean? = null,
    val content: List<ContentResult> = arrayListOf())

@Serializable
class Link(
    val type: String = "",
    val label: String = "",
    val contentId: Long = 0,
    val url: String = "",
    val kindIds: List<Int> = arrayListOf())

@Serializable
class ContentResult(
    val type: String = "",
    val contentId: Long = 0,
    val kindIds: List<Int> = arrayListOf(),
    val target: String = "")

@Serializable
class ArtistResult(
    val artistId: String = "",
    val adamId: String = "",
    val artistType: String = "",
    val name: String = "",
    val mediaType: String = "",
    val url: String? = null,
    val kind: String? = null,
    val editorialArtwork: EditorialArtworkArtistResult? = null
)

@Serializable
class SegmentResult(
    val adamId: String = "",
    val title: String = "",
    val adamIds: List<Int> = arrayListOf(),
    val seeAllUrl: SeeAllUrlResult? = null,
    val fcKind: String? = null,
)

@Serializable
class ContentDataResult(
    val title: String = "",
    val adamIds: List<String> = arrayListOf(),
    val dkId: Int? = null,
    val dkEternalId: String? = null,
)

@Serializable
class SeeAllUrlResult(
    val label: String = "",
    val url: String = "",
)

@Serializable
class PopularityMapResult(
    val podcastEpisode: Map<String, Double> = hashMapOf(),
)

@Serializable
class UberResult(
    val name: String? = null,
    val backgroundColor: String? = null,
    val titleTextColor: String? = null,
    val primaryTextColor: String? = null,
    val headerTextColor: String? = null,
    val primaryTextColorOnBlack: String? = null,
    val titleTextColorOnBlack: String? = null,
    val description: String? = null,
    val masterArt: ArtworkDto? = null,
)

@Serializable
class UberListResult(
    val name: String? = null,
    val backgroundColor: String? = null,
    val titleTextColor: String? = null,
    val primaryTextColor: String? = null,
    val headerTextColor: String? = null,
    val primaryTextColorOnBlack: String? = null,
    val titleTextColorOnBlack: String? = null,
    val description: String? = null,
    val masterArt: List<ArtworkDto> = arrayListOf()
)

@Serializable
class EditorialArtworkResult(
    val storeFlowcase: ArtworkDto? = null
)
@Serializable
class EditorialArtworkArtistResult(
    val storeFlowcase: List<ArtworkDto> = arrayListOf()
)

@Serializable
class LookupResultItem(
    val artwork: ArtworkDto? = null,
    val artistName: String? = null,
    val url: String? = null,
    val shortUrl: String? = null,
    val hasPrimaryArtist: Boolean? = null,
    val genreNames: List<String> = arrayListOf(),
    val collection: Map<String, LookupResultItem> = hashMapOf(),
    val trackCount: Int? = null,
    val children: Map<String, LookupResultItem> = hashMapOf(),
    val nameSortValue: String? = null,
    val id: String? = null,
    val releaseDate: LocalDate? = null,
    val userRating: UserRatingResult? = null,
    val contentRatingsBySystem: ContentRatingBySystemResult? = null,
    val feedUrl: String? = null,
    val name: String? = null,
    val artistUrl: String? = null,
    val editorialArtwork: EditorialArtworkResult? = null,
    val kind: String = "",
    val copyright: String? = null,
    val artistId: String? = null,
    val genres: List<GenreDto> = arrayListOf(),
    val releaseDateTime: Instant? = null,
    val description: DescriptionResult? = null,
    val offers: List<OfferResult> = arrayListOf(),
    val podcastType: String? = null,
    val uber: UberResult? = null,
    val podcastWebsiteUrl: String? = null,
    val collectionId: String? = null,
    val collectionName: String? = null,
    val podcastEpisodeNumber: Int? = null,
    val podcastEpisodeSeason: Int? = null,
    val podcastEpisodeType: String? = null,
    val podcastEpisodeGuid: String? = null,
    val podcastEpisodeITunesTitle: String? = null,
    val podcastEpisodeWebsiteUrl: String? = null,
)

@Serializable
class UserRatingResult(
    val value: Double = 0.0,
    val ratingCount: Int = 0,
    val ariaLabelForRating: String? = null,
    val ratingCountList: List<Int> = arrayListOf(),
)

@Serializable
class ContentRatingBySystemResult(
    val riaa: RiaaResult = RiaaResult()
)

@Serializable
class RiaaResult(
    val name: String = "",
    val value: Int = 0,
    val rank: Int = 0,
)

@Serializable
class DescriptionResult(
    val standard: String = "",
    val short: String = "",
)

@Serializable
class OfferResult(
    val download: DownloadResult? = null,
    val assets: List<AssetResult> = arrayListOf(),
)

@Serializable
class DownloadResult(
    val url: String = "",
    val type: String = "",
)

@Serializable
class AssetResult(
    val fileExtension: String = "",
    val flavor: String? = null,
    val isClosedCaption: Boolean? = null,
    val duration: Int? = null,
)

@Serializable
class MetricsBaseResult(
    val pageType: String = "",
    val pageId: String? = null,
    val pageDetails: String? = null,
    val storeFrontHeader: String? = null,
    val language: String? = null,
    val storeFront: String? = null,
)