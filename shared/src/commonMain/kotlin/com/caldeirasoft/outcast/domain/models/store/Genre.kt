package com.caldeirasoft.outcast.domain.models.store

import kotlinx.serialization.Serializable

@Serializable
data class Genre(val id: Int,
                 val name: String,
                 val url: String)