package com.noghre.sod.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noghre.sod.data.remote.api.ProductApi
import com.noghre.sod.domain.model.Product
import retrofit2.HttpException
import java.io.IOException

/**
 * Paging source for product list pagination using Paging 3.
 * Handles network pagination of products with efficient page loading.
 * 
 * @property productApi API service for fetching products
 * @property query Optional search query for filtering
 * @property category Optional category filter
 * 
 * @since 1.0.0
 */
class ProductsPagingSource(
    private val productApi: ProductApi,
    private val query: String? = null,
    private val category: String? = null
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        // Typically, getRefreshKey returns the page key of the page that was closest
        // to the most recently accessed index.
        // Anchor position is the most recently accessed index in the list.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        // Start refresh at page 1 if undefined.
        val pageNumber = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = productApi.getProducts(
                page = pageNumber,
                pageSize = params.loadSize,
                query = query,
                category = category
            )

            val products = response.data ?: emptyList()
            val nextKey = if (products.isEmpty()) null else pageNumber + 1
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1

            LoadResult.Page(
                data = products.map { it.toDomain() },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            // IOException for network failures.
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}
