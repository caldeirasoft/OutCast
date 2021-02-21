package com.caldeirasoft.outcast.domain.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun <T> suspendCall(block: suspend CoroutineScope.() -> T): T = runBlocking(block = block)

fun Log_D(tag: String, message: String) {
    Log.d(tag, message)
}