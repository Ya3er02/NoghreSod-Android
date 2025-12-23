package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.Coupon
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ApplyCouponUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : UseCase<ApplyCouponUseCase.Params, Coupon>() {

    data class Params(val couponCode: String)

    override suspend fun execute(params: Params): Coupon {
        if (params.couponCode.isEmpty()) {
            throw IllegalArgumentException("کد تخفیف نوشته شود")
        }
        
        return cartRepository.applyCoupon(params.couponCode)
    }
}
