package com.noghre.sod.utils.extensions

import com.noghre.sod.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Wraps a Flow in Result states (Loading, Success, Error).
 *
 * Example:
 * ```
 * val productsFlow: Flow<Result<List<Product>>> = productRepository
 *     .getProducts()
 *     .asResult()
 * ```
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = this
    .map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it as Exception)) }

/**
 * Transforms the success value in a Flow<Result>.
 *
 * Example:
 * ```
 * val productNamesFlow = productFlow
 *     .mapResult { products -> products.map { it.name } }
 * ```
 */
fun <T, R> Flow<Result<T>>.mapResult(transform: (T) -> R): Flow<Result<R>> =
    this.map { result -> result.map(transform) }
