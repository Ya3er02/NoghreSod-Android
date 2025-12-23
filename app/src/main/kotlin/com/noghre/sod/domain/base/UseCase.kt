package com.noghre.sod.domain.base

/**
 * Base class for all use cases in the domain layer
 * @param P - Parameter type for the use case
 * @param R - Return type from the use case
 */
abstract class UseCase<P, R> {
    abstract suspend fun execute(params: P): R

    suspend operator fun invoke(params: P): R = execute(params)
}

/**
 * Extension for use cases with no parameters
 */
abstract class NoParamUseCase<R> : UseCase<Unit, R>() {
    final override suspend fun execute(params: Unit): R = execute()

    abstract suspend fun execute(): R

    suspend operator fun invoke(): R = execute()
}
