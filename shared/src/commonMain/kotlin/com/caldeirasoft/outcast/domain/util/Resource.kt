package com.caldeirasoft.outcast.domain.util

sealed class Resource<out T>(val status: Status, val message: String?) {
    /**
     * A request that resulted in a response with a 2xx status code that has a body
     */
    data class Success<T : Any>(val data: T)
        : Resource<T>(Status.SUCCESS, message = null)

    /**
     * A request that resulted in a response with an error
     */
    data class Error<T : Any>(val msg: String, val data: T?)
        : Resource<T>(Status.ERROR, message = msg)

    /**
     * A request that resulted in a response with an error
     */
    data class ErrorThrowable<T : Any>(val throwable: Throwable?, val data: T?)
        : Resource<T>(Status.ERROR, message = throwable?.message)

    /**
     * A request that resulted in a response with an error
     */
    data class Loading<T : Any>(val data: T?)
        : Resource<T>(Status.LOADING, message = null)
}