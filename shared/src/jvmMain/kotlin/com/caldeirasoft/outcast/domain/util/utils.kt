package com.caldeirasoft.outcast.domain.util

import jdk.internal.org.jline.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.io.Console

actual fun <T> suspendCall(block: suspend CoroutineScope.() -> T): T = runBlocking(block = block)

actual fun Log_D(tag: String, message: String) {
    //Log(tag, message)
}