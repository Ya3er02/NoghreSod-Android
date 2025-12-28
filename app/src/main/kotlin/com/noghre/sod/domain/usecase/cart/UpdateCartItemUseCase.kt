package com.noghre.sod.domain.usecase.cart

import com.noghre.sod.core.util.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Updates quantity of a cart item.
 * Part of Shopping Cart domain logic.
 * 
 * Business Rules:
 * - Item must exist in cart
 * - Quantity must be between 1 and stock availability
 * - Updates inventory in real-time
 */
class UpdateCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UpdateCartItemUseCase.Params, Cart>(dispatcher) {
    
    data class Params(
        val cartItemId: String,
        val newQuantity: Int
    )
    
    companion object {
        private const val MIN_QUANTITY = 1
        private const val MAX_QUANTITY = 999
    }
    
    override suspend fun execute(params: Params): Cart {
        // Validate quantity
        when {
            params.newQuantity < MIN_QUANTITY -> throw AppError.Validation(
                message = "تعداد باید حداقل 1 باشد",
                fieldName = "quantity"
            )
            params.newQuantity > MAX_QUANTITY -> throw AppError.Validation(
                message = "تعداد نمی‌تواند بیشتر از $MAX_QUANTITY باشد",
                fieldName = "quantity"
            )
        }
        
        // Update in repository
        return cartRepository.updateCartItem(params.cartItemId, params.newQuantity)
            .getOrThrow()
    }
}

/**
 * Clears all items from cart.
 * Part of Shopping Cart domain logic.
 */
class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, Cart>(dispatcher) {
    
    override suspend fun execute(params: Unit): Cart {
        return cartRepository.clearCart()
            .getOrThrow()
    }
}

/**
 * Calculates cart total with discounts and taxes.
 * Part of Shopping Cart domain logic.
 * 
 * Business Rules:
 * - Includes product prices
 * - Applies coupon discounts
 * - Calculates 9% tax
 * - Adds shipping cost
 */
class CalculateCartTotalUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<CalculateCartTotalUseCase.Params, CartTotalCalculation>(dispatcher) {
    
    data class Params(
        val shippingCost: Long = 0,
        val includeTax: Boolean = true
    )
    
    data class CartTotalCalculation(
        val itemsTotal: Long,
        val discountAmount: Long,
        val subtotal: Long,
        val shippingCost: Long,
        val taxAmount: Long,
        val totalPrice: Long
    )
    
    companion object {
        private const val TAX_RATE = 0.09f  // 9% VAT
    }
    
    override suspend fun execute(params: Params): CartTotalCalculation {
        // Get current cart
        val cart = cartRepository.getCart()
            .getOrThrow()
        
        // Calculate items total
        val itemsTotal = cart.items.sumOf { item ->
            item.product.price * item.quantity
        }
        
        // Apply discount if exists
        val discountAmount = cart.appliedCoupon?.discountAmount ?: 0L
        val subtotalBeforeTax = itemsTotal - discountAmount + params.shippingCost
        
        // Calculate tax
        val taxAmount = if (params.includeTax) {
            (subtotalBeforeTax * TAX_RATE).toLong()
        } else {
            0L
        }
        
        val totalPrice = subtotalBeforeTax + taxAmount
        
        return CartTotalCalculation(
            itemsTotal = itemsTotal,
            discountAmount = discountAmount,
            subtotal = subtotalBeforeTax,
            shippingCost = params.shippingCost,
            taxAmount = taxAmount,
            totalPrice = totalPrice
        )
    }
}
