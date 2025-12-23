package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : UseCase<AddToCartUseCase.Params, Unit>() {

    data class Params(
        val productId: String,
        val quantity: Int = 1
    )

    override suspend fun execute(params: Params) {
        if (params.quantity <= 0) {
            throw IllegalArgumentException("تعداد باید بیشتر از 0 باشد")
        }
        
        cartRepository.addToCart(params.productId, params.quantity)
    }
}
