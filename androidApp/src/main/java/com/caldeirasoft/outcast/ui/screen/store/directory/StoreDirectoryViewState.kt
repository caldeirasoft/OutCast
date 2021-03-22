package com.caldeirasoft.outcast.ui.screen.store.directory

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.ui.screen.store.base.FollowState
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class StoreDirectoryViewState(
    val storeData: StoreGroupingPage? = null,
    val storeFront: String? = null,
    override val followingStatus: Map<Long, FollowStatus> = emptyMap(),
) : MavericksState, FollowState
