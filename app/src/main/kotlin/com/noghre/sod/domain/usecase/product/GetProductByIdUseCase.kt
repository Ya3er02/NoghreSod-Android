package com.noghre.sod.domain.usecase.product

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.FlowUseCase
import com.noghre.sod.domain.model.ProductDetail
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching product details by ID
 * 
 * @param productRepository Repository for product data
 */
class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) : FlowUseCase<String, ProductDetail>() {

    override fun execute(parameters: String): Flow<Result<ProductDetail>> {
        return productRepository.getProductById(parameters)
    }
}
