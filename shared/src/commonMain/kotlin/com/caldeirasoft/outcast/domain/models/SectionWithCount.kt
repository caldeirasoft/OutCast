package com.caldeirasoft.outcast.domain.models

import kotlin.Long
import kotlin.String

data class SectionWithCount(
  val podcastId: Long,
  val queueCount: Long?,
  val inboxCount: Long?,
  val favoritesCount: Long?,
  val historyCount: Long?
)