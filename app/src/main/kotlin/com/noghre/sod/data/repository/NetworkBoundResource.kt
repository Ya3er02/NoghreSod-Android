package com.noghre.sod.data.repository

import com.noghre.sod.core.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Generic offline-first pattern implementation using Flow.
 *
 * This utility implements the offline-first strategy:
 * 1. Load data from local cache and emit as Loading state
 * 2. Fetch fresh data from network
 * 3. If fetch is successful, save to cache and emit as Success
 * 4. If fetch fails, emit Error with cached data if available
 *
 * Usage example:
 * ```
 * fun getProducts(): Flow<Result<List<Product>>> =
 *     networkBoundResource(
 *         query = { productDao.getAllProductsFlow() },
 *         fetch = { apiService.getProducts() },
 *         saveFetchResult = { products -> productDao.insertProducts(...) },
 *         shouldFetch = { cached -> cached.isEmpty() || isCacheStale() }
 *     )
 * ```
 *
 * @param T Type of resource (e.g., Product, List<Product>)
 * @param query Suspend function that queries local cache, returns Flow
 * @param fetch Suspend function that fetches from network
 * @param saveFetchResult Suspend function that saves network result to cache
 * @param shouldFetch Predicate to determine if fresh fetch is needed
 * @return Flow of Result states (Loading -> Success/Error)
 */
fun <T> networkBoundResource(
    query: suspend () -> Flow<T>,
    fetch: suspend () -> T,
    saveFetchResult: suspend (T) -> Unit,
    shouldFetch: suspend (T) -> Boolean = { true }
): Flow<Result<T>> = flow {
    // 1. Start by emitting cached data as Loading state
    val cachedData = query()
    emitAll(
        cachedData.map { cached ->
            Result.Loading(data = cached)
        }
    )

    try {
        // 2. Check if we should fetch fresh data
        val data = cachedData.lastOrNull() // Get the latest emitted value
        if (data != null && !shouldFetch(data)) {
            // Cache is still valid, emit success
            emit(Result.Success(data))
            return@flow
        }

        // 3. Fetch from network
        val networkResult = fetch()
        
        // 4. Save to cache
        saveFetchResult(networkResult)
        
        // 5. Emit success
        emit(Result.Success(networkResult))
    } catch (e: Exception) {
        // 6. On error, get latest cached data
        val cachedData = cachedData.lastOrNull()
        // 7. Emit error with cached data as fallback
        emit(Result.Error(e.toAppError(), data = cachedData))
    }
}

/**
 * Simplified version for one-time fetch (no Flow from query).
 *
 * Usage:
 * ```
 * suspend fun getProduct(id: String): Result<Product> =
 *     networkBoundResource(
 *         query = { productDao.getProduct(id) },
 *         fetch = { apiService.getProductDetail(id) },
 *         saveFetchResult = { product -> productDao.insertProduct(...) }
 *     )
 * ```
 */
suspend fun <T> networkBoundResourceSuspend(
    query: suspend () -> T?,
    fetch: suspend () -> T,
    saveFetchResult: suspend (T) -> Unit,
    shouldFetch: suspend (T?) -> Boolean = { true }
): Result<T> = try {
    val cached = query()
    
    if (cached != null && !shouldFetch(cached)) {
        return Result.Success(cached)
    }
    
    val networkResult = fetch()
    saveFetchResult(networkResult)
    Result.Success(networkResult)
} catch (e: Exception) {
    val cached = query()
    Result.Error(e.toAppError(), data = cached)
}

/**
 * Extension function to get the last emitted value from a Flow.
 * Used internally by networkBoundResource.
 */
suspend fun <T> Flow<T>.lastOrNull(): T? {
    var value: T? = null
    kotlinx.coroutines.flow.collect { value = it }
    return value
}

/**
 * Extension function to convert Throwable to AppError.
 * Import from your error handling module.
 */
fun Throwable.toAppError(): com.noghre.sod.core.result.AppError {
    return when (this) {
        is java.io.IOException -> com.noghre.sod.core.result.AppError.NetworkError(
            message = "خطا در اتصال به اینترنت",
            throwable = this
        )
        is java.net.SocketTimeoutException -> com.noghre.sod.core.result.AppError.TimeoutError(
            message = "مهلت زمانی درخواست پایان یافت",
            throwable = this
        )
        is retrofit2.HttpException -> com.noghre.sod.core.result.AppError.ServerError(
            code = this.code(),
            message = this.message(),
            throwable = this
        )
        else -> com.noghre.sod.core.result.AppError.UnknownError(
            message = this.message ?: "خطای نامشخص",
            throwable = this
        )
    }
}
