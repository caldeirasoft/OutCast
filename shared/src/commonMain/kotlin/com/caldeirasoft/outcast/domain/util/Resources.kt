package com.caldeirasoft.outcast.domain.util

expect object Resources {
    fun getResourceContent(filename:String): String
}