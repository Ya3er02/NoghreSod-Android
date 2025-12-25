package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

// ============== PRODUCT VIEW MODEL ==============

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getFeaturedProductsUseCase: GetFeaturedProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase
) : ViewModel() {

    // State for products list
    private val _productsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val productsState: StateFlow<UiState<List<Product>>> = _productsState.asStateFlow()

    // State for product detail
    private val _productDetailState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val productDetailState: StateFlow<UiState<Product>> = _productDetailState.asStateFlow()

    // State for categories
    private val _categoriesState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<String>>> = _categoriesState.asStateFlow()

    // State for favorite products
    private val _favoriteProductsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val favoriteProductsState: StateFlow<UiState<List<Product>>> = _favoriteProductsState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Search results
    val searchResults: StateFlow<UiState<List<Product>>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(UiState.Idle)
            } else {
                searchProductsUseCase(query)
                    .map { result ->
                        result.fold(
                            onSuccess = { UiState.Success(it) },
                            onFailure = { UiState.Error(it.message ?: "Search failed") }
                        )
                    }
                    .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Idle)

    fun getProducts(limit: Int = 20, offset: Int = 0) {
        viewModelScope.launchSafely {
            getProductsUseCase(limit, offset)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load products") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _productsState.value = it }
        }
    }

    fun getProductDetail(productId: String) {
        viewModelScope.launchSafely {
            getProductDetailUseCase(productId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Product not found") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _productDetailState.value = it }
        }
    }

    fun getCategories() {
        viewModelScope.launchSafely {
            getCategoriesUseCase()
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load categories") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _categoriesState.value = it }
        }
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launchSafely {
            getProductsByCategoryUseCase(category)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load products") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _productsState.value = it }
        }
    }

    fun getFeaturedProducts() {
        viewModelScope.launchSafely {
            getFeaturedProductsUseCase()
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load featured products") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _productsState.value = it }
        }
    }

    fun getFavorites() {
        viewModelScope.launchSafely {
            getFavoriteProductsUseCase()
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load favorites") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _favoriteProductsState.value = it }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launchSafely {
            toggleFavoriteUseCase(product)
                .collect { result ->
                    result.onFailure {
                        Timber.e(it, "Failed to toggle favorite")
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

// ============== CART VIEW MODEL ==============

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _cartState = MutableStateFlow<UiState<Cart>>(UiState.Loading)
    val cartState: StateFlow<UiState<Cart>> = _cartState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    fun getCart(userId: String) {
        viewModelScope.launchSafely {
            getCartUseCase(userId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load cart") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _cartState.value = it }
        }
    }

    fun addToCart(cartId: String, product: Product, quantity: Int) {
        viewModelScope.launchSafely {
            addToCartUseCase(cartId, product, quantity)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to add to cart") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _cartState.value = it }
        }
    }

    fun updateCartItem(itemId: String, quantity: Int) {
        viewModelScope.launchSafely {
            updateCartItemUseCase(itemId, quantity)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to update item") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _cartState.value = it }
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launchSafely {
            removeFromCartUseCase(itemId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to remove item") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _cartState.value = it }
        }
    }

    fun clearCart(cartId: String) {
        viewModelScope.launchSafely {
            clearCartUseCase(cartId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(Unit) },
                        onFailure = { UiState.Error(it.message ?: "Failed to clear cart") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _actionState.value = it }
        }
    }
}

// ============== ORDER VIEW MODEL ==============

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getOrderTrackingUseCase: GetOrderTrackingUseCase
) : ViewModel() {

    private val _ordersState = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val ordersState: StateFlow<UiState<List<Order>>> = _ordersState.asStateFlow()

    private val _orderDetailState = MutableStateFlow<UiState<Order>>(UiState.Loading)
    val orderDetailState: StateFlow<UiState<Order>> = _orderDetailState.asStateFlow()

    private val _createOrderState = MutableStateFlow<UiState<Order>>(UiState.Idle)
    val createOrderState: StateFlow<UiState<Order>> = _createOrderState.asStateFlow()

    fun getOrders(userId: String) {
        viewModelScope.launchSafely {
            getOrdersUseCase(userId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to load orders") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _ordersState.value = it }
        }
    }

    fun getOrderDetail(orderId: String) {
        viewModelScope.launchSafely {
            getOrderDetailUseCase(orderId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Order not found") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _orderDetailState.value = it }
        }
    }

    fun createOrder(order: Order) {
        viewModelScope.launchSafely {
            _createOrderState.value = UiState.Loading
            createOrderUseCase(order)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to create order") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _createOrderState.value = it }
        }
    }

    fun getOrderTracking(orderId: String) {
        viewModelScope.launchSafely {
            getOrderTrackingUseCase(orderId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Tracking not available") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _orderDetailState.value = it }
        }
    }
}

// ============== USER VIEW MODEL ==============

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    fun getUserProfile() {
        viewModelScope.launchSafely {
            getUserProfileUseCase()
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Profile not found") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _userState.value = it }
        }
    }

    fun updateProfile(firstName: String, lastName: String) {
        viewModelScope.launchSafely {
            updateUserProfileUseCase(firstName, lastName)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Update failed") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _userState.value = it }
        }
    }

    fun addAddress(address: Address) {
        viewModelScope.launchSafely {
            addAddressUseCase(address)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Failed to add address") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _userState.value = it }
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launchSafely {
            updateAddressUseCase(address)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(it) },
                        onFailure = { UiState.Error(it.message ?: "Update failed") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _userState.value = it }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launchSafely {
            deleteAddressUseCase(addressId)
                .map { result ->
                    result.fold(
                        onSuccess = { UiState.Success(Unit) },
                        onFailure = { UiState.Error(it.message ?: "Delete failed") }
                    )
                }
                .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                .collect { _actionState.value = it }
        }
    }
}

// ============== UI STATE ==============

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Idle : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ============== EXTENSIONS ==============

private fun <T> androidx.lifecycle.ViewModelScope.launchSafely(
    block: suspend () -> Unit
) {
    kotlinx.coroutines.launch {
        try {
            block()
        } catch (e: Exception) {
            Timber.e(e, "Error in ViewModelScope")
        }
    }
}
