package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork

fun Episode.getArtworkUrl(): String =
    StoreItemWithArtwork.artworkUrl(artwork, 200, 200)