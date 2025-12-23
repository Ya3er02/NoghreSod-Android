package com.noghre.sod.data.repository

import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UserRepositoryImpl @Inject constructor() : UserRepository {
    override suspend fun updateProfile(fullName: String, email: String?, avatarUrl: String?): User {
        throw NotImplementedError()
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) {
        // TODO: Implement
    }

    override suspend fun getCurrentUser(): User? {
        return null
    }

    override suspend fun deleteAccount(password: String) {
        // TODO: Implement
    }
}
