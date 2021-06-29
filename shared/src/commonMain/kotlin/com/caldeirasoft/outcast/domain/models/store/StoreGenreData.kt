package com.caldeirasoft.outcast.domain.models.store

class StoreGenreData(
    val root: StoreGenre,
    val genres: List<StoreGenre> = mutableListOf(),
)