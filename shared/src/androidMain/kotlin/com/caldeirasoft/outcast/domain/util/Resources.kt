package com.caldeirasoft.outcast.domain.util

actual object Resources {
    actual fun getResourceContent(filename:String): String =
        javaClass.classLoader.getResource(filename).readText()
}