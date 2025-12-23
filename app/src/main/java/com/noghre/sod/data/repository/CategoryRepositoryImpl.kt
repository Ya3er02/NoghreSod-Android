package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.mapper.CategoryMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException

class CategoryRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val categoryDao: CategoryDao,
    private val networkMonitor: NetworkMonitor,
    private val mapper: CategoryMapper
) : CategoryRepository {

    override fun getCategories(): Flow<Result<List<CategoryDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.getCategories()
                        if (response.success && response.data != null) {
                            categoryDao.insertCategories(mapper.toEntities(response.data))
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch categories")
                        emitCached()
                    }
                } else {
                    emitCached()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getCategories")
            emit(Result.Error(handleException(e)))
        }
    }

    private suspend fun emitCached() {
        categoryDao.getAllCategories().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(mapper.toDtos(entities)))
            }
        }
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is IOException -> ApiException.NetworkError()
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

interface CategoryRepository {
    fun getCategories(): Flow<Result<List<CategoryDto>>>
}