@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithCollections
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreMultiRoomPage(
    var id: Long,
    override var label: String,
    override val url: String = "",
    override var description: String? = null,
    override var artwork: Artwork? = null,
    override val storeFront: String,
    override val storeList: List<StoreCollection> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StoreFeaturedPage, StoreDataWithCollections