package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork

fun Podcast.getArtworkUrl(): String =
    StoreItemArtwork.artworkUrl(artwork, 200, 200)

