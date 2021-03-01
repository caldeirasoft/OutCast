package com.caldeirasoft.outcast.data.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException

abstract class BasePagingSource<T: Any>: PagingSource<Int, T>()
{
    var position = STARTING_PAGE_INDEX

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        position = params.key ?: STARTING_PAGE_INDEX

        return try {
            /*var results = loadFromLocalStorage()

            if (results == null || results.isEmpty() == true) {
                results = loadFromNetwork() ?: emptyList()
            }*/
            val results = loadFromNetwork(params) ?: emptyList()

            LoadResult.Page(
                data = results,
                prevKey = getPreviousKey(results),
                nextKey = getNextKey(results, params.loadSize)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    abstract suspend fun loadFromNetwork(params: LoadParams<Int>): List<T>?

    private fun getNextKey(results: List<T>, paramLoadSize: Int): Int? =
        if (results.isNotEmpty() && results.size == paramLoadSize)
            position + results.size else null

    private fun getPreviousKey(results: List<T>): Int? =
        if (position > STARTING_PAGE_INDEX) position - results.size else null

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition

    companion object {
        const val STARTING_PAGE_INDEX = 0
        const val THRESHOLD = 20
    }

}