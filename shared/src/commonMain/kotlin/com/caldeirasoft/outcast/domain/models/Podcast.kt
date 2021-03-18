package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork

fun Podcast.getArtworkUrl(): String =
    StoreItemWithArtwork.artworkUrl(artwork, 200, 200)