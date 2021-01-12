@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithCollections
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreGroupingData(
    val id: String,
    val label: String,
    override val storeFront: String,
    override val storeList: MutableList<StoreCollection> = mutableListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    val genres: StoreCollectionGenres? = null,
    val topCharts: List<StoreCollectionTopPodcasts> = emptyList(),
    override val timestamp: Instant
) : StorePage, StoreDataWithCollections