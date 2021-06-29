package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import kotlinx.serialization.Serializable


/**
 * Created by Edmond on 12/02/2018.
 */
@Serializable
class GenreDto (
    private val genreId: Int,
    val name: String = "",
    val url: String = "",
) {
    fun toStoreCategory(): StoreCategory =
        StoreCategory(
            id = genreId,
            name = name,
            url = url,
            storeFront = ""
        )
}