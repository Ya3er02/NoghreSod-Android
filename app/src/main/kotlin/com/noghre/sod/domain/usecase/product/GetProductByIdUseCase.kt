package com.noghre.sod.domain.usecase.product

import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.usecase.base.FlowUseCase
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fetches single product by ID.
 * Part of Product Catalog domain logic.
 */
class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Product>(dispatcher) {
    
    override suspend fun execute(params: String): Product {
        return productRepository.getProductById(params)
            .getOrThrow()
    }
}

/**
 * Observes favorite products as a Flow.
 * Part of Favorites domain logic.
 */
class ObserveFavoritesUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowUseCase<Unit, List<Product>>(dispatcher) {
    
    override fun execute(params: Unit): Flow<List<Product>> {
        return productRepository.observeFavorites()
    }
}

/**
 * Toggles favorite status of a product.
 * Part of Favorites domain logic.
 * 
 * Business Rules:
 * - If product is already favorite, removes it
 * - If product is not favorite, adds it
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Boolean>(dispatcher) {
    
    override suspend fun execute(params: String): Boolean {
        return productRepository.toggleFavorite(params)
            .getOrThrow()
    }
}

/**
 * Gets all favorite products.
 * Part of Favorites domain logic.
 */
class GetFavoritesUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, List<Product>>(dispatcher) {
    
    override suspend fun execute(params: Unit): List<Product> {
        return productRepository.getFavorites()
            .getOrThrow()
    }
}

/**
 * Observes all products with real-time updates.
 * Part of Product Catalog domain logic.
 */
class ObserveProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowUseCase<Unit, List<Product>>(dispatcher) {
    
    override fun execute(params: Unit): Flow<List<Product>> {
        return productRepository.observeProducts()
    }
}
