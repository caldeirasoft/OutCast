package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionEpisodes(
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    val itemsIds: List<Long> = emptyList(),
    var items: List<StoreEpisode> = mutableListOf(),
) : StoreCollection