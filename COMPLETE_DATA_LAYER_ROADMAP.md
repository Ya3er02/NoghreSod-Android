# 🚀 Noghresod Android - Complete Data Layer Implementation Roadmap

**Project Status**: Phase 2 - Data Layer Implementation in Progress  
**Last Updated**: December 23, 2025  
**Current Progress**: 35% Complete  

---

## ✅ Completed Components (4/15)

### Database Layer
- ✅ ProductEntity.kt - Silver jewelry product model
- ✅ CartEntity.kt & CartItemEntity.kt - Shopping cart models
- ✅ UserEntity.kt - User authentication & profile model
- ✅ OrderEntity.kt, OrderItemEntity.kt, OrderStatusHistoryEntity.kt - Order management
- ✅ Converters.kt - Room type converters for complex types
- ✅ AppDatabase.kt - Room database configuration
- ✅ ProductDao.kt - Product database operations
- ✅ CartDao.kt - Cart database operations
- ✅ OrderDao.kt - Order database operations
- ✅ UserDao.kt - User database operations

### DTOs
- ✅ ProductDto.kt - Product API response model

---

## 🚧 Remaining Components (11/15)

### Part 1: API DTOs (5 files)

#### 1. UserDto.kt - User API Models
```kotlin
data class LoginRequestDto(
    val phone: String,
    val password: String
)

data class LoginResponseDto(
    val success: Boolean,
    val data: UserTokenDto,
    val message: String?
)

data class UserTokenDto(
    val userId: String,
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class UserProfileDto(
    val id: String,
    val phone: String,
    val email: String?,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val verificationStatus: String,
    val accountStatus: String
)
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

#### 2. OrderDto.kt - Order API Models
```kotlin
data class CreateOrderRequestDto(
    val items: List<OrderItemRequestDto>,
    val shippingAddress: String,
    val phoneNumber: String,
    val recipientName: String,
    val paymentMethod: String,
    val notes: String? = null
)

data class OrderResponseDto(
    val id: String,
    val orderNumber: String,
    val status: String,
    val items: List<OrderItemResponseDto>,
    val total: Long,
    val trackingCode: String?,
    val estimatedDeliveryDate: Long?
)
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

#### 3. CartDto.kt - Cart API Models
```kotlin
data class AddToCartRequestDto(
    val productId: String,
    val quantity: Int,
    val weight: Double,
    val laborCost: Long
)

data class CartSummaryDto(
    val items: List<CartItemResponseDto>,
    val totalItems: Int,
    val totalWeight: Double,
    val subtotal: Long,
    val tax: Long,
    val shippingCost: Long,
    val total: Long
)
```
**Status**: ⏳ PENDING  
**Priority**: HIGH  

#### 4. PaymentDto.kt - Payment Integration Models
```kotlin
data class InitPaymentRequestDto(
    val orderId: String,
    val amount: Long,
    val returnUrl: String
)

data class InitPaymentResponseDto(
    val authority: String,
    val paymentUrl: String
)

data class VerifyPaymentRequestDto(
    val authority: String
)
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL (Phase 3)  

#### 5. CategoryDto.kt & CouponDto.kt
```kotlin
data class CategoryDto(
    val id: String,
    val name: String,
    val nameFA: String,
    val description: String,
    val image: String,
    val isActive: Boolean
)

data class CouponDto(
    val id: String,
    val code: String,
    val discountPercentage: Int,
    val maxUses: Int,
    val usedCount: Int,
    val expiryDate: Long
)
```
**Status**: ⏳ PENDING  
**Priority**: MEDIUM  

### Part 2: Remote Data Source (2 files)

#### 6. ApiService.kt - Retrofit API Interface
```kotlin
interface ApiService {
    // === AUTHENTICATION ===
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @POST("/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenDto): Response<LoginResponseDto>
    
    // === PRODUCTS ===
    @GET("/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ProductListResponse>
    
    @GET("/products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ProductDto>
    
    @GET("/products/search")
    suspend fun searchProducts(@Query("q") query: String): Response<ProductListResponse>
    
    // === CART ===
    @POST("/cart/items")
    suspend fun addToCart(@Body request: AddToCartRequestDto): Response<CartSummaryDto>
    
    @GET("/cart")
    suspend fun getCart(): Response<CartSummaryDto>
    
    @PUT("/cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body request: UpdateCartItemDto
    ): Response<CartSummaryDto>
    
    @DELETE("/cart/items/{id}")
    suspend fun removeFromCart(@Path("id") itemId: String): Response<CartSummaryDto>
    
    // === ORDERS ===
    @POST("/orders")
    suspend fun createOrder(@Body request: CreateOrderRequestDto): Response<OrderResponseDto>
    
    @GET("/orders")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<OrderListResponse>
    
    @GET("/orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): Response<OrderResponseDto>
    
    @GET("/orders/{id}/tracking")
    suspend fun trackOrder(@Path("id") orderId: String): Response<OrderTrackingDto>
    
    // === PAYMENT ===
    @POST("/payment/init")
    suspend fun initPayment(@Body request: InitPaymentRequestDto): Response<InitPaymentResponseDto>
    
    @POST("/payment/verify")
    suspend fun verifyPayment(@Body request: VerifyPaymentRequestDto): Response<PaymentVerifyResponseDto>
    
    // === USER ===
    @GET("/user/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>
    
    @PUT("/user/profile")
    suspend fun updateUserProfile(@Body request: UpdateUserProfileDto): Response<UserProfileDto>
    
    // === CATEGORIES ===
    @GET("/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
    
    // === COUPONS ===
    @POST("/coupons/validate")
    suspend fun validateCoupon(@Body request: ValidateCouponRequestDto): Response<CouponDto>
}
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

#### 7. RetrofitConfig.kt & HttpClientSetup.kt
```kotlin
object RetrofitConfig {
    const val BASE_URL = "https://api.noghresod.ir/v1/"
    const val TIMEOUT = 30L
    const val MAX_RETRIES = 3
    
    fun createRetrofit(token: String? = null): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(ErrorInterceptor())
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

### Part 3: Data Source Implementations (1 file)

#### 8. LocalDataSource.kt & RemoteDataSource.kt
```kotlin
interface LocalDataSource {
    suspend fun getProducts(): List<ProductEntity>
    suspend fun saveProducts(products: List<ProductEntity>)
    suspend fun getProductById(id: String): ProductEntity?
    suspend fun addToCart(item: CartItemEntity)
    suspend fun getCart(): CartEntity?
    // ... more methods
}

class LocalDataSourceImpl(private val database: AppDatabase) : LocalDataSource {
    // Implementation using DAOs
}

interface RemoteDataSource {
    suspend fun fetchProducts(page: Int = 1, limit: Int = 20): Result<List<ProductDto>>
    suspend fun login(phone: String, password: String): Result<UserTokenDto>
    suspend fun createOrder(order: CreateOrderRequestDto): Result<OrderResponseDto>
    // ... more methods
}

class RemoteDataSourceImpl(private val apiService: ApiService) : RemoteDataSource {
    // Implementation using Retrofit API calls
}
```
**Status**: ⏳ PENDING  
**Priority**: HIGH  

### Part 4: Repository Implementations (3 files)

#### 9. ProductRepository.kt
```kotlin
interface ProductRepository {
    suspend fun getProducts(page: Int = 1, limit: Int = 20): Flow<Result<List<Product>>>
    suspend fun getProductById(id: String): Flow<Result<Product>>
    suspend fun searchProducts(query: String): Flow<Result<List<Product>>>
    suspend fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>>
    suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Flow<Result<Unit>>
}

class ProductRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : ProductRepository {
    // Offline-first implementation
    // Sync strategy, caching, error handling
}
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

#### 10. CartRepository.kt
```kotlin
interface CartRepository {
    suspend fun getCart(): Flow<Result<Cart>>
    suspend fun addToCart(productId: String, quantity: Int, weight: Double): Flow<Result<Cart>>
    suspend fun updateCartItem(itemId: Long, quantity: Int): Flow<Result<Cart>>
    suspend fun removeFromCart(itemId: Long): Flow<Result<Cart>>
    suspend fun clearCart(): Flow<Result<Unit>>
    suspend fun syncCart(): Flow<Result<Unit>>
}

class CartRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : CartRepository {
    // Sync cart with server
    // Handle offline cart state
}
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

#### 11. OrderRepository.kt & UserRepository.kt
```kotlin
interface OrderRepository {
    suspend fun createOrder(order: CreateOrderRequestDto): Flow<Result<Order>>
    suspend fun getOrders(page: Int = 1): Flow<Result<List<Order>>>
    suspend fun getOrderById(orderId: String): Flow<Result<Order>>
    suspend fun trackOrder(orderId: String): Flow<Result<OrderTrackingInfo>>
}

interface UserRepository {
    suspend fun login(phone: String, password: String): Flow<Result<UserToken>>
    suspend fun getUserProfile(): Flow<Result<User>>
    suspend fun updateUserProfile(user: UpdateUserProfileDto): Flow<Result<User>>
    suspend fun logout(): Flow<Result<Unit>>
}
```
**Status**: ⏳ PENDING  
**Priority**: CRITICAL  

### Part 5: Security & Interceptors (1 file)

#### 12. AuthInterceptor.kt, HeaderInterceptor.kt, ErrorInterceptor.kt
```kotlin
class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = tokenProvider.getToken()
        
        if (token != null) {
            request = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        
        var response = chain.proceed(request)
        
        // Handle token refresh on 401
        if (response.code == 401) {
            // Refresh token logic
        }
        
        return response
    }
}

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept-Language", "fa-IR")
            .header("User-Agent", "NoghreSod/1.0")
            .build()
        
        return chain.proceed(request)
    }
}

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: IOException) {
            throw NetworkException("Network error", e)
        }
    }
}
```
**Status**: ⏳ PENDING  
**Priority**: HIGH  

### Part 6: Mappers (1 file)

#### 13. Mapper Functions
```kotlin
// Extension functions for DTO to Entity conversion
fun ProductDto.toEntity(): ProductEntity = /* ... */
fun UserDto.toEntity(): UserEntity = /* ... */
fun OrderDto.toEntity(): OrderEntity = /* ... */

// Extension functions for Entity to Domain conversion
fun ProductEntity.toDomain(): Product = /* ... */
fun UserEntity.toDomain(): User = /* ... */
fun OrderEntity.toDomain(): Order = /* ... */
```
**Status**: ⏳ PENDING  
**Priority**: MEDIUM  

### Part 7: Hilt DI Modules (1 file)

#### 14. DataModule.kt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return getAppDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitConfig.createRetrofit().create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideLocalDataSource(database: AppDatabase): LocalDataSource {
        return LocalDataSourceImpl(database)
    }
    
    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSourceImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideProductRepository(
        local: LocalDataSource,
        remote: RemoteDataSource
    ): ProductRepository {
        return ProductRepositoryImpl(local, remote)
    }
    
    @Provides
    @Singleton
    fun provideCartRepository(
        local: LocalDataSource,
        remote: RemoteDataSource
    ): CartRepository {
        return CartRepositoryImpl(local, remote)
    }
    
    @Provides
    @Singleton
    fun provideOrderRepository(
        local: LocalDataSource,
        remote: RemoteDataSource
    ): OrderRepository {
        return OrderRepositoryImpl(local, remote)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        local: LocalDataSource,
        remote: RemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(local, remote)
    }
}
```
**Status**: ⏳ PENDING  
**Priority**: HIGH  

### Part 8: UseCase Implementations (1 file)

#### 15. Domain Use Cases
```kotlin
class GetProductsUseCase(private val repository: ProductRepository) 
    : FlowUseCase<GetProductsUseCase.Params, List<Product>>() {
    
    data class Params(val page: Int = 1, val limit: Int = 20)
    
    override fun execute(params: Params): Flow<Result<List<Product>>> {
        return repository.getProducts(params.page, params.limit)
    }
}

class AddToCartUseCase(private val repository: CartRepository) 
    : FlowUseCase<AddToCartUseCase.Params, Cart>() {
    
    data class Params(val productId: String, val quantity: Int, val weight: Double)
    
    override fun execute(params: Params): Flow<Result<Cart>> {
        return repository.addToCart(params.productId, params.quantity, params.weight)
    }
}

class CreateOrderUseCase(private val repository: OrderRepository) 
    : FlowUseCase<CreateOrderRequestDto, Order>() {
    
    override fun execute(params: CreateOrderRequestDto): Flow<Result<Order>> {
        return repository.createOrder(params)
    }
}

class LoginUseCase(private val repository: UserRepository) 
    : FlowUseCase<LoginUseCase.Params, UserToken>() {
    
    data class Params(val phone: String, val password: String)
    
    override fun execute(params: Params): Flow<Result<UserToken>> {
        return repository.login(params.phone, params.password)
    }
}
```
**Status**: ⏳ PENDING  
**Priority**: HIGH  

---

## 📊 Implementation Timeline

### Week 1 (This Week)
- [x] Entities & Database (DONE)
- [ ] DTOs & API Models (IN PROGRESS)
- [ ] ApiService Interface
- [ ] RetrofitConfig Setup

### Week 2
- [ ] Data Source Implementations
- [ ] Repository Implementations
- [ ] Mapper Functions
- [ ] Hilt DI Setup

### Week 3
- [ ] UseCase Classes
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] API Integration Testing

### Week 4
- [ ] ViewModels
- [ ] Screens Implementation
- [ ] UI Integration
- [ ] End-to-End Testing

---

## 🎯 Success Criteria

✅ Complete - All 15 components implemented  
✅ Tested - 80%+ code coverage  
✅ Documented - KDoc comments on all classes  
✅ Secure - Bearer token auth, encrypted storage  
✅ Scalable - Repository pattern, DI, clean architecture  
✅ Performant - Offline-first, caching, efficient queries  
✅ Maintainable - Type-safe, proper error handling  

---

## 🔗 Related Files

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [CODE_ANALYSIS_REPORT.md](CODE_ANALYSIS_REPORT.md) - Code quality analysis
- [API_INTEGRATION.md](API_INTEGRATION.md) - API endpoints documentation

---

**Status**: Active Development  
**Next**: API DTOs Implementation  
**Estimated Completion**: 2-3 weeks  

Built with ❤️ for Noghresod Team
