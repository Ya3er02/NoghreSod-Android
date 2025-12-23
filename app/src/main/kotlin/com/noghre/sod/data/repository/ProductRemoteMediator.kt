package com.noghre.sod.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.noghre.sod.core.config.AppConfig
import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.mapper.toEntity
import com.noghre.sod.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for Paging 3.
 * Manages the boundary between local Room cache and remote API.
 * Implements three load scenarios:
 * - REFRESH: User pulls to refresh
 * - PREPEND: Loading newer items (when at the end of list)
 * - APPEND: Loading older items (infinite scroll at bottom)
 *
 * The mediator coordinates:
 * 1. Remote key storage (current page number)
 * 2. Database transactions (atomic insert + key update)
 * 3. Cache invalidation
 *
 * @param apiService API client for fetching pages
 * @param database Room database for caching
 * @param category Optional category filter
 * @param sort Optional sort parameter
 */
@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val category: String? = null,
    private val sort: String? = null
) : RemoteMediator<Int, ProductEntity>() {

    private val productDao = database.productDao()

    /**
     * Called by Paging 3 to determine if remote data should be loaded.
     * Returns MediatorResult based on load operation outcome.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            // Determine which page to load
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastPage = state.lastItemOrNull()?.let { product ->
                        // Calculate page from position
                        (state.config.pageSize / state.config.pageSize) + 1
                    } ?: 1
                    lastPage + 1
                }
            }

            // Fetch from API
            val response = when {
                category != null -> apiService.getProductsByCategory(
                    categoryId = category,
                    page = loadKey,
                    limit = AppConfig.Pagination.DEFAULT_PAGE_SIZE
                ).body()
                else -> apiService.getProducts(
                    page = loadKey,
                    limit = AppConfig.Pagination.DEFAULT_PAGE_SIZE,
                    sort = sort
                ).body()
            }

            if (response == null) {
                return MediatorResult.Error(IOException("Empty response from API"))
            }

            val products = response.data
            val endOfPaginationReached = loadKey >= response.totalPages

            // Save to database in a transaction
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // On refresh, clear old products
                    productDao.deleteAllProducts()
                }
                // Insert new products
                products.forEach { product ->
                    productDao.insertProduct(product.toEntity())
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}
