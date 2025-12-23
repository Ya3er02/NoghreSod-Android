package com.noghre.sod.domain.usecase.product

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.NoParamsUseCase
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeaturedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) : NoParamsUseCase<List<ProductSummary>>() {

    override fun execute(): Flow<Result<List<ProductSummary>>> {
        return productRepository.getFeaturedProducts()
    }
}
