package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.serialization.Serializable


/**
 * Created by Edmond on 12/02/2018.
 */
@Serializable
class GenreDto (
    val genreId: Int,
    val name: String = "",
    val url: String = "",
) {
    val category: Category?
        get() = Category.fromId(id = genreId)
}