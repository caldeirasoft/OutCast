package com.caldeirasoft.outcast.ui.util

interface ScrollViewModel {
    var scrollState: ListState

    fun saveScrollState(index: Int, offset: Int) {
        scrollState = ListState(index = index, offset = offset)
    }
}