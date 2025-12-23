package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.NoParamsUseCase
import com.noghre.sod.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) : NoParamsUseCase<Unit>() {

    override fun execute(): Flow<Result<Unit>> {
        return object : Flow<Result<Unit>> {
            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<Result<Unit>>) {
                try {
                    collector.emit(Result.Loading)
                    val result = authRepository.logout()
                    collector.emit(result)
                } catch (e: Throwable) {
                    collector.emit(Result.Error(e))
                }
            }
        }
    }
}
