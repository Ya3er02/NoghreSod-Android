package com.noghre.sod.domain.usecase.product

import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilters
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for fetching products with filtering, sorting, and pagination.
 * 
 * Separates business logic from data layer.
 * Handles pagination, filtering, and sorting operations.
 * 
 * @author NoghreSod Team
 * @version 2.0.0
 */
@Singleton
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    /**
     * Get products with optional filtering, sorting, and pagination.
     * 
     * @param filters ProductFilters containing all filter criteria
     * @param page Page number for pagination (1-based)
     * @param pageSize Number of products per page
     * @return Flow emitting Result<List<Product>>
     */
    operator fun invoke(
        filters: ProductFilters = ProductFilters(),
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>> {
        return productRepository.getProducts(
            page = page,
            pageSize = pageSize,
            query = filters.searchQuery,
            categoryId = filters.category,
            sortBy = filters.sortBy?.name?.lowercase() ?: "popular"
        ).flowOn(dispatcher)
    }

    /**
     * Get products with direct parameters (alternative method).
     * 
     * @param page Page number for pagination
     * @param pageSize Number of products per page
     * @param query Search query string
     * @param categoryId Category filter
     * @param sortBy Sort option
     * @return Flow emitting Result<List<Product>>
     */
    fun getProductsByParams(
        page: Int = 1,
        pageSize: Int = 20,
        query: String? = null,
        categoryId: String? = null,
        sortBy: String? = "popular"
    ): Flow<Result<List<Product>>> {
        return productRepository.getProducts(
            page = page,
            pageSize = pageSize,
            query = query,
            categoryId = categoryId,
            sortBy = sortBy
        ).flowOn(dispatcher)
    }
}
