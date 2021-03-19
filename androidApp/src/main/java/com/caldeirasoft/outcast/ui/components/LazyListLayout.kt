package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun LazyListLayout(
    modifier: Modifier = Modifier,
    lazyListItems: LazyPagingItems<*>,
    onLoading: @Composable () -> Unit = { LoadingScreen() },
    onError: @Composable (Throwable) -> Unit = { ErrorScreen(t = it) },
    onSuccessEmpty: @Composable () -> Unit = {},
    onSuccess: @Composable () -> Unit,
) {
    val loadState = lazyListItems.loadState
    val refreshState = loadState.refresh
    Box(modifier = modifier.fillMaxSize()) {
        when {
            refreshState is LoadState.Loading ->
                onLoading()
            refreshState is LoadState.Error ->
                onError(refreshState.error)
            (refreshState is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && lazyListItems.itemCount == 0) ->
                onSuccessEmpty()
            refreshState is LoadState.NotLoading && lazyListItems.itemCount > 0 ->
                onSuccess()
        }
    }
}