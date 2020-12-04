package com.caldeirasoft.outcast.data.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

class NetworkStatusUtil(val context: Context) {

    var connectivityManager: ConnectivityManager

    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun withNetConnection(
            onSuccess: (() -> Unit)? = null,
            onError: (() -> Unit)? = null,
            onConnected: (() -> Unit)? = null
    ) {
        if (hasNetConnection()) {
            onSuccess?.invoke()
        } else {
            onError?.invoke()

            onConnected?.let { callback ->
                registerNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        connectivityManager.unregisterNetworkCallback(this)

                        callback.invoke()
                    }
                })
            }
        }
    }

    fun hasNetConnection(): Boolean {
        val isConnected = connectivityManager.activeNetwork?.let { true } ?: false
        return isConnected
    }

    private fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, callback)
    }
}