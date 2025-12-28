package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.CategoryDao
import com.noghre.sod.data.remote.CategoryApi
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi,
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override suspend fun getCategories(): Result<List<Category>> = try {
        Timber.d("Fetching categories from API")
        val response = categoryApi.getCategories()
        
        // Save to local database
        categoryDao.insertAll(response.categories)
        Timber.d("Categories saved to local DB: ${response.categories.size}")
        
        Result.Success(response.categories)
    } catch (e: Exception) {
        Timber.e("Error fetching categories: ${e.message}")
        // Fallback to local data
        try {
            val localCategories = categoryDao.getAll()
            if (localCategories.isNotEmpty()) {
                Timber.d("Returning local categories: ${localCategories.size}")
                Result.Success(localCategories)
            } else {
                Result.Error(AppException.NetworkException("No categories available"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local categories: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getCategoryById(id: String): Result<Category> = try {
        Timber.d("Fetching category by ID: $id")
        val response = categoryApi.getCategoryById(id)
        
        // Save to local database
        categoryDao.insert(response)
        Timber.d("Category saved to local DB: ${response.name}")
        
        Result.Success(response)
    } catch (e: Exception) {
        Timber.e("Error fetching category: ${e.message}")
        // Fallback to local data
        try {
            val localCategory = categoryDao.getById(id)
            if (localCategory != null) {
                Timber.d("Returning local category: ${localCategory.name}")
                Result.Success(localCategory)
            } else {
                Result.Error(AppException.NetworkException("Category not found"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local category: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getProductsByCategory(categoryId: String): Result<List<String>> = try {
        Timber.d("Fetching products for category: $categoryId")
        val response = categoryApi.getProductsByCategory(categoryId)
        
        Timber.d("Products fetched: ${response.productIds.size}")
        Result.Success(response.productIds)
    } catch (e: Exception) {
        Timber.e("Error fetching products for category: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Failed to fetch products"))
    }
    
    override fun observeCategories(): Flow<List<Category>> {
        Timber.d("Observing categories")
        return categoryDao.observeAll()
    }
    
    override suspend fun searchCategories(query: String): Result<List<Category>> = try {
        Timber.d("Searching categories: $query")
        val results = categoryDao.search("%$query%")
        
        if (results.isEmpty()) {
            Timber.d("No categories found for query: $query")
            Result.Success(emptyList())
        } else {
            Timber.d("Found ${results.size} categories for: $query")
            Result.Success(results)
        }
    } catch (e: Exception) {
        Timber.e("Error searching categories: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Search failed"))
    }
}
