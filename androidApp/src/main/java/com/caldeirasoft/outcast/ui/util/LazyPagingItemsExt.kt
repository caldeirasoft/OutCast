package com.caldeirasoft.outcast.ui.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

fun LazyPagingItems<*>.ifLoading(
    loadingContent: () -> Unit
) : LazyPagingItems<*> {
    val loadState = this.loadState
    if (loadState.refresh is LoadState.Loading)
        loadingContent()

    return this
}

fun LazyPagingItems<*>.ifError(
    errorContent: (Throwable) -> Unit
) : LazyPagingItems<*> {
    val loadState = this.loadState
    val refreshState = loadState.refresh
    if (refreshState is LoadState.Error) {
        errorContent(refreshState.error)
    }
    return this
}

fun LazyPagingItems<*>.ifEmpty(
    emptyContent: () -> Unit
) : LazyPagingItems<*> {
    val loadState = this.loadState
    val refreshState = loadState.refresh
    if (refreshState is LoadState.NotLoading
        && loadState.append.endOfPaginationReached
        && this.itemCount == 0
    ) {
        emptyContent()
    }
    return this
}

fun LazyPagingItems<*>.ifNotLoading(
    itemsContent: () -> Unit,
) : LazyPagingItems<*> {
    val loadState = this.loadState
    if (loadState.refresh is LoadState.NotLoading)
        itemsContent()
    return this
}

fun LazyPagingItems<*>.ifLoadingMore(
    loadingContent: () -> Unit
) : LazyPagingItems<*> {
    val loadState = this.loadState
    if (loadState.append is LoadState.Loading) {
        loadingContent()
    }
    return this
}

fun LazyPagingItems<*>.itemIfErrorOnLoadingMore(
    errorContent: (Throwable) -> Unit,
): LazyPagingItems<*> {
    val appendState = this.loadState.append
    if (appendState is LoadState.Error) {
        errorContent(appendState.error)
    }
    return this
}

val LazyPagingItems<*>.isLoading: Boolean
    get() = (this.loadState.refresh is LoadState.Loading)


val LazyPagingItems<*>.isError: Boolean
    get() = (this.loadState.refresh is LoadState.Error)


val LazyPagingItems<*>.isNotLoading: Boolean
    get() = (this.loadState.refresh is LoadState.NotLoading)

val LazyPagingItems<*>.isEmpty: Boolean
    get() {
        return (this.loadState.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached &&
                this.itemCount == 0)
    }

val LazyPagingItems<*>.isLoadingMore: Boolean
    get() = (this.loadState.append is LoadState.Loading)


inline fun <reified T : Any> LazyPagingItems<T>.refreshOrRetry() {
    if (loadState.refresh is LoadState.Error) {
        retry()
    } else {
        refresh()
    }
}