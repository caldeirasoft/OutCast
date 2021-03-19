package com.caldeirasoft.outcast.ui.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

interface ScrollViewModel {
    var scrollState: ListState

    fun saveScrollState(index: Int, offset: Int) {
        scrollState = ListState(index = index, offset = offset)
    }

    @Composable
    fun collectAsLazyListState(): LazyListState {
        val lastScrollState = remember { scrollState }

        val scrollState = rememberLazyListState(
            initialFirstVisibleItemIndex = lastScrollState.index,
            initialFirstVisibleItemScrollOffset = lastScrollState.offset
        )

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.isScrollInProgress }
                .distinctUntilChanged()
                .filter { !it }
                .collect {
                    saveScrollState(
                        scrollState.firstVisibleItemIndex,
                        scrollState.firstVisibleItemScrollOffset
                    )
                }
        }
        return scrollState
    }
}