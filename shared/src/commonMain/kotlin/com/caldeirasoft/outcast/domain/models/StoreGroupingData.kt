package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.*
import kotlinx.serialization.Serializable

@Serializable
class StoreGroupingData(
    val id: String,
    val label: String,
    override val storeFront: String,
    override val storeList: List<StoreCollection> = mutableListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StoreDataWithCollections