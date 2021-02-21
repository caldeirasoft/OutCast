package com.caldeirasoft.outcast.domain.util

sealed class DataState<out T> {
    class Idle<T> : DataState<T>()
    class Loading<T> : DataState<T>()
    data class Success<T>(val data: T) : DataState<T>()
    class Empty<T> : DataState<T>()
    data class Error<T>(val throwable: Throwable) : DataState<T>()
}

inline fun <T> DataState<T>.onLoading(action: () -> Unit): DataState<T> {
    if (this is DataState.Loading) {
        action()
    }
    return this
}

inline fun <T> DataState<T>.onSuccess(action: (T) -> Unit): DataState<T> {
    if (this is DataState.Success) {
        action(data)
    }
    return this
}

inline fun <T> DataState<T>.onEmpty(action: () -> Unit): DataState<T> {
    if (this is DataState.Empty) {
        action()
    }
    return this
}

inline fun <T> DataState<T>.onError(action: (Throwable) -> Unit): DataState<T> {
    if (this is DataState.Error) {
        action(throwable)
    }

    return this
}