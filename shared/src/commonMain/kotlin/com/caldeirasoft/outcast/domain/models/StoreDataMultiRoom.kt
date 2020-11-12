package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreDataMultiRoom(
    var id: String,
    var label: String,
    var description: String? = null,
    var artwork: Artwork? = null,
    val storeList: List<StoreCollection> = arrayListOf(),
    override var lookup: Map<Long, StoreItem> = mutableMapOf()
) : StoreDataWithLookup