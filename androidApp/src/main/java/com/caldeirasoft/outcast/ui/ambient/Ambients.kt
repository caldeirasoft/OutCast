package com.caldeirasoft.outcast.ui.ambient

import androidx.compose.runtime.staticAmbientOf
import com.caldeirasoft.outcast.ui.navigation.Actions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
/*val InboxViewModelAmbient =
    staticAmbientOf<InboxViewModel> { error("not viewModel") }
val StoreDirectoryViewModelAmbient =
    staticAmbientOf<StoreDirectoryViewModel> { error("not viewModel") }
val StoreDataViewModelAmbient =
    staticAmbientOf<StoreDataViewModel> { error("not viewModel") }*/
val ActionsAmbient = staticAmbientOf<Actions>()

