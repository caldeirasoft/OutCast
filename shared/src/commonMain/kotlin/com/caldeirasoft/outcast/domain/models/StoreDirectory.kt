package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.*
import kotlinx.serialization.Serializable

@Serializable
class StoreDirectory(
    val id: String,
    val label: String,
    override val storeFront: String,
    override val storeList: List<StoreCollection>,
    val topPodcasts: StoreCollectionPodcasts,
    val topEpisodes: StoreCollectionEpisodes,
    val categories: List<Genre> = emptyList(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StoreDataWithCollections