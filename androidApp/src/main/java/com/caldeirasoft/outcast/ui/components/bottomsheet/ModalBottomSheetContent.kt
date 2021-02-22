package com.caldeirasoft.outcast.ui.components.bottomsheet

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.caldeirasoft.outcast.ui.util.ComposableFn

class ModalBottomSheetContent(defaultContent: ComposableFn) {
    /**
     * Bottom Drawer content
     */
    var content: MutableState<ComposableFn> = mutableStateOf(defaultContent)
        private set

    /**
     * Update bottom drawer content
     */
    fun updateContent(newContent: ComposableFn) {
        content.value = newContent
    }
}