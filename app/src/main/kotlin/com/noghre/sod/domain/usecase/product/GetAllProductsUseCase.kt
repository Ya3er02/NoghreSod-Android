package com.noghre.sod.domain.usecase.product

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.FlowUseCase
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Parameters for getting products
 */
data class GetProductsParams(
    val page: Int = 0,
    val pageSize: Int = 20,
)

/**
 * Use case for fetching all products with pagination
 */
class GetAllProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) : FlowUseCase<GetProductsParams, List<ProductSummary>>() {

    override fun execute(parameters: GetProductsParams): Flow<Result<List<ProductSummary>>> {
        return productRepository.getAllProducts(parameters.page, parameters.pageSize)
    }
}
