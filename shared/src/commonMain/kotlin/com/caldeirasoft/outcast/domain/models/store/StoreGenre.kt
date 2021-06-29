package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.dto.GenreResult

class StoreGenre(
    val id: Int,
    var name: String = "",
    val storeFront: String = "",
    val url: String = "",
) {
    companion object {
        fun GenreResult.toStoreGenre(): StoreGenre =
            StoreGenre(
                id = id,
                name = name,
                url = url,
                storeFront = ""
            )
    }
}