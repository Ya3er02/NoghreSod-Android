# NoghreSod Data Layer Architecture

## 📋 Overview

یک معماری Clean Architecture بر اساس **SSOT (Single Source of Truth)** pattern با:
- **Offline-First** caching strategy
- **Network-First** synchronization
- **Reactive** data flow با Kotlin Flows
- **Type-Safe** dependency injection با Hilt

## 🏗️ Layer Structure

```
app/src/main/kotlin/com/noghre/sod/data/
├── database/           # Local caching (Room)
├── network/            # API calls (Retrofit)
├── repository/         # Business logic & data aggregation
├── mapper/            # DTO ↔ Domain conversion
├── error/             # Error handling
└── di/                # Hilt dependency injection
```

## 📦 Components

### 1. **Repositories** (Single Source of Truth)

#### Product Repository
```kotlin
getProducts(page, pageSize)        // Products with pagination
getProductsByCategory()             // Filter by category
searchProducts(query)               // Full-text search
filterProducts(filter)              // Advanced filtering
getProductDetail(id)                // Detailed view
getFeaturedProducts()               // Homepage highlights
getMarketPrices()                   // Real-time market data
```

#### Cart Repository
```kotlin
getCart()              // Get user's cart
addToCart()            // Add with variants
updateCartItem()       // Update quantity
removeFromCart()       // Remove single item
clearCart()            // Clear all items
```

#### Order Repository
```kotlin
getOrders()            // Order history
getOrderDetail(id)     // Order details
createOrder()          // Place new order
getOrderTracking()     // Tracking info
```

#### User Repository
```kotlin
getUserProfile()       // Get profile data
updateUserProfile()    // Update info
addAddress()           // Add shipping address
updateAddress()        // Update address
deleteAddress()        // Remove address
```

#### Payment Repository
```kotlin
processPayment()       // Process payment
getPaymentStatus()     // Check status
verifyTransaction()    // Verify transaction
```

#### Wishlist Repository
```kotlin
getWishlist()          // Get all items
isInWishlist()         // Check if added
addToWishlist()        // Add item
removeFromWishlist()   // Remove item
enablePriceNotification()    // Price drop alerts
disablePriceNotification()   // Disable alerts
```

### 2. **Network Bound Resource** (Caching Strategy)

```kotlin
networkBoundResource(
    query = { localData },          // Query from DB first
    fetch = { apiCall },            // Fetch from network
    saveFetchResult = { save },     // Cache to DB
    shouldFetch = { predicate },    // When to fetch
    onFetchFailed = { handler }     // Error handling
)
```

**Cache Staleness Policy:**
- Products: 2 hours
- Cart: 1 hour
- Orders: 30 minutes
- Wishlist: 1 hour
- User Profile: Real-time
- Market Prices: Real-time

### 3. **Mappers** (Data Transformation)

هر repository دارای mapper است برای تبدیل:
- `DTO (API Response)` → `Domain Model`
- `Entity (Database)` → `Domain Model`
- `Domain Model` → `Entity (Database)`

```kotlin
ProductDto.toDomain()              // API → Domain
ProductEntity.toDomain()           // DB → Domain
Product.toEntity()                 // Domain → DB
List<ProductDto>.toDomainList()   // Batch conversion
```

## 🔄 Data Flow

### Example: Getting Products

```
┌─────────────────────────────────────────────────┐
│ 1. ViewModel calls: productRepository.getProducts() │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────▼──────────────┐
       │ 2. NetworkBoundResource  │
       │    Flow starts           │
       └───────────┬──────────────┘
                   │
        ┌──────────▼────────────┐
        │ 3. Emit Loading State │
        └──────────┬────────────┘
                   │
        ┌──────────▼──────────────┐
        │ 4. Query Local Database │
        │    (ProductDao)         │
        └──────────┬──────────────┘
                   │
        ┌──────────▼───────────────────────┐
        │ 5. Check if Cache is Stale       │
        │    (age > 2 hours?)              │
        └──────────┬──────────────────────┘
                   │
         Yes ┌─────▼─────┐ No
            │            │
    ┌───────▼──────┐    │
    │ 6. Fetch from │    │
    │ Network (API) │    │
    └───────┬──────┘    │
            │           │
       ┌────▼──────────────┐
       │ 7. Save to Cache  │
       │    (ProductDao)   │
       └────┬──────────────┘
            │               │
    ┌───────▼────────┐      │
    │ 8. Emit Success│      │
    │    with Data   │      │
    └────────────────┘      │
                           │
                    ┌──────▼────────┐
                    │ Use Cached    │
                    │ Data Directly │
                    └───────────────┘
```

## 📊 Database (Room)

### Entities
```
ProductEntity
  ├── id: String (Primary Key)
  ├── name, description
  ├── price, currentPrice
  ├── images (JSON List)
  ├── rating, weight
  └── lastUpdated: Long

CartEntity
  ├── id: String (Primary Key)
  ├── userId: String
  ├── items (JSON List)
  └── totalPrice: Double

OrderEntity
  ├── id: String (Primary Key)
  ├── userId, status
  ├── items, totalPrice
  └── trackingNumber

UserEntity
  ├── id: String (Primary Key)
  ├── firstName, lastName, email
  ├── addresses (JSON List)
  └── lastUpdated

WishlistEntity
  ├── id: String (Primary Key)
  ├── productId, productName
  ├── currentPrice
  └── priceDropNotificationEnabled
```

### DAOs (Data Access Objects)
```kotlin
ProductDao         // Insert, query, filter, search
CartDao            // Get, insert, update, clear
OrderDao           // Get all, get by ID, insert
UserDao            // Get current, insert
WishlistDao        // Get all, get by ID, delete
```

## 🌐 Network (Retrofit + OkHttp)

### API Interface: NoghreSodApi

```kotlin
// Products
getProducts(page, pageSize)
getProductsByCategory(categoryId, page, pageSize)
searchProducts(query, page, pageSize)
filterProducts(priceMin, priceMax, ...)
getProductDetail(productId)
getFeaturedProducts()
getSaleProducts(page, pageSize)
getRelatedProducts(productId)
getMarketPrices()  // Real-time gold/silver prices

// Cart
getCart()
addToCart(request)
updateCartItem(itemId, request)
removeFromCart(itemId)
clearCart()

// Orders
getOrders()
getOrderDetail(orderId)
createOrder(request)
getOrderTracking(orderId)

// Users
getUserProfile()
updateUserProfile(request)
addAddress(request)
updateAddress(addressId, request)
deleteAddress(addressId)

// Payments
processPayment(request)
getPaymentStatus(paymentId)
verifyPayment(request)

// Wishlist
getWishlist()
addToWishlist(request)
removeFromWishlist(productId)
enablePriceNotification(request)
disablePriceNotification(request)
```

## 🔒 Error Handling

### ExceptionHandler
```kotlin
// Central exception handling
ExceptionHandler.handle(exception, context)

// Error types:
- NetworkException: No internet
- ApiException: Server errors (4xx, 5xx)
- ParseException: JSON parsing fails
- DatabaseException: Local DB error
- ValidationException: Invalid input
```

### Result Class
```kotlin
Result<T>(
    status: Status,          // LOADING, SUCCESS, FAILURE
    data: T?,               // Actual data
    exception: Exception?   // Error details
)
```

## 💉 Dependency Injection (Hilt)

### RepositoryModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): IProductRepository
    // ... similar bindings for other repos
}
```

### Usage in ViewModels
```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val cartRepository: ICartRepository
) : ViewModel() {
    val products = productRepository.getProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
```

## 📱 Usage Examples

### Example 1: Fetch Products with Caching

```kotlin
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productRepository: IProductRepository
) : ViewModel() {
    
    val products = productRepository.getProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.loading())
    
    fun searchProducts(query: String) {
        productRepository.searchProducts(query)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        // Update UI with results
                    }
                    is Result.Failure -> {
                        // Show error message
                    }
                    is Result.Loading -> {
                        // Show loading state
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
```

### Example 2: Cart Operations

```kotlin
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: ICartRepository
) : ViewModel() {
    
    val cart = cartRepository.getCart()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.loading())
    
    fun addToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            val result = cartRepository.addToCart(productId, quantity)
            // Handle result
        }
    }
}
```

## 🎯 Best Practices

### 1. Always Use Repository Interfaces
```kotlin
// ❌ Don't
val repo = ProductRepositoryImpl(api, dao)

// ✅ Do
private val repo: IProductRepository
```

### 2. Handle Results Properly
```kotlin
// ✅ Good
when (result) {
    is Result.Success -> updateUI(result.data)
    is Result.Failure -> showError(result.exception)
    is Result.Loading -> showLoader()
}
```

### 3. Use StateIn for LiveUI
```kotlin
// ✅ Good - Caches last value, replays to new subscribers
val products = repository.getProducts()
    .stateIn(viewModelScope, SharingStarted.Lazily, null)
```

### 4. Cancel Flows on Destroy
```kotlin
// ✅ Hilt ViewModel handles this automatically
viewModelScope.launch {
    repository.getProducts()
        .collect { /* automatic cleanup */ }
}
```

## 🚀 Performance Optimizations

1. **Pagination**: Load products in batches (default: 20 per page)
2. **Search**: Real-time search from network, local cache for browsing
3. **Image Loading**: Use Coil with proper caching
4. **Database Indexes**: On frequently queried columns (categoryId, productId)
5. **Network**: OkHttp caching for static responses

## 📝 File Structure Summary

```
data/
├── database/
│   ├── dao/
│   │   ├── ProductDao.kt
│   │   ├── CartDao.kt
│   │   ├── OrderDao.kt
│   │   ├── UserDao.kt
│   │   └── WishlistDao.kt
│   ├── entity/
│   │   ├── ProductEntity.kt
│   │   ├── CartEntity.kt
│   │   ├── OrderEntity.kt
│   │   ├── UserEntity.kt
│   │   └── WishlistEntity.kt
│   └── NoghreSodDatabase.kt
├── network/
│   ├── NoghreSodApi.kt
│   └── dto/
│       ├── ProductDto.kt
│       ├── CartDto.kt
│       ├── OrderDto.kt
│       ├── UserDto.kt
│       ├── PaymentDto.kt
│       └── WishlistItemDto.kt
├── repository/
│   ├── NetworkBoundResource.kt
│   ├── product/
│   │   ├── IProductRepository.kt
│   │   └── ProductRepositoryImpl.kt
│   ├── cart/
│   │   ├── ICartRepository.kt
│   │   └── CartRepositoryImpl.kt
│   ├── order/
│   │   ├── IOrderRepository.kt
│   │   └── OrderRepositoryImpl.kt
│   ├── user/
│   │   ├── IUserRepository.kt
│   │   └── UserRepositoryImpl.kt
│   ├── payment/
│   │   ├── IPaymentRepository.kt
│   │   └── PaymentRepositoryImpl.kt
│   └── wishlist/
│       ├── IWishlistRepository.kt
│       └── WishlistRepositoryImpl.kt
├── mapper/
│   ├── ProductMapper.kt
│   ├── CartMapper.kt
│   ├── OrderMapper.kt
│   ├── UserMapper.kt
│   ├── PaymentMapper.kt
│   └── WishlistMapper.kt
├── error/
│   └── ExceptionHandler.kt
└── di/
    └── RepositoryModule.kt
```

## 🔐 Security

- ✅ Tokens stored in DataStore (encrypted)
- ✅ HTTPS only for network calls
- ✅ Sensitive data not logged
- ✅ Database queries parameterized (Room handles this)

## 📈 Monitoring

تمام operations لاگ می‌شوند با Timber:
```kotlin
Timber.d("Product cached: $id")
Timber.e(exception, "Network error")
```

---

**Version**: 1.0.0  
**Last Updated**: 1402/10/11  
**Team**: NoghreSod Android Development
