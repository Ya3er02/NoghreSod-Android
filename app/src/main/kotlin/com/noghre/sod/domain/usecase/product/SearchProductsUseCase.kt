package com.noghre.sod.domain.usecase.product

import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for searching products with debouncing.
 * 
 * Implements search functionality with minimum character requirement
 * and debouncing to optimize API calls and user experience.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Singleton
class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    companion object {
        private const val MIN_SEARCH_LENGTH = 2
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    /**
     * Search products with debouncing.
     * 
     * Requires minimum 2 characters to prevent excessive searches.
     * Debounces for 300ms to wait for user to finish typing.
     * 
     * @param query Search query string
     * @return Flow emitting Result<List<Product>>
     */
    operator fun invoke(query: String): Flow<Result<List<Product>>> {
        // Return empty result immediately if query is too short
        if (query.length < MIN_SEARCH_LENGTH) {
            return flowOf(Result.Success(emptyList()))
        }
        
        return productRepository.searchProducts(query.trim())
            .debounce(SEARCH_DEBOUNCE_MS)
            .flowOn(dispatcher)
    }

    /**
     * Search products by category.
     * 
     * @param categoryId Category identifier
     * @return Flow emitting Result<List<Product>>
     */
    fun searchByCategory(categoryId: String): Flow<Result<List<Product>>> {
        return productRepository.getProductsByCategory(categoryId)
            .flowOn(dispatcher)
    }

    /**
     * Search products with query and category filter.
     * 
     * @param query Search query
     * @param categoryId Category filter
     * @return Flow emitting Result<List<Product>>
     */
    fun searchWithFilter(
        query: String,
        categoryId: String? = null
    ): Flow<Result<List<Product>>> {
        if (query.length < MIN_SEARCH_LENGTH) {
            return flowOf(Result.Success(emptyList()))
        }
        
        return productRepository.searchProducts(
            query = query.trim(),
            categoryId = categoryId
        ).debounce(SEARCH_DEBOUNCE_MS)
            .flowOn(dispatcher)
    }
}
