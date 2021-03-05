package com.caldeirasoft.outcast.ui.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.airbnb.mvrx.*
import org.koin.androidx.viewmodel.ViewModelOwner.Companion.from
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Compose v1.0.0-alpha12 and koin v2.2.2 are not compatible. This file contains koin source code as
 * a temporary workaround.
 */

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T : ViewModel> getViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    val owner = LocalViewModelStoreOwner.current.viewModelStore
    return remember {
        GlobalContext.get()
            .getViewModel(qualifier, owner = { from(owner) }, parameters = parameters)
    }
}

/**
 * Resolve a dependency for [Composable] functions
 * @param qualifier
 * @param parameters
 *
 * @return Lazy instance of type T
 *
 * @author Arnaud Giuliani
 * @author Henrique Horbovyi
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T = remember {
    GlobalContext.get().get(qualifier, parameters)
}

@Composable
fun getKoin(): Koin = remember {
    GlobalContext.get()
}

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
