package com.caldeirasoft.outcast.domain.util

import kotlinx.coroutines.CoroutineScope

expect fun <T> suspendCall(block: suspend CoroutineScope.() -> T): T

expect fun Log_D(tag: String, message: String)