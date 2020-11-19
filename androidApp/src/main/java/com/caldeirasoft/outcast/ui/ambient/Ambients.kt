package com.caldeirasoft.outcast.ui.ambient

import androidx.compose.runtime.staticAmbientOf
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.storedata.StoreDataViewModel
import com.caldeirasoft.outcast.ui.screen.storedirectory.StoreDirectoryViewModel

val InboxViewModelAmbient =
    staticAmbientOf<InboxViewModel> { error("not viewModel") }
val StoreDirectoryViewModelAmbient =
    staticAmbientOf<StoreDirectoryViewModel> { error("not viewModel") }
val StoreDataViewModelAmbient =
    staticAmbientOf<StoreDataViewModel> { error("not viewModel") }
val ActionsAmbient = staticAmbientOf<Actions>()

