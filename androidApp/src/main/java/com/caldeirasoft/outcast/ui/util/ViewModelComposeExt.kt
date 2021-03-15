package com.caldeirasoft.outcast.ui.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.airbnb.mvrx.*

@OptIn(InternalMavericksApi::class)
@Composable
inline fun <reified VM : MavericksViewModel<S>, reified S : MavericksState> mavericksViewModel(
    initialArgument: Any,
    scope: LifecycleOwner = LocalLifecycleOwner.current,
    noinline keyFactory: (() -> String)? = null,
): VM {
    val activity = LocalContext.current as? ComponentActivity ?: error("Composable is not hosted in a ComponentActivity!")
    val viewModelStoreOwner = scope as? ViewModelStoreOwner ?: error("LifecycleOwner must be a ViewModelStoreOwner!")
    val savedStateRegistryOwner = scope as? SavedStateRegistryOwner ?: error("LifecycleOwner must be a SavedStateRegistryOwner!")
    val savedStateRegistry = savedStateRegistryOwner.savedStateRegistry
    val viewModelClass = VM::class
    val viewModelContext = remember(scope, activity, viewModelStoreOwner, savedStateRegistry) {
        ActivityViewModelContext(activity, initialArgument, viewModelStoreOwner, savedStateRegistry)
    }
    return remember(viewModelClass, viewModelContext) {
        MavericksViewModelProvider.get(
            viewModelClass = viewModelClass.java,
            stateClass = S::class.java,
            viewModelContext = viewModelContext,
            key = keyFactory?.invoke() ?: viewModelClass.java.name,
        )
    }
}
