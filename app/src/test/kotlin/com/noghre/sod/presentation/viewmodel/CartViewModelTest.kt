package com.noghre.sod.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.AddToCartUseCase
import com.noghre.sod.domain.usecase.RemoveFromCartUseCase
import com.noghre.sod.domain.usecase.GetCartItemsUseCase
import com.noghre.sod.domain.usecase.UpdateCartItemQuantityUseCase
import com.noghre.sod.domain.usecase.ClearCartUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Unit Tests for CartViewModel
 * 
 * مختبری شامل:
 * ✅ افزودن به سبد
 * ✅ حذف از سبد
 * ✅ بررسی سبد
 * ✅ تغییر تعداد
 * ✅ محاسبه قيمت مجموعی
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var addToCartUseCase: AddToCartUseCase

    @MockK
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase

    @MockK
    private lateinit var getCartItemsUseCase: GetCartItemsUseCase

    @MockK
    private lateinit var updateQuantityUseCase: UpdateCartItemQuantityUseCase

    @MockK
    private lateinit var clearCartUseCase: ClearCartUseCase

    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)

        viewModel = CartViewModel(
            addToCartUseCase = addToCartUseCase,
            removeFromCartUseCase = removeFromCartUseCase,
            getCartItemsUseCase = getCartItemsUseCase,
            updateQuantityUseCase = updateQuantityUseCase,
            clearCartUseCase = clearCartUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== افزودن به سبد ==========

    @Test
    fun `افزودن به سبد - موفق`() = runTest(testDispatcher) {
        // Given
        val productId = "1"
        val quantity = 2
        val cartItem = CartItem(
            productId = productId,
            quantity = quantity,
            product = Product(
                id = productId,
                name = "انگشتر نقره",
                price = 1500000.0,
                imageUrl = "",
                description = "",
                categoryId = "rings",
                stock = 10
            )
        )

        coEvery { addToCartUseCase(productId, quantity) } returns Unit
        coEvery { getCartItemsUseCase() } returns flowOf(listOf(cartItem))

        // When
        viewModel.addToCart(productId, quantity)
        advanceUntilIdle()

        // Then
        coVerify { addToCartUseCase(productId, quantity) }
        
        val state = viewModel.uiState.value
        assertThat(state.cartItems).hasSize(1)
        assertThat(state.cartItems[0].productId).isEqualTo(productId)
    }

    @Test
    fun `افزودن به سبد - خطا رخ ثبتی شده`() = runTest(testDispatcher) {
        // Given
        val productId = "1"
        coEvery { addToCartUseCase(productId, any()) } throws IOException("اتصال انترنت")

        // When
        viewModel.addToCart(productId, 1)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.error).isNotNull()
    }

    @Test
    fun `افزودن به سبد - انبار کم`() = runTest(testDispatcher) {
        // Given
        val productId = "1"
        coEvery { addToCartUseCase(productId, 100) } throws Exception("روز باقیر کافی نیست")

        // When
        viewModel.addToCart(productId, 100)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.error).isNotNull()
    }

    // ========== حذف از سبد ==========

    @Test
    fun `حذف از سبد - موفق`() = runTest(testDispatcher) {
        // Given
        val itemId = "cart_1"
        coEvery { removeFromCartUseCase(itemId) } returns Unit
        coEvery { getCartItemsUseCase() } returns flowOf(emptyList())

        // When
        viewModel.removeFromCart(itemId)
        advanceUntilIdle()

        // Then
        coVerify { removeFromCartUseCase(itemId) }
        
        val state = viewModel.uiState.value
        assertThat(state.cartItems).isEmpty()
    }

    @Test
    fun `حذف از سبد - خطا`() = runTest(testDispatcher) {
        // Given
        val itemId = "cart_1"
        coEvery { removeFromCartUseCase(itemId) } throws Exception("خطا خذف")

        // When
        viewModel.removeFromCart(itemId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.error).isNotNull()
    }

    // ========== تغییر تعداد ==========

    @Test
    fun `تغییر تعداد - افزایش`() = runTest(testDispatcher) {
        // Given
        val itemId = "cart_1"
        val newQuantity = 3
        val updatedItem = CartItem(
            productId = "1",
            quantity = newQuantity,
            product = Product(
                id = "1",
                name = "محصول",
                price = 1500000.0,
                imageUrl = "",
                description = "",
                categoryId = "rings",
                stock = 10
            )
        )

        coEvery { updateQuantityUseCase(itemId, newQuantity) } returns Unit
        coEvery { getCartItemsUseCase() } returns flowOf(listOf(updatedItem))

        // When
        viewModel.updateQuantity(itemId, newQuantity)
        advanceUntilIdle()

        // Then
        coVerify { updateQuantityUseCase(itemId, newQuantity) }
        
        val state = viewModel.uiState.value
        assertThat(state.cartItems[0].quantity).isEqualTo(newQuantity)
    }

    @Test
    fun `تغییر تعداد - به صفر`() = runTest(testDispatcher) {
        // Given - quantity = 0 باید حذف شه
        val itemId = "cart_1"
        coEvery { updateQuantityUseCase(itemId, 0) } throws Exception("تعداد باید > 0")

        // When
        viewModel.updateQuantity(itemId, 0)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.error).isNotNull()
    }

    // ========== بررسی سبد ==========

    @Test
    fun `بررسی سبد - لاخته اندازه`() = runTest(testDispatcher) {
        // Given
        val cartItems = listOf(
            CartItem(
                productId = "1",
                quantity = 2,
                product = Product(
                    id = "1",
                    name = "محصول 1",
                    price = 1000.0,
                    imageUrl = "",
                    description = "",
                    categoryId = "cat",
                    stock = 10
                )
            ),
            CartItem(
                productId = "2",
                quantity = 3,
                product = Product(
                    id = "2",
                    name = "محصول 2",
                    price = 2000.0,
                    imageUrl = "",
                    description = "",
                    categoryId = "cat",
                    stock = 5
                )
            )
        )

        coEvery { getCartItemsUseCase() } returns flowOf(cartItems)

        // When
        viewModel.loadCart()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.cartItems).hasSize(2)
        assertThat(state.totalPrice).isEqualTo(2000.0 * 2 + 1000.0 * 3)  // 8000
        assertThat(state.totalItems).isEqualTo(5)  // 2 + 3
    }

    @Test
    fun `بررسی سبد - خالی`() = runTest(testDispatcher) {
        // Given
        coEvery { getCartItemsUseCase() } returns flowOf(emptyList())

        // When
        viewModel.loadCart()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.cartItems).isEmpty()
        assertThat(state.totalPrice).isEqualTo(0.0)
        assertThat(state.totalItems).isEqualTo(0)
    }

    // ========== محاسبه قيمت ==========

    @Test
    fun `محاسبه قيمت مجموعی - دقيق`() = runTest(testDispatcher) {
        // Given
        val cartItems = listOf(
            CartItem(
                productId = "1",
                quantity = 1,
                product = Product(
                    id = "1",
                    name = "محصول 1",
                    price = 1500000.0,  // 1.5M
                    imageUrl = "",
                    description = "",
                    categoryId = "cat",
                    stock = 10
                )
            ),
            CartItem(
                productId = "2",
                quantity = 2,
                product = Product(
                    id = "2",
                    name = "محصول 2",
                    price = 500000.0,  // 0.5M
                    imageUrl = "",
                    description = "",
                    categoryId = "cat",
                    stock = 10
                )
            )
        )

        coEvery { getCartItemsUseCase() } returns flowOf(cartItems)

        // When
        viewModel.loadCart()
        advanceUntilIdle()

        // Then - 1.5M + (0.5M * 2) = 2.5M
        val state = viewModel.uiState.value
        val expectedTotal = 1500000.0 + (500000.0 * 2)
        assertThat(state.totalPrice).isEqualTo(expectedTotal)
    }

    @Test
    fun `پاك بره و مالیات - دقيق`() = runTest(testDispatcher) {
        // Given
        val cartItems = listOf(
            CartItem(
                productId = "1",
                quantity = 1,
                product = Product("1", "محصول", 1000.0, "", "", "cat", 10),
            ),
            CartItem(
                productId = "2",
                quantity = 2,
                product = Product("2", "محصول", 500.0, "", "", "cat", 10),
            ),
            CartItem(
                productId = "3",
                quantity = 3,
                product = Product("3", "محصول", 200.0, "", "", "cat", 10),
            )
        )

        coEvery { getCartItemsUseCase() } returns flowOf(cartItems)

        // When
        viewModel.loadCart()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.totalItems).isEqualTo(6)  // 1 + 2 + 3
    }

    // ========== تائيدکننری ==========

    @Test
    fun `هر ہٹر اکسشن - سبد هائی بارگذاری جدید`() = runTest(testDispatcher) {
        // Given
        val cartItems = listOf(
            CartItem(
                productId = "1",
                quantity = 1,
                product = Product("1", "محصول", 1000.0, "", "", "cat", 10),
            )
        )

        coEvery { getCartItemsUseCase() } returns flowOf(cartItems)

        // When
        viewModel.loadCart()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.cartItems).isNotEmpty()
    }

    @Test
    fun `خالی كردن سبد - موفق`() = runTest(testDispatcher) {
        // Given
        coEvery { clearCartUseCase() } returns Unit
        coEvery { getCartItemsUseCase() } returns flowOf(emptyList())

        // When
        viewModel.clearCart()
        advanceUntilIdle()

        // Then
        coVerify { clearCartUseCase() }
        
        val state = viewModel.uiState.value
        assertThat(state.cartItems).isEmpty()
    }
}
