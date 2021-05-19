package com.caldeirasoft.outcast.data.util.network

import okhttp3.Interceptor
import okhttp3.Response
import kotlin.time.days

class RewriteResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        val cacheControl = originalResponse.header("CacheControl")
        return if (cacheControl == null ||
                arrayListOf("no-store", "no-cache", "must-revalidate", "max-age=0").any { cacheControl.contains(it) }) {
            //Cache results for 1 day =>
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 1.days.inSeconds)
                    .build()
        } else {
            originalResponse
        }
    }
}
