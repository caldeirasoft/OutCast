@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreTopCharts(
    var id: Long,
    var label: String,
    override val storeFront: String,
    val topPodcastsChart: StoreChart,
    val topEpisodesChart: StoreChart,
    override val timestamp: Instant,
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StorePage {
}