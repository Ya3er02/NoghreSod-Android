package com.noghre.sod.domain.usecase

import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for fetching products.
 * 
 * Separates business logic from data layer.
 * Handles pagination, filtering, and sorting.
 */
@Singleton
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    /**
     * Get products with optional filtering and sorting.
     */
    operator fun invoke(
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