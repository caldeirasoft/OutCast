@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StorePage(
    val storeData: StoreData,
    val storeFront: String,
    val lookup: Map<Long, StoreItemArtwork> = mutableMapOf(),
    val timestamp: Instant,
    var fetchedAt: Instant = Clock.System.now(),
) {
    val label = storeData.label
    val description = storeData.description
    val url = storeData.url
    val artwork = storeData.artwork
    val storeIds: List<Long> = storeData.storeIds
    val storeList: MutableList<StoreCollection> = storeData.storeList
    val isMultiRoom: Boolean = storeData.isMultiRoom()
    val containsFeatured = storeData.storeList.filterIsInstance<StoreCollectionFeatured>().isNotEmpty()
}