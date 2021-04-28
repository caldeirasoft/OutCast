package com.caldeirasoft.outcast.ui.screen.store.storedata

import com.caldeirasoft.outcast.data.common.Constants.Companion.DEFAULT_GENRE
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class StoreDataState(
    val storeData: StoreData? = null,
    val url: String? = null,
    val title: String = "",
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
    val categories: List<StoreCategory> = emptyList(),
    val currentCategoryId: Int = DEFAULT_GENRE,
    val newVersionAvailable: Boolean = false,
) {
    constructor(data: StoreData?) : this(storeData = data, url = data?.url, title = data?.label.orEmpty())

    val currentCategory: StoreCategory
        get() = categories.first { it.id == currentCategoryId }
}
