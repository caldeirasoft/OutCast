package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionGenres(
    var label: String,
    val genres: List<StoreGenre>,
    override val storeFront: String,
) : StoreCollection