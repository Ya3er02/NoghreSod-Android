package com.noghre.sod.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.noghre.sod.data.local.database.ProductDatabase
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.RemoteKeyEntity
import com.noghre.sod.data.remote.ProductApi

/**
 * RemoteMediator for hybrid synchronization
 * Combines local Room database with remote API calls
 * Handles pagination with caching strategy
 */
@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val db: ProductDatabase,
    private val api: ProductApi,
    private val category: String? = null
) : RemoteMediator<Int, ProductEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = CACHE_TIMEOUT_MINUTES * 60 * 1000 // minutes to milliseconds
        val lastUpdate = db.remoteKeyDao().getCreationTime(category ?: "all")
        val timeSinceLastUpdate = System.currentTimeMillis() - (lastUpdate ?: 0)

        return if (timeSinceLastUpdate >= cacheTimeout) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult = try {
        val pageKey = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyForPage(loadType)
                remoteKey?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                remoteKey?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
        }

        // Fetch from API
        val response = api.getProducts(
            page = pageKey,
            pageSize = state.config.pageSize,
            category = category
        )

        val isEndOfList = response.hasNextPage.not()

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                db.productDao().clearAll()
                db.remoteKeyDao().deleteByCategory(category ?: "all")
            }

            // Insert products
            val products = response.products.map { it.toEntity() }
            db.productDao().insertAll(products)

            // Insert remote keys for pagination
            val remoteKeys = response.products.mapIndexed { index, product ->
                RemoteKeyEntity(
                    id = product.id,
                    prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                    currentKey = pageKey,
                    nextKey = if (isEndOfList) null else pageKey + 1,
                    category = category ?: "all",
                    createdAt = System.currentTimeMillis()
                )
            }
            db.remoteKeyDao().insertAll(remoteKeys)
        }

        MediatorResult.Success(endOfPaginationReached = isEndOfList)
    } catch (exception: Exception) {
        MediatorResult.Error(exception)
    }

    private suspend fun getRemoteKeyForPage(loadType: LoadType): RemoteKeyEntity? {
        return when (loadType) {
            LoadType.REFRESH -> db.remoteKeyDao().remoteKeyByCategory(category ?: "all")
            else -> null
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ProductEntity>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            db.remoteKeyDao().remoteKeyById(it.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ProductEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            db.remoteKeyDao().remoteKeyById(it.id)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val CACHE_TIMEOUT_MINUTES = 30
    }
}
