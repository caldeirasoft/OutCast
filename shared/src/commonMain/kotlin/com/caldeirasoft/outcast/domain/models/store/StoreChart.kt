package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreChart(
    val id: Long,
    val label: String,
    val genreId: Int?,
    val storeFront: String,
)