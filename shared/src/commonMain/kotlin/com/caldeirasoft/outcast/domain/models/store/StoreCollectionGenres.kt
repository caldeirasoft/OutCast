package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.models.Genre
import kotlinx.serialization.Serializable

@Serializable
data class StoreCollectionGenres(
    var label: String,
    val genres: List<Genre>,
    val storeFront: String,
)