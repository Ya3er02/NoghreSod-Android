package com.noghre.sod.data.local.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.domain.model.Product
import timber.log.Timber

/**
 * Paging source for product pagination using Paging 3.
 */
class ProductPagingSource(
    private val api: NoghreSodApi,
    private val query: String? = null
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        // Return the page index to refresh from, or null to start from 0
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: INITIAL_PAGE
        val pageSize = params.loadSize

        return try {
            Timber.d("Loading products page: $page, pageSize: $pageSize")

            val response = if (query != null) {
                // Search products if query is provided
                api.searchProducts(query, page, pageSize)
            } else {
                // Fetch all products
                api.getProducts(page, pageSize)
            }

            val products = response.data // Assuming response has a 'data' field
            val nextKey = if (products.isEmpty()) null else page + 1
            val prevKey = if (page == INITIAL_PAGE) null else page - 1

            Timber.d("Loaded ${products.size} products for page $page")

            LoadResult.Page(
                data = products,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading products page $page")
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}
