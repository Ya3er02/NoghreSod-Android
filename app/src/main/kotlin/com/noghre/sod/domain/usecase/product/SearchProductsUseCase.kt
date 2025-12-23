package com.noghre.sod.domain.usecase.product

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.FlowUseCase
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SearchProductsParams(
    val query: String,
    val page: Int = 0,
    val pageSize: Int = 20,
)

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) : FlowUseCase<SearchProductsParams, List<ProductSummary>>() {

    override fun execute(parameters: SearchProductsParams): Flow<Result<List<ProductSummary>>> {
        return productRepository.searchProducts(parameters.query, parameters.page, parameters.pageSize)
    }
}
