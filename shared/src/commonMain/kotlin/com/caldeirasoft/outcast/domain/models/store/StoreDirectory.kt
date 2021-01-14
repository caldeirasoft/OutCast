@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreDirectory(
    val id: String,
    val label: String,
    override val storeFront: String,
    override val storeList: List<StoreCollection>,
    val genres: List<StoreGenre>?,
    val topPodcastsChart: StoreChart,
    val topEpisodesChart: StoreChart,
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant
) : StorePage, StoreDataWithCollections