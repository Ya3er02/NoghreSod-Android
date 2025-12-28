package com.noghre.sod.domain.usecase.search

import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.SearchRepository
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Searches for products by query.
 * Part of Search domain logic.
 * 
 * Business Rules:
 * - Query must not be empty
 * - Returns results sorted by relevance
 * - Handles typos gracefully
 */
class SearchProductsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<SearchProductsUseCase.Params, List<Product>>(dispatcher) {
    
    data class Params(
        val query: String,
        val page: Int = 1,
        val pageSize: Int = 20,
        val filters: Map<String, String> = emptyMap()
    )
    
    override suspend fun execute(params: Params): List<Product> {
        val query = params.query.trim()
        
        if (query.isBlank()) {
            return emptyList()
        }
        
        return searchRepository.searchProducts(
            query = query,
            page = params.page,
            pageSize = params.pageSize,
            filters = params.filters
        ).getOrThrow()
    }
}

/**
 * Retrieves user's search history.
 * Part of Search domain logic.
 */
class GetSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Int, List<String>>(dispatcher) {
    
    /**
     * @param limit Maximum number of history items to retrieve
     */
    override suspend fun execute(params: Int): List<String> {
        return searchRepository.getSearchHistory(params)
            .getOrThrow()
    }
}

/**
 * Saves a search query to history.
 * Part of Search domain logic.
 * 
 * Business Rules:
 * - Query must not be empty
 * - Duplicate queries are not saved
 * - Only keeps recent queries (configurable)
 */
class SaveSearchQueryUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Unit>(dispatcher) {
    
    override suspend fun execute(params: String): Unit {
        val query = params.trim()
        
        if (query.isBlank()) {
            return
        }
        
        return searchRepository.saveSearchQuery(query)
            .getOrThrow()
    }
}

/**
 * Clears entire search history.
 * Part of Search domain logic.
 */
class ClearSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, Unit>(dispatcher) {
    
    override suspend fun execute(params: Unit): Unit {
        return searchRepository.clearSearchHistory()
            .getOrThrow()
    }
}
