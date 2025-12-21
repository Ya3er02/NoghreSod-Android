package com.noghre.sod.domain.usecase.base

import com.noghre.sod.domain.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): Result<R> = withContext(dispatcher) {
        try {
            Result.Success(execute(params))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Throws(Exception::class)
    protected abstract suspend fun execute(params: P): R
}

abstract class FlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    operator fun invoke(params: P): Flow<Result<R>> = execute(params)
        .catch { emit(Result.Error(it as Exception)) }
        .flowOn(dispatcher)

    protected abstract fun execute(params: P): Flow<Result<R>>
}

abstract class NoParamsUseCase<out R>(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): Result<R> = withContext(dispatcher) {
        try {
            Result.Success(execute())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Throws(Exception::class)
    protected abstract suspend fun execute(): R
}
