package com.caldeirasoft.outcast.domain.util

sealed class NetworkResponse<out T : Any>() {

    /**
     * A request that resulted in a response with a 2xx status code that has a body
     */
    data class Success<T: Any>(
        val body: T,
        val code: Int = 200,
        val headers: Map<String, Any>? = null,
        ) : NetworkResponse<T>()


    /**
     * A request that resulted in a response with a non-2xx status code
     */
    data class ServerError<T: Any>(
        val body: String?,
        val code: Int,
        val headers: Map<String, Any>? = null,
    ) : NetworkResponse<T>()

    /**
     * A request that didn't result in a response
     */
    data class NetworkError(val error: Exception) : NetworkResponse<Nothing>()

    /**
     * A request that result in an error different from an IO or Server error
     * An example of sunch an error is JSON parsing exception thrown by a serialization library
     */
    data class UnknownError(val error: Throwable) : NetworkResponse<Nothing>()

    /**
     * A request that result in an cached response
     */
    object Cached : NetworkResponse<Nothing>()
}