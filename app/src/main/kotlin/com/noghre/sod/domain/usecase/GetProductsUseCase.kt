package com.noghre.sod.domain.usecase

import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.domain.model.Product
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for fetching products with optional pagination.
 *
 * This use case handles the business logic of retrieving products from the repository,
 * applying any necessary filtering or sorting, and returning the results to the presentation layer.
 *
 * @property repository The product repository for data access
 *
 * @sample
 * ```kotlin
 * val useCase = GetProductsUseCase(repository)
 * val products = useCase(page = 1, pageSize = 20)
 * ```
 *
 * @throws Exception if the repository call fails
 *
 * @author NoghreSod Team
 * @version 1.0
 */
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    /**
     * Execute the use case to get products.
     *
     * @param page The page number for pagination (default: 1)
     * @param pageSize The number of items per page (default: 20)
     * @return List of products for the specified page
     * @throws Exception if the data fetch fails
     *
     * @sample
     * ```kotlin
     * try {
     *     val products = useCase.invoke(page = 1, pageSize = 10)
     *     // Handle success
     * } catch (e: Exception) {
     *     // Handle error
     * }
     * ```
     */
    suspend fun invoke(page: Int = 1, pageSize: Int = 20): List<Product> {
        Timber.d("Fetching products - page: $page, pageSize: $pageSize")
        return try {
            val products = repository.getProducts(page, pageSize)
            Timber.d("Successfully fetched ${products.size} products")
            products
        } catch (e: Exception) {
            Timber.e(e, "Error fetching products")
            throw e
        }
    }
}
