package com.noghre.sod.domain.usecase.base

import com.noghre.sod.domain.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Base class for use cases that return a single result.
 * 
 * @param P The type of parameters the use case accepts
 * @param R The type of result the use case returns
 * @param dispatcher The CoroutineDispatcher to run the use case on
 */
abstract class UseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    
    /**
     * Execute the use case with given parameters.
     * Automatically wraps result in Result.Success or Result.Error.
     */
    suspend operator fun invoke(params: P): Result<R> = withContext(dispatcher) {
        try {
            Result.Success(execute(params))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Implement this method to define the use case logic.
     * Exceptions thrown here will be automatically caught and wrapped in Result.Error.
     */
    @Throws(Exception::class)
    protected abstract suspend fun execute(params: P): R
}

/**
 * Base class for use cases that return a Flow.
 * 
 * @param P The type of parameters the use case accepts
 * @param R The type of result the Flow emits
 * @param dispatcher The CoroutineDispatcher to run the use case on
 */
abstract class FlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    
    /**
     * Execute the use case with given parameters and return a Flow.
     * Automatically catches exceptions and emits Result.Error.
     */
    operator fun invoke(params: P): Flow<Result<R>> = execute(params)
        .catch { emit(Result.Error(it as Exception)) }
        .flowOn(dispatcher)
    
    /**
     * Implement this method to define the use case logic that returns a Flow.
     */
    protected abstract fun execute(params: P): Flow<Result<R>>
}

/**
 * Use case with no parameters.
 * 
 * @param R The type of result the use case returns
 * @param dispatcher The CoroutineDispatcher to run the use case on
 */
abstract class NoParamsUseCase<out R>(private val dispatcher: CoroutineDispatcher) {
    
    /**
     * Execute the use case without parameters.
     */
    suspend operator fun invoke(): Result<R> = withContext(dispatcher) {
        try {
            Result.Success(execute())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Implement this method to define the use case logic.
     */
    @Throws(Exception::class)
    protected abstract suspend fun execute(): R
}
