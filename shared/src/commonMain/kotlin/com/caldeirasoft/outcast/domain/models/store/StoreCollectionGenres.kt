package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.models.Genre
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionGenres(
    override val id: Long,
    var label: String,
    val genres: List<Genre>,
    override val storeFront: String,
) : StoreCollection