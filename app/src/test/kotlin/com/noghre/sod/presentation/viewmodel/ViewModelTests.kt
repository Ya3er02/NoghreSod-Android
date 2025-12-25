package com.noghre.sod.presentation.viewmodel

import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.usecase.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductViewModelTests {

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var searchProductsUseCase: SearchProductsUseCase
    private lateinit var getProductDetailUseCase: GetProductDetailUseCase
    private lateinit var getProductsByCategoryUseCase: GetProductsByCategoryUseCase
    private lateinit var getFeaturedProductsUseCase: GetFeaturedProductsUseCase
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var getFavoriteProductsUseCase: GetFavoriteProductsUseCase
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setUp() {
        getProductsUseCase = mockk(relaxed = true)
        searchProductsUseCase = mockk(relaxed = true)
        getProductDetailUseCase = mockk(relaxed = true)
        getProductsByCategoryUseCase = mockk(relaxed = true)
        getFeaturedProductsUseCase = mockk(relaxed = true)
        getCategoriesUseCase = mockk(relaxed = true)
        toggleFavoriteUseCase = mockk(relaxed = true)
        getFavoriteProductsUseCase = mockk(relaxed = true)

        viewModel = ProductViewModel(
            getProductsUseCase,
            searchProductsUseCase,
            getProductDetailUseCase,
            getProductsByCategoryUseCase,
            getFeaturedProductsUseCase,
            getCategoriesUseCase,
            toggleFavoriteUseCase,
            getFavoriteProductsUseCase
        )
    }

    @Test
    fun testGetProductsInitial() {
        // Initial state should be Loading
        val state = viewModel.productsState.value
        assertTrue(state is UiState.Loading)
    }

    @Test
    fun testSearchQueryUpdate() {
        // Act
        viewModel.updateSearchQuery("ring")

        // Assert
        assertEquals("ring", viewModel.searchQuery.value)
    }
}

class CartViewModelTests {

    private lateinit var getCartUseCase: GetCartUseCase
    private lateinit var addToCartUseCase: AddToCartUseCase
    private lateinit var updateCartItemUseCase: UpdateCartItemUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase
    private lateinit var clearCartUseCase: ClearCartUseCase
    private lateinit var viewModel: CartViewModel

    @Before
    fun setUp() {
        getCartUseCase = mockk(relaxed = true)
        addToCartUseCase = mockk(relaxed = true)
        updateCartItemUseCase = mockk(relaxed = true)
        removeFromCartUseCase = mockk(relaxed = true)
        clearCartUseCase = mockk(relaxed = true)

        viewModel = CartViewModel(
            getCartUseCase,
            addToCartUseCase,
            updateCartItemUseCase,
            removeFromCartUseCase,
            clearCartUseCase
        )
    }

    @Test
    fun testCartInitialState() {
        // Initial state should be Loading
        val state = viewModel.cartState.value
        assertTrue(state is UiState.Loading)
    }
}

class OrderViewModelTests {

    private lateinit var getOrdersUseCase: GetOrdersUseCase
    private lateinit var getOrderDetailUseCase: GetOrderDetailUseCase
    private lateinit var createOrderUseCase: CreateOrderUseCase
    private lateinit var getOrderTrackingUseCase: GetOrderTrackingUseCase
    private lateinit var viewModel: OrderViewModel

    @Before
    fun setUp() {
        getOrdersUseCase = mockk(relaxed = true)
        getOrderDetailUseCase = mockk(relaxed = true)
        createOrderUseCase = mockk(relaxed = true)
        getOrderTrackingUseCase = mockk(relaxed = true)

        viewModel = OrderViewModel(
            getOrdersUseCase,
            getOrderDetailUseCase,
            createOrderUseCase,
            getOrderTrackingUseCase
        )
    }

    @Test
    fun testOrdersInitialState() {
        // Initial state should be Loading
        val state = viewModel.ordersState.value
        assertTrue(state is UiState.Loading)
    }
}

class UserViewModelTests {

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase
    private lateinit var updateUserProfileUseCase: UpdateUserProfileUseCase
    private lateinit var addAddressUseCase: AddAddressUseCase
    private lateinit var updateAddressUseCase: UpdateAddressUseCase
    private lateinit var deleteAddressUseCase: DeleteAddressUseCase
    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        getUserProfileUseCase = mockk(relaxed = true)
        updateUserProfileUseCase = mockk(relaxed = true)
        addAddressUseCase = mockk(relaxed = true)
        updateAddressUseCase = mockk(relaxed = true)
        deleteAddressUseCase = mockk(relaxed = true)

        viewModel = UserViewModel(
            getUserProfileUseCase,
            updateUserProfileUseCase,
            addAddressUseCase,
            updateAddressUseCase,
            deleteAddressUseCase
        )
    }

    @Test
    fun testUserInitialState() {
        // Initial state should be Loading
        val state = viewModel.userState.value
        assertTrue(state is UiState.Loading)
    }
}
