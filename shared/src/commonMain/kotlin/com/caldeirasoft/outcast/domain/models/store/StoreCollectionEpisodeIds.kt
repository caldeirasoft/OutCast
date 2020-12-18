package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionEpisodeIds(
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    val itemsIds: List<Long>) : StoreCollection