package com.caldeirasoft.outcast.domain.util

import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

/*
/**
 * A generic function that can stream network resource and fetch and save it
 * It is used for retry resource when we already observe data from database
 *
 * [ResultType] represents the type for database.
 * [RequestType] represents the type for network.
 */
inline fun <ResponseType: Any> fetchResourceAndSave(
    crossinline networkCall: suspend () -> NetworkResponse<ResponseType>,
    crossinline saveRemoteData: suspend (ResponseType) -> Unit = { Unit },
) = flow<DataState<Unit>> {

    emit(DataState.Loading())
    when (val response = networkCall.invoke()) {
        is NetworkResponse.Success -> {
            saveRemoteData(response.body)
            emit(DataState.Success(Unit))
        }
        is NetworkResponse.NetworkError -> emit(DataState.Error(response.error))
        is NetworkResponse.ServerError -> emit(DataState.Error(Exception(response.code.toString())))
        is NetworkResponse.UnknownError -> emit(DataState.Error(response.error))
    }
}
*/
@OptIn(ExperimentalTime::class)
suspend inline fun <R> stopwatch(msg: String, crossinline action: suspend () -> R): R {
    val start = Clock.System.now()
    try {
        val result = action.invoke()
        return result
    }
    finally {
        val end = Clock.System.now()
        val elapsed = end.minus(start)
        Log_D("TAG", "${msg} - (${elapsed.toString()})", )
    }
}

inline fun <reified T> Any?.checkType(): Boolean =
    (this is T)

inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}

inline fun <reified T> Any?.castAs(): T? =
    if (this is T) { this } else null
