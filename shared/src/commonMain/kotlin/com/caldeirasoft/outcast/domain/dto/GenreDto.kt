package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import kotlinx.serialization.Serializable


/**
 * Created by Edmond on 12/02/2018.
 */
@Serializable
class GenreDto (
    var genreId: Int,
    var name: String = "",
    var url: String = "") {

    fun toGenre() =
        Genre(
            id = genreId,
            name = name,
            url = url
        )

    fun toStoreGenre(storeFront: String) =
        StoreGenre(
            id = genreId,
            name = name,
            url = url,
            storeFront = storeFront
        )
}