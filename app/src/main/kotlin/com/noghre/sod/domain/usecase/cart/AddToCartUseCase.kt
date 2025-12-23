package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.CartRepository
import javax.inject.Inject

data class AddToCartParams(
    val productId: String,
    val product: Product,
    val quantity: Int,
)

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) : UseCase<AddToCartParams, CartItem>() {

    override suspend fun execute(parameters: AddToCartParams): Result<CartItem> {
        return cartRepository.addToCart(parameters.productId, parameters.product, parameters.quantity)
    }
}
