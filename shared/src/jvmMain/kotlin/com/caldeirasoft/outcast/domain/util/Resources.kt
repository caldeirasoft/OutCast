package com.caldeirasoft.outcast.domain.util

object Resources {
    fun getResourceContent(filename:String): String =
        javaClass.classLoader.getResource(filename).readText()
}