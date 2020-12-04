package com.caldeirasoft.outcast.ui.util

sealed class ScreenState {
    object Idle : ScreenState()
    object Loading : ScreenState()
    data class Error(val throwable: Throwable) : ScreenState()
    object Success : ScreenState()
    object Empty : ScreenState()
}

inline fun ScreenState.onLoading(action: () -> Unit): ScreenState {
    if (this is ScreenState.Loading) {
        action()
    }
    return this
}

inline fun ScreenState.onSuccess(action: () -> Unit): ScreenState {
    if (this is ScreenState.Success) {
        action()
    }
    return this
}

inline fun ScreenState.onEmpty(action: () -> Unit): ScreenState {
    if (this is ScreenState.Empty) {
        action()
    }
    return this
}

inline fun ScreenState.onError(action: (Throwable) -> Unit): ScreenState {
    if (this is ScreenState.Error) {
        action(throwable)
    }

    return this
}