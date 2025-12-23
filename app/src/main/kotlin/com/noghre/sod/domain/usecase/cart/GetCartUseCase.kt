package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.NoParamsUseCase
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) : NoParamsUseCase<Cart>() {

    override fun execute(): Flow<Result<Cart>> {
        return cartRepository.getCart()
    }
}
