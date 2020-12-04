package com.caldeirasoft.outcast.domain.util

import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

/**
 * A generic class that can provide a resource backed by both
 * the SQLite database and the network.
 *
 * [ResultType] represents the type for database.
 * [RequestType] represents the type for network.
 */
inline fun <ResponseType: Any> networkCall(
    crossinline fetchFromRemote: suspend () -> ResponseType
) = flow<DataState<ResponseType>> {
        emit(DataState.Success(fetchFromRemote()))
    }
    .onStart { emit(DataState.Loading()) }
    .catch { emit(DataState.Error<ResponseType>(it)) }


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