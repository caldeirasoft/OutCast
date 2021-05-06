@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.common.Constants
import com.caldeirasoft.outcast.domain.common.Constants.Companion.GENRE_URL
import com.caldeirasoft.outcast.domain.common.Constants.Companion.TOP_CHARTS_URL
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StoreData(
    override var id: Long,
    val label: String,
    val url: String = "",
    val genreId: Int? = null,
    val description: String? = null,
    override val artwork: Artwork? = null,
    override val storeFront: String,
    var storeIds: List<Long> = emptyList(),
    val storeList: List<StoreCollection> = emptyList(),
    val sortByPopularity: Boolean = false,
    val storeCategories: List<StoreCategory> = emptyList(),
    val lookup: Map<Long, StoreItemArtwork> = mutableMapOf(),
    val timestamp: Instant = Instant.DISTANT_PAST,
    var fetchedAt: Instant = Clock.System.now(),
) : StoreItemArtwork {
    override var featuredArtwork: Artwork? = artwork
    override var editorialArtwork: Artwork? = null

    val isMultiRoom: Boolean
        get() = storeList.isNotEmpty()

    override fun getArtworkUrl(): String =
        StoreItemArtwork.artworkUrl(artwork, 400, 196, crop = "fa")

    val containsFeatured: Boolean
        get() = storeList.filterIsInstance<StoreCollectionFeatured>().isNotEmpty()

    companion object {
        val Default = StoreData(
            id = 0L,
            label = "",
            genreId = Constants.DEFAULT_GENRE,
            storeFront = "",
        )

        val TopCharts = StoreData(
            id = 0L,
            label = "",
            url = TOP_CHARTS_URL,
            storeFront = "",
        )

        fun Genre.toStoreData() = StoreData(
            id = id.toLong(),
            label = name,
            url = url,
            genreId = id,
            storeFront = "",
        )

        fun Category.toStoreData() = StoreData(
            id = id.toLong(),
            label = text,
            url = GENRE_URL.replace("{genre}", id.toString()),
            genreId = id,
            storeFront = "",
        )
    }
}