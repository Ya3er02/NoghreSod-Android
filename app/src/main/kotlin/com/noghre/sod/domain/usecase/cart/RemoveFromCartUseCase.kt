package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : UseCase<RemoveFromCartUseCase.Params, Unit>() {

    data class Params(val productId: String)

    override suspend fun execute(params: Params) {
        cartRepository.removeFromCart(params.productId)
    }
}
