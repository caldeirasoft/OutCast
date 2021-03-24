package com.caldeirasoft.outcast.ui.components.foundation

import androidx.compose.runtime.compositionLocalOf

class ViewPagerController {
    internal var requestMoveTo: (page: Int) -> Unit = {}

    fun moveTo(page: Int) {
        requestMoveTo.invoke(page)
    }
}

val LocalViewPagerController = compositionLocalOf { ViewPagerController() }