package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.core.network.NetworkMonitor
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * 📕 Category Repository Implementation
 * 
 * Handles category operations with caching and comprehensive error handling.
 * Uses offline-first strategy with AppError classification.
 */
class CategoryRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val categoryDao: CategoryDao,
    private val networkMonitor: NetworkMonitor,
    private val exceptionHandler: GlobalExceptionHandler
) : CategoryRepository {

    /**
     * 📋 Get all categories
     */
    override fun getCategories(): Flow<Result<List<Category>>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[CATEGORY] Getting categories")

            // Try to emit cached categories first
            try {
                val cachedFlow = categoryDao.getAllCategories()
                cachedFlow.collect { cached ->
                    if (cached.isNotEmpty()) {
                        Timber.d("[CATEGORY] Cached categories found: ${cached.size}")
                        emit(Result.Success(cached.map { it.toDomain() }))
                    }
                }
            } catch (e: Exception) {
                Timber.w(e, "[CATEGORY] Error reading cache")
            }

            // Fetch from API if online
            if (networkMonitor.isOnline) {
                try {
                    val response = api.getCategories()
                    
                    if (response.isSuccessful) {
                        if (response.data != null) {
                            // Save to cache
                            try {
                                val entities = response.data.map { dto ->
                                    CategoryEntity(
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
                                Timber.d("[CATEGORY] Categories cached: ${entities.size}")
                            } catch (e: Exception) {
                                Timber.w(e, "[CATEGORY] Error saving to cache")
                            }
                            
                            val categories = response.data.map { it.toDomain() }
                            Timber.d("[CATEGORY] Categories fetched: ${categories.size}")
                            emit(Result.Success(categories))
                        } else {
                            Timber.w("[CATEGORY] Categories response is empty")
                            emit(Result.Error(AppError.Network(
                                message = "پاسخ خالی است",
                                statusCode = 200
                            )))
                        }
                    } else {
                        Timber.w("[CATEGORY] Get categories failed: ${response.code()}")
                        emit(Result.Error(AppError.Network(
                            message = response.message ?: "تاب آبی دسته‌بندی ها ناموفق",
                            statusCode = response.code()
                        )))
                    }
                } catch (e: java.net.UnknownHostException) {
                    Timber.e(e, "[CATEGORY] Network error")
                    emit(Result.Error(AppError.Network(
                        message = "بدون دسترسی به اینترنت",
                        statusCode = null
                    )))
                } catch (e: java.net.SocketTimeoutException) {
                    Timber.e(e, "[CATEGORY] Timeout")
                    emit(Result.Error(AppError.Network(
                        message = "زمان اتصال تمام شد",
                        statusCode = null
                    )))
                }
            } else {
                Timber.d("[CATEGORY] Offline mode")
            }
        } catch (e: Exception) {
            Timber.e(e, "[CATEGORY] Get categories error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 🔍 Get category by ID
     */
    override fun getCategoryById(id: String): Flow<Result<Category>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[CATEGORY] Getting category: $id")
            
            if (id.isBlank()) {
                Timber.w("[CATEGORY] Invalid category ID")
                emit(Result.Error(AppError.Validation(
                    message = "شناسه دسته‌بندی نامعتبر",
                    field = "categoryId"
                )))
                return@flow
            }
            
            // Try cache first
            try {
                val cached = categoryDao.getCategoryById(id)
                if (cached != null) {
                    Timber.d("[CATEGORY] Found in cache: ${cached.name}")
                    emit(Result.Success(cached.toDomain()))
                    return@flow
                }
            } catch (e: Exception) {
                Timber.w(e, "[CATEGORY] Error reading from cache")
            }

            if (networkMonitor.isOnline) {
                try {
                    val response = api.getCategoryById(id)
                    
                    if (response.isSuccessful) {
                        if (response.data != null) {
                            Timber.d("[CATEGORY] Category fetched: ${response.data.name}")
                            
                            // Save to cache
                            try {
                                val entity = CategoryEntity(
                                    id = response.data.id,
                                    name = response.data.name,
                                    nameEn = response.data.nameEn,
                                    description = response.data.description,
                                    iconUrl = response.data.iconUrl,
                                    parentId = response.data.parentId,
                                    isActive = response.data.isActive,
                                    displayOrder = response.data.displayOrder,
                                    productCount = response.data.productCount
                                )
                                categoryDao.insertCategory(entity)
                                Timber.d("[CATEGORY] Category cached")
                            } catch (e: Exception) {
                                Timber.w(e, "[CATEGORY] Error saving category to cache")
                            }
                            
                            emit(Result.Success(response.data.toDomain()))
                        } else {
                            Timber.w("[CATEGORY] Category response is empty")
                            emit(Result.Error(AppError.Network(
                                message = "دسته‌بندی یافت نشد",
                                statusCode = 200
                            )))
                        }
                    } else {
                        Timber.w("[CATEGORY] Get category failed: ${response.code()}")
                        emit(Result.Error(when (response.code()) {
                            404 -> AppError.Network(
                                message = "دسته‌بندی یافت نشد",
                                statusCode = 404
                            )
                            else -> AppError.Network(
                                message = response.message ?: "بارگیری دسته‌بندی ناموفق",
                                statusCode = response.code()
                            )
                        }))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "[CATEGORY] Get category error")
                    emit(Result.Error(exceptionHandler.handleException(e)))
                }
            } else {
                Timber.w("[CATEGORY] Offline and not in cache: $id")
                emit(Result.Error(AppError.Network(
                    message = "بدون دسترسی به اینترنت",
                    statusCode = null
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[CATEGORY] Get category error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }.flowOn(Dispatchers.IO)

    // ============================================
    // 🔄 Mapper Functions
    // ============================================

    private fun com.noghre.sod.data.local.entity.CategoryEntity.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            iconUrl = iconUrl,
            parentId = parentId,
            isActive = isActive,
            displayOrder = displayOrder,
            productCount = productCount,
        )
    }

    private fun com.noghre.sod.data.remote.dto.CategoryDto.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            iconUrl = iconUrl,
            parentId = parentId,
            isActive = isActive,
            displayOrder = displayOrder,
            productCount = productCount,
        )
    }
}