package com.noghre.sod.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noghre.sod.data.remote.ProductApi
import com.noghre.sod.domain.model.Product

/**
 * PagingSource implementation for Product pagination
 * Fetches products from API with page-based pagination
 */
class ProductPagingSource(
    private val api: ProductApi,
    private val category: String? = null,
    private val sortBy: String = "name"
) : PagingSource<Int, Product>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Product> = try {
        val position = params.key ?: STARTING_PAGE_INDEX
        val pageSize = params.loadSize

        // Fetch products from API
        val response = api.getProducts(
            page = position,
            pageSize = pageSize,
            category = category,
            sortBy = sortBy
        )

        val products = response.products
        val hasNext = response.hasNextPage

        LoadResult.Page(
            data = products,
            prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
            nextKey = if (hasNext) position + 1 else null
        )
    } catch (exception: Exception) {
        LoadResult.Error(exception)
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}
