package com.caldeirasoft.outcast.data.utils

import android.util.Log

const val TAG_PREFIX = "KtRssReader/"

fun logD(tag: String, message: String) {
    Log.d("$TAG_PREFIX$tag", message)
}

fun logW(tag: String, message: String) {
    Log.w("$TAG_PREFIX$tag", message)
}