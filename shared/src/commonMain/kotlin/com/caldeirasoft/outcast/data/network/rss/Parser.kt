package com.caldeirasoft.outcast.data.network.rss

interface Parser<out T> {
    fun parse(xml: String): T
}