package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithCollections
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.serialization.Serializable

@Serializable
class StoreGroupingData(
    val id: String,
    val label: String,
    override val storeList: List<StoreCollection> = mutableListOf(),
    override var lookup: Map<Long, StoreItem> = mutableMapOf()
) : StoreDataWithLookup, StoreDataWithCollections