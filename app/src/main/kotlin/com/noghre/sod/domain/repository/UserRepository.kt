package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.User

interface UserRepository {
    suspend fun updateProfile(fullName: String, email: String?, avatarUrl: String?): User
    suspend fun changePassword(currentPassword: String, newPassword: String)
    suspend fun getCurrentUser(): User?
    suspend fun deleteAccount(password: String)
}
