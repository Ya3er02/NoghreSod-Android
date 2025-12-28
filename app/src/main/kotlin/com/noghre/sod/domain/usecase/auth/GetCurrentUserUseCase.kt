package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Retrieves the currently logged-in user.
 * Part of Authentication domain logic.
 * 
 * Business Rules:
 * - Returns current user if authenticated
 * - Throws error if not authenticated
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, User>(dispatcher) {
    
    override suspend fun execute(params: Unit): User {
        return authRepository.getCurrentUser()
            .getOrThrow()
    }
}

/**
 * Checks if user is currently authenticated.
 * Part of Authentication domain logic.
 */
class IsAuthenticatedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : UseCase<Unit, Boolean>(dispatcher) {
    
    override suspend fun execute(params: Unit): Boolean {
        return authRepository.isAuthenticated()
    }
}

/**
 * Refreshes authentication token if needed.
 * Part of Authentication domain logic.
 */
class RefreshAuthTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, String>(dispatcher) {
    
    override suspend fun execute(params: Unit): String {
        return authRepository.refreshToken()
            .getOrThrow()
    }
}
