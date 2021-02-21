package com.caldeirasoft.outcast.domain.util

sealed class Resource {
    /**
     * A request that resulted in a response with a 2xx status code that has a body
     */
    data class Success<T>(val data: T) : Resource()

    /**
     * A request that resulted in a response with an error
     */
    data class Error(val throwable: Throwable) : Resource()

    /**
     * A request that resulted in a response with an error
     */
    object Loading : Resource()

    companion object {
        inline fun Resource.onLoading(action: () -> Unit): Resource {
            if (this is Loading) {
                action()
            }
            return this
        }

        inline fun Resource.onSuccess(action: () -> Unit): Resource {
            if (this is Success<*>) {
                action()
            }
            return this
        }

        inline fun <reified T> Resource.onSuccess(action: (T) -> Unit): Resource {
            if (this is Success<*> && this.data is T) {
                action(this.data)
            }
            return this
        }

        inline fun Resource.onError(action: (Throwable) -> Unit): Resource {
            if (this is Error) {
                action(throwable)
            }

            return this
        }
    }
}