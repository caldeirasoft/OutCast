package com.caldeirasoft.outcast.data.util.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class RewriteOfflineRequestInterceptor(val context: Context) : Interceptor {
    val networkStatusUtil = NetworkStatusUtil(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.method.equals("GET")) {
            if (!networkStatusUtil.hasNetConnection()) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "only-if-cached")
                        .build()
            } else {
                //Use stale cache thats at the most 1 day old if no network available
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "max-stale=86400")
                        .build()
            }
        }

        return chain.proceed(request)
    }
}