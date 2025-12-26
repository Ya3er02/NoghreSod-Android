package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.AddToCartUseCase
import com.noghre.sod.domain.usecase.GetCartItemsUseCase
import com.noghre.sod.domain.usecase.RemoveFromCartUseCase
import com.noghre.sod.domain.usecase.UpdateCartItemQuantityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CartViewModel.
 *
 * Tests the following scenarios:
 * - Loading cart items
 * - Adding items to cart
 * - Removing items from cart
 * - Updating item quantities
 * - Calculating total price
 * - Handling cart errors
 * - Coupon application
 *
 * @author Test Suite
 */
@ExperimentalCoroutinesApi
class CartViewModelTest {
    
    private lateinit var viewModel: CartViewModel
    private lateinit var getCartItemsUseCase: GetCartItemsUseCase
    private lateinit var addToCartUseCase: AddToCartUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase
    private lateinit var updateQuantityUseCase: UpdateCartItemQuantityUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    // Sample test data
    private val sampleProduct = Product(
        id = "1",
        name = "Silver Ring",
        price = 150000f,
        image = "https://example.com/ring.jpg",
        category = "Rings",
        description = "Beautiful silver ring"
    )
    
    private val sampleCartItems = listOf(
        CartItem(
            id = "1",
            product = sampleProduct,
            quantity = 2,
            totalPrice = 300000f
        ),
        CartItem(
            id = "2",
            product = sampleProduct.copy(id = "2", name = "Silver Necklace", price = 250000f),
            quantity = 1,
            totalPrice = 250000f
        )
    )
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCartItemsUseCase = mockk()
        addToCartUseCase = mockk()
        removeFromCartUseCase = mockk()
        updateQuantityUseCase = mockk()
        
        viewModel = CartViewModel(
            getCartItemsUseCase = getCartItemsUseCase,
            addToCartUseCase = addToCartUseCase,
            removeFromCartUseCase = removeFromCartUseCase,
            updateQuantityUseCase = updateQuantityUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }
    
    @Test
    fun `loadCartItems should update state with success`() = runTest {
        // Arrange
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(sampleCartItems)
        )
        
        // Act
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(null, uiState.error)
        assertEquals(sampleCartItems, uiState.cartItems)
        assertEquals(2, uiState.cartItems.size)
    }
    
    @Test
    fun `addToCart should add item to cart`() = runTest {
        // Arrange
        coEvery { addToCartUseCase(sampleProduct, 1) } returns NetworkResult.Success(
            CartItem(
                id = "3",
                product = sampleProduct,
                quantity = 1,
                totalPrice = 150000f
            )
        )
        
        // Act
        viewModel.addToCart(sampleProduct, quantity = 1)
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(null, uiState.error)
        coVerify { addToCartUseCase(sampleProduct, 1) }
    }
    
    @Test
    fun `removeFromCart should remove item from cart`() = runTest {
        // Arrange
        coEvery { removeFromCartUseCase("1") } returns NetworkResult.Success(Unit)
        
        // Act
        viewModel.removeFromCart("1")
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(null, uiState.error)
        coVerify { removeFromCartUseCase("1") }
    }
    
    @Test
    fun `updateQuantity should update item quantity`() = runTest {
        // Arrange
        coEvery { updateQuantityUseCase("1", 5) } returns NetworkResult.Success(
            CartItem(
                id = "1",
                product = sampleProduct,
                quantity = 5,
                totalPrice = 750000f
            )
        )
        
        // Act
        viewModel.updateQuantity("1", newQuantity = 5)
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(null, uiState.error)
        coVerify { updateQuantityUseCase("1", 5) }
    }
    
    @Test
    fun `getTotalPrice should calculate correct total`() = runTest {
        // Arrange
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Success(sampleCartItems)
        )
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Act
        val totalPrice = viewModel.getTotalPrice()
        
        // Assert
        assertEquals(550000f, totalPrice)  // 300000 + 250000
    }
    
    @Test
    fun `getItemCount should return correct count`() = runTest {
        // Arrange
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Success(sampleCartItems)
        )
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Act
        val itemCount = viewModel.getItemCount()
        
        // Assert
        assertEquals(3, itemCount)  // 2 + 1 (quantities)
    }
    
    @Test
    fun `applyCoupon should apply discount`() = runTest {
        // Arrange
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Success(sampleCartItems)
        )
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Act
        viewModel.applyCoupon("SAVE10")  // 10% discount
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals("SAVE10", uiState.appliedCoupon)
    }
    
    @Test
    fun `clearCart should remove all items`() = runTest {
        // Arrange
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Success(sampleCartItems)
        )
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Act
        viewModel.clearCart()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(true, uiState.cartItems.isEmpty())
    }
    
    @Test
    fun `loadCartItems should handle error state`() = runTest {
        // Arrange
        val errorMessage = "خطای در بارگذاری سبد"
        coEvery { getCartItemsUseCase() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Error(code = 500, message = errorMessage)
        )
        
        // Act
        viewModel.loadCartItems()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(errorMessage, uiState.error)
        assertEquals(true, uiState.cartItems.isEmpty())
    }
}
