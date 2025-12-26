package com.noghre.sod.data.local.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.data.remote.util.NetworkResult
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.model.Money
import com.noghre.sod.domain.model.Currency
import timber.log.Timber
import java.math.BigDecimal

/**
 * PagingSource for infinite scrolling pagination of products.
 * Loads products page by page to optimize memory usage and performance.
 * 
 * @param api Retrofit API interface
 * @param category Optional category filter
 * @param searchQuery Optional search query
 * 
 * @author Yaser
 * @version 1.0.0
 */
class ProductPagingSource(
    private val api: NoghreSodApi,
    private val category: String? = null,
    private val searchQuery: String? = null
) : PagingSource<Int, ProductSummary>() {
    
    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 20
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductSummary> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            
            val response = when {
                !searchQuery.isNullOrBlank() -> {
                    // Search products
                    api.searchProducts(
                        query = searchQuery,
                        page = page,
                        limit = params.loadSize
                    )
                }
                !category.isNullOrBlank() -> {
                    // Get products by category
                    api.getProducts(
                        page = page,
                        limit = params.loadSize,
                        category = category
                    )
                }
                else -> {
                    // Get all products
                    api.getProducts(
                        page = page,
                        limit = params.loadSize
                    )
                }
            }
            
            // Handle response
            if (!response.isSuccessful) {
                Timber.e("API Error: ${response.code()} - ${response.message()}")
                return LoadResult.Error(Exception("HTTP ${response.code()}"))
            }
            
            val data = response.body()?.data ?: emptyList()
            val products = data.map { dto ->
                ProductSummary(
                    id = dto.id,
                    name = dto.name,
                    price = Money(
                        amount = BigDecimal(dto.price),
                        currency = Currency.IRR
                    ),
                    mainImage = dto.images.firstOrNull()?.url ?: "",
                    category = mapStringToProductCategory(dto.category),
                    purity = mapToPurityType(dto.purity ?: 925),
                    rating = dto.rating ?: 0f,
                    reviewCount = dto.reviewCount ?: 0,
                    inStock = dto.inStock ?: true,
                    isNew = isProductNew(dto.createdAt),
                    isFeatured = dto.isFeatured ?: false,
                    discountPercentage = dto.discountPercentage
                )
            }
            
            // Calculate next page
            val nextKey = if (products.isEmpty()) {
                null
            } else {
                page + 1
            }
            
            LoadResult.Page(
                data = products,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading products")
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, ProductSummary>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    
    private fun mapStringToProductCategory(category: String?): com.noghre.sod.domain.model.ProductCategory {
        return try {
            com.noghre.sod.domain.model.ProductCategory.fromString(category ?: "OTHER")
        } catch (e: Exception) {
            com.noghre.sod.domain.model.ProductCategory.OTHER
        }
    }
    
    private fun mapToPurityType(purity: Double): com.noghre.sod.domain.model.PurityType {
        return when {
            purity >= 99.0 -> com.noghre.sod.domain.model.PurityType.SILVER999
            purity >= 95.0 -> com.noghre.sod.domain.model.PurityType.SILVER950
            purity >= 92.0 -> com.noghre.sod.domain.model.PurityType.STERLING925
            else -> com.noghre.sod.domain.model.PurityType.SILVER800
        }
    }
    
    private fun isProductNew(createdAt: String?): Boolean {
        return try {
            if (createdAt.isNullOrBlank()) return false
            val created = java.time.LocalDateTime.parse(createdAt)
            val daysSinceCreated = java.time.temporal.ChronoUnit.DAYS.between(
                created,
                java.time.LocalDateTime.now()
            )
            daysSinceCreated <= 7
        } catch (e: Exception) {
            false
        }
    }
}
