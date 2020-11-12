package com.caldeirasoft.outcast.domain.util

sealed class Resource<out T>() {
    /**
     * A request that resulted in a response with a 2xx status code that has a body
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * A request that resulted in a response with an error
     */
    data class Error<T>(val msg: String) : Resource<T>()

    /**
     * A request that resulted in a response with an error
     */
    data class ErrorThrowable<T>(val throwable: Throwable) : Resource<T>()

    /**
     * A request that resulted in a response with an error
     */
    data class Loading<T>(val data: T? = null) : Resource<T>()
}