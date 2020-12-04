package com.caldeirasoft.outcast.domain.util

import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

expect fun <T> suspendCall(block: suspend CoroutineScope.() -> T): T

expect fun Log_D(tag: String, message: String)