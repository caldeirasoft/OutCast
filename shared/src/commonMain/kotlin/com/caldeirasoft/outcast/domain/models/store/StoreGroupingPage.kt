@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreGroupingPage(
    val storeData: StoreGroupingData,
    override val storeFront: String,
    override val lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StorePageWithCollection {
    val genres = storeData.genres
    override val storeList: MutableList<StoreCollection>
        get() = storeData.storeList
}