package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.enums.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePodcastSortOrderUseCase @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
) {
    suspend fun execute(feedUrl: String, sortOrder: SortOrder) =
        dataStoreRepository.updatePodcastSortOrder(feedUrl, sortOrder)
}