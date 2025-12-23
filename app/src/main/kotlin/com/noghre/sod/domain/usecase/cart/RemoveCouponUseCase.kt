package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RemoveCouponUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : UseCase<Unit, Unit>() {

    override suspend fun execute(params: Unit) {
        cartRepository.removeCoupon()
    }
}
