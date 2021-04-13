@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.common.Constants
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
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
    var storeIds: List<Long> = arrayListOf(),
    val storeList: MutableList<StoreCollection> = mutableListOf(),
) : StoreItemArtwork {
    override var featuredArtwork: Artwork? = artwork
    override var editorialArtwork: Artwork? = null

    val isMultiRoom: Boolean = storeList.isNotEmpty()

    override fun getArtworkUrl(): String =
        StoreItemArtwork.artworkUrl(artwork, 400, 196, crop = "fa")

    fun getPage(): StorePage =
        StorePage(
            storeData = this.copy(artwork = null),
            storeFront = this.storeFront,
            timestamp = Clock.System.now(),
        )

    companion object {
        val Default = StoreData(
            id = 0L,
            label = "",
            genreId = Constants.DEFAULT_GENRE,
            storeFront = "",
        )
    }
}