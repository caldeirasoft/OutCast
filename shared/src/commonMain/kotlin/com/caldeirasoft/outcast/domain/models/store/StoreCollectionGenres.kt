package com.caldeirasoft.outcast.domain.models.store

import kotlinx.serialization.Serializable

@Serializable
data class StoreCollectionGenres(
    var label: String,
    val genres: List<StoreGenre>,
    val storeFront: String,
)