package com.noghre.sod.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.noghre.sod.data.local.database.ProductDatabase
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.paging.ProductPagingSource
import com.noghre.sod.data.paging.ProductRemoteMediator
import com.noghre.sod.data.remote.ProductApi
import com.noghre.sod.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository for paginated product listing
 * Combines network and database with RemoteMediator
 */
class ProductPagingRepository @Inject constructor(
    private val db: ProductDatabase,
    private val api: ProductApi
) {

    /**
     * Get paginated products with hybrid caching
     * Uses RemoteMediator for automatic sync
     */
    fun getProductsPaged(
        category: String? = null,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                maxSize = MAX_SIZE
            ),
            remoteMediator = ProductRemoteMediator(
                db = db,
                api = api,
                category = category
            ),
            pagingSourceFactory = {
                db.productDao().pagingSource(category)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    /**
     * Get paginated products from API only (without caching)
     * Used when RemoteMediator not available
     */
    fun getProductsPagedDirect(
        category: String? = null,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductPagingSource(
                    api = api,
                    category = category
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val MAX_SIZE = 100
    }
}
