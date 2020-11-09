package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreDataGrouping(
    val id: String,
    val label: String,
    val storeList: List<StoreCollection> = mutableListOf(),
    override var lookup: Map<Long, StoreItem> = mutableMapOf()
) : StoreDataWithLookup