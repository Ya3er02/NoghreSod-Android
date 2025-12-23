package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.mapper.CategoryMapper.toDomain
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Category Repository Implementation with caching.
 */
class CategoryRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val categoryDao: CategoryDao,
    private val networkMonitor: NetworkMonitor
) : CategoryRepository {

    override fun getCategories(): Flow<Result<List<Category>>> = flow {
        try {
            emit(Result.Loading)

            // Try to emit cached categories
            val cachedFlow = categoryDao.getAllCategories()
            cachedFlow.collect { cached ->
                if (cached.isNotEmpty()) {
                    emit(Result.Success(cached.map { it.toDomain() }))
                }
            }

            // Fetch from API
            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.getCategories()
                    if (response.success && response.data != null) {
                        val entities = response.data.map { dto ->
                            com.noghre.sod.data.local.entity.CategoryEntity(
                                id = dto.id,
                                name = dto.name,
                                nameEn = dto.nameEn,
                                description = dto.description,
                                iconUrl = dto.iconUrl,
                                parentId = dto.parentId,
                                isActive = dto.isActive,
                                displayOrder = dto.displayOrder,
                                productCount = dto.productCount
                            )
                        }
                        categoryDao.insertCategories(entities)
                        emit(Result.Success(response.data.map { it.toDomain() }))
                    } else {
                        emit(Result.Error(response.message ?: "Failed to fetch"))
                    }
                } catch (e: ApiException) {
                    Timber.e(e, "API error fetching categories")
                    emit(Result.Error(e.message ?: "Network error"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting categories")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getCategoryById(id: String): Flow<Result<Category>> = flow {
        try {
            emit(Result.Loading)

            val response = api.getCategoryById(id)
            if (response.success && response.data != null) {
                emit(Result.Success(response.data.toDomain()))
            } else {
                emit(Result.Error(response.message ?: "Not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting category: $id")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)
}
