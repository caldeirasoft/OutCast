package com.caldeirasoft.outcast.ui.util

import kotlinx.serialization.Serializable

@Serializable
data class ListState(
    val index: Int = 0,
    val offset: Int = 0,
)