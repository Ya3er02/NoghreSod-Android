package com.noghre.sod.data.repository

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.error.ExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import timber.log.Timber

/**
 * Single Source of Truth (SSOT) pattern for data access.
 *
 * Implements network-first caching strategy:
 * 1. Query local database first
 * 2. If data exists and not stale, emit it
 * 3. Fetch fresh data from network in parallel
 * 4. Cache the network response locally
 * 5. Emit fresh data if different from cache
 *
 * Usage:
 * ```
 * override fun getProducts(): Flow<Result<List<Product>>> = networkBoundResource(
 *     query = { productDao.getAllProducts() },
 *     fetch = { api.getProducts() },
 *     saveFetchResult = { response -> 
 *         productDao.clear()
 *         productDao.insertAll(response.toEntities()) 
 *     },
 *     shouldFetch = { localData -> 
 *         localData.isEmpty() || isCacheExpired() 
 *     }
 * )
 * ```
 *
 * @author NoghreSod Team
 * @version 2.0.0
 */

/**
 * Creates a network-bound resource with caching strategy.
 *
 * @param query Function to query local database
 * @param fetch Function to fetch from network
 * @param saveFetchResult Function to save network response to database
 * @param shouldFetch Predicate to determine if network fetch is needed
 * @param onFetchFailed Optional handler when network fetch fails
 * @return Flow emitting Result with cached then fresh data
 */
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> ResultType,
    crossinline fetch: suspend () -> Response<RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    noinline onFetchFailed: (Exception) -> Unit = { Timber.e(it) }
): Flow<Result<ResultType>> = flow {

    // 1. Emit loading state
    emit(Result.loading())

    try {
        // 2. Query local database
        val localData = query()

        // 3. Check if we should fetch from network
        if (shouldFetch(localData)) {
            try {
                // 4. Fetch from network
                val response = fetch()

                if (response.isSuccessful) {
                    // 5. Save to database
                    val responseBody = response.body()
                    if (responseBody != null) {
                        saveFetchResult(responseBody)
                        
                        // 6. Query updated local data
                        val refreshedData = query()
                        emit(Result.success(refreshedData))
                        
                        Timber.d("Successfully fetched and cached data")
                    } else {
                        // Response body is null but status is successful
                        emit(Result.success(localData))
                        Timber.w("Response body is null despite successful status")
                    }
                } else {
                    // Network request failed with HTTP error
                    val errorState = when (response.code()) {
                        401 -> Exception("Unauthorized")
                        404 -> Exception("Not Found")
                        500 -> Exception("Server Error")
                        else -> Exception("HTTP Error: ${response.code()}")
                    }
                    
                    onFetchFailed(errorState)
                    
                    // Emit cached data if available, otherwise error
                    if (localData != null) {
                        emit(Result.success(localData))
                        Timber.w("Network failed, emitting cached data")
                    } else {
                        emit(Result.failure(errorState))
                        Timber.e(errorState, "Network failed and no cache available")
                    }
                }
            } catch (e: Exception) {
                onFetchFailed(e)
                
                // Emit cached data if available, otherwise error
                if (localData != null) {
                    emit(Result.success(localData))
                    Timber.w(e, "Network exception, emitting cached data")
                } else {
                    emit(Result.failure(e))
                    Timber.e(e, "Network exception and no cache available")
                }
            }
        } else {
            // Use cached data without fetching
            emit(Result.success(localData))
            Timber.d("Using cached data, skip network fetch")
        }
    } catch (e: Exception) {
        // Error querying local database
        emit(Result.failure(e))
        Timber.e(e, "Error querying local database")
    }
}

/**
 * Creates a simpler network-bound resource without local caching.
 *
 * Useful for data that doesn't need local caching.
 *
 * @param fetch Function to fetch from network
 * @return Flow emitting Result with network data
 */
inline fun <ResultType> networkResource(
    crossinline fetch: suspend () -> Response<ResultType>
): Flow<Result<ResultType>> = flow {
    emit(Result.loading())
    
    try {
        val response = fetch()
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                emit(Result.success(body))
            } else {
                emit(Result.failure(Exception("Empty response body")))
            }
        } else {
            val errorState = ExceptionHandler.handle(
                Exception("HTTP ${response.code()}: ${response.message()}")
            )
            emit(Result.failure(Exception(errorState.toString())))
        }
    } catch (e: Exception) {
        emit(Result.failure(e))
        Timber.e(e, "Network request failed")
    }
}

/**
 * Result class representing loading state for UI.
 */
sealed class LoadingResult<T> {
    class Loading<T> : LoadingResult<T>()
    data class Success<T>(val data: T) : LoadingResult<T>()
    data class Error<T>(val exception: Exception) : LoadingResult<T>()

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}
