package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class CouponResult(
    val success: Boolean,
    val discountAmount: Double = 0.0,
    val discountPercent: Int = 0,
    val message: String? = null
)

@ViewModelScoped
class ApplyCouponUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : UseCase<ApplyCouponUseCase.Params, CouponResult>() {

    data class Params(val couponCode: String)

    override suspend fun execute(params: Params): CouponResult {
        if (params.couponCode.isEmpty()) {
            return CouponResult(false, message = "لطفا کد تخفیف را وارد نمایید")
        }
        return cartRepository.applyCoupon(params.couponCode)
    }
}
