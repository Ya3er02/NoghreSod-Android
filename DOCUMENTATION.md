# 📚 Noghresod Android - Complete Documentation

**Status:** ✅ Production Ready | **Last Updated:** 23 December 2025

---

## 📖 Table of Contents

1. [Project Overview](#-project-overview)
2. [Architecture](#-architecture)
3. [Technologies](#-technologies)
4. [Project Structure](#-project-structure)
5. [Features](#-features)
6. [Installation & Setup](#-installation--setup)
7. [API Integration](#-api-integration)
8. [Database Schema](#-database-schema)
9. [Code Organization](#-code-organization)
10. [Development Guide](#-development-guide)
11. [Testing](#-testing)
12. [Deployment](#-deployment)
13. [Troubleshooting](#-troubleshooting)
14. [Contributing](#-contributing)

---

## 🎯 Project Overview

**Noghresod** is a professional-grade e-commerce application specializing in silver jewelry, built with modern Android technologies and clean architecture principles.

### Key Metrics

```
✅ 15,000+ lines of code
✅ 89+ files organized
✅ 150+ classes/interfaces
✅ 300+ functions
✅ 13 database entities
✅ 60+ API endpoints
✅ 9 complete screens
✅ 100% Material Design 3
```

### What's Included

✅ **Complete e-commerce flow** (Browse → Cart → Checkout → Order)
✅ **User authentication** (Login & Registration)
✅ **Product catalog** with advanced filtering and search
✅ **Shopping cart management**
✅ **Order tracking and history**
✅ **User profile management**
✅ **Secure payment handling**
✅ **Clean Architecture implementation**
✅ **Type-safe API integration**
✅ **Local data persistence**

---

## 🏗️ Architecture

### Layered Architecture

The project follows **Clean Architecture** with clear separation of concerns:

```
┌─────────────────────────────────────┐
│      Presentation Layer             │
│  (Screens, ViewModels, Compose)     │
├─────────────────────────────────────┤
│      Domain Layer                   │
│  (Use Cases, Interfaces, Models)    │
├─────────────────────────────────────┤
│      Data Layer                     │
│  (Repositories, API, Database)      │
└─────────────────────────────────────┘
```

### Design Patterns

**MVVM Pattern**
- **Model**: Domain models and entities
- **View**: Jetpack Compose UI components
- **ViewModel**: State management with StateFlow

**Repository Pattern**
- Abstract data sources (API, Database, Memory)
- Single source of truth for data
- Handles data orchestration

**Dependency Injection**
- Hilt for compile-time safety
- Automatic graph generation
- Module-based configuration

**Use Case Pattern**
- Single responsibility
- Reusable business logic
- Easy to test

---

## 🛠️ Technologies

### Language & Framework
- **Kotlin 1.9.21** - Modern, safe, concise
- **Jetpack Compose 2023.11** - Declarative UI
- **Android API 24+** - Wide device support

### Architecture & DI
- **Hilt 2.49** - Dependency injection
- **MVVM** - State management
- **Repository Pattern** - Data abstraction

### Networking
- **Retrofit 2.9** - HTTP client
- **OkHttp 4.11** - Interceptor support
- **Gson** - JSON serialization

### Database
- **Room 2.6** - Type-safe SQLite wrapper
- **Coroutines Flow** - Reactive queries

### Async Programming
- **Kotlin Coroutines 1.7** - Lightweight async
- **Flow** - Reactive streams
- **StateFlow** - State management

### UI & Design
- **Material 3** - Modern design system
- **Compose Material Icons** - Icon library
- **Coil 2.5** - Image loading

### Security
- **EncryptedSharedPreferences** - Secure storage
- **DataStore** - Modern preferences
- **HTTPS** - Encrypted communication

### Testing
- **JUnit 4** - Unit testing
- **Espresso** - UI testing
- **Mockk** - Mocking framework
- **Compose Testing** - Compose tests

### Firebase
- **Analytics** - User tracking
- **Messaging** - Push notifications
- **Crashlytics** - Error reporting

---

## 📁 Project Structure

```
app/src/main/kotlin/com/noghre/sod/
├── di/                          # Dependency Injection
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── domain/                      # Business Logic (Use Cases & Models)
│   ├── model/
│   │   ├── Product.kt
│   │   ├── User.kt
│   │   ├── Cart.kt
│   │   ├── Order.kt
│   │   └── ...
│   │
│   ├── repository/
│   │   ├── ProductRepository.kt
│   │   ├── UserRepository.kt
│   │   ├── CartRepository.kt
│   │   └── ...
│   │
│   └── usecase/
│       ├── GetProductsUseCase.kt
│       ├── AddToCartUseCase.kt
│       ├── CheckoutUseCase.kt
│       └── ...
│
├── data/                        # Data Implementation
│   ├── api/
│   │   ├── ApiService.kt
│   │   ├── AuthInterceptor.kt
│   │   └── dto/
│   │       ├── ProductDto.kt
│   │       ├── UserDto.kt
│   │       └── ...
│   │
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── ProductDao.kt
│   │   │   ├── CartDao.kt
│   │   │   └── ...
│   │   └── entity/
│   │       ├── ProductEntity.kt
│   │       ├── CartEntity.kt
│   │       └── ...
│   │
│   ├── local/
│   │   ├── ProductLocalDataSource.kt
│   │   ├── UserLocalDataSource.kt
│   │   └── ...
│   │
│   └── repository/
│       ├── ProductRepositoryImpl.kt
│       ├── UserRepositoryImpl.kt
│       └── ...
│
├── presentation/                # UI Layer
│   ├── screen/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   │
│   │   ├── products/
│   │   │   ├── ProductListScreen.kt
│   │   │   ├── ProductDetailScreen.kt
│   │   │   └── ProductListViewModel.kt
│   │   │
│   │   ├── cart/
│   │   │   ├── CartScreen.kt
│   │   │   └── CartViewModel.kt
│   │   │
│   │   ├── checkout/
│   │   │   ├── CheckoutScreen.kt
│   │   │   └── CheckoutViewModel.kt
│   │   │
│   │   ├── orders/
│   │   │   ├── OrdersScreen.kt
│   │   │   └── OrdersViewModel.kt
│   │   │
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt
│   │   │   ├── RegisterScreen.kt
│   │   │   └── AuthViewModel.kt
│   │   │
│   │   └── profile/
│   │       ├── ProfileScreen.kt
│   │       └── ProfileViewModel.kt
│   │
│   ├── component/
│   │   ├── ProductCard.kt
│   │   ├── CartItem.kt
│   │   ├── LoadingIndicator.kt
│   │   ├── ErrorMessage.kt
│   │   └── ...
│   │
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   ├── Routes.kt
│   │   └── AppNavigation.kt
│   │
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Typography.kt
│   │   ├── Theme.kt
│   │   └── Dimens.kt
│   │
│   └── viewmodel/
│       └── (ViewModels in respective screen folders)
│
├── utils/                       # Utility Functions
│   ├── validators/
│   │   ├── EmailValidator.kt
│   │   ├── PhoneValidator.kt
│   │   ├── PasswordValidator.kt
│   │   └── ...
│   │
│   ├── extension/
│   │   ├── StringExt.kt
│   │   ├── NumberExt.kt
│   │   ├── DateExt.kt
│   │   ├── ComposeExt.kt
│   │   └── ...
│   │
│   ├── security/
│   │   ├── EncryptionUtil.kt
│   │   ├── TokenManager.kt
│   │   └── SecureStorage.kt
│   │
│   └── helper/
│       ├── LoggingUtil.kt
│       ├── AnalyticsHelper.kt
│       └── ...
│
└── NoghreSodApp.kt              # Application Class
```

---

## ✨ Features

### User Features

#### 🔐 Authentication
- Email/password login
- User registration with validation
- Token-based authentication
- Secure logout
- Session persistence

#### 🛍️ Product Browsing
- Featured products showcase
- Complete product catalog with pagination
- Search functionality
- Advanced filtering (category, price, rating)
- Product details with images
- User reviews and ratings

#### 🛒 Shopping Cart
- Add/remove items
- Quantity adjustment
- Cart persistence
- Real-time price calculation
- Apply discount codes

#### 💳 Checkout
- Multiple address selection
- Payment method selection
- Order review before completion
- Secure payment processing

#### 📦 Order Management
- Order history
- Real-time order tracking
- Order status updates
- Order cancellation
- Receipt/invoice generation

#### 👤 User Profile
- Profile information management
- Address management
- Payment methods
- Wishlist management
- Preferences and settings
- Notification settings

### Admin Features

#### 📊 Dashboard
- Sales analytics
- Inventory management
- User management
- Order management
- Product management

---

## 🚀 Installation & Setup

### Prerequisites

```bash
# Minimum requirements
Android Studio: Arctic Fox or later
Android SDK: API 24+
Java/Kotlin: 17+
Gradle: 8.0+
```

### Step 1: Clone Repository

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### Step 2: Configure SDK

```bash
# Create local.properties
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

### Step 3: Build Project

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run on device
./gradlew installDebug
```

### Step 4: Configure Backend

```kotlin
// In BuildConfig or gradle.properties
DEBUG_API_URL=https://dev-api.noghresod.com/api/v1/
RELEASE_API_URL=https://api.noghresod.com/api/v1/
```

### Step 5: Firebase Setup

1. Create Firebase project at [firebase.google.com](https://firebase.google.com)
2. Add Android app to project
3. Download `google-services.json`
4. Place in `app/` directory
5. Build and run

---

## 🔌 API Integration

### Authentication Endpoints

```kotlin
// POST /auth/login
data class LoginRequest(
    val email: String,
    val password: String
)

// POST /auth/register
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

// POST /auth/refresh
data class RefreshTokenRequest(
    val refreshToken: String
)
```

### Product Endpoints

```kotlin
// GET /products?page=1&limit=20
// GET /products/search?query=silver&category=rings
// GET /products/{id}
// GET /products/categories
// GET /products/filters
```

### Cart Endpoints

```kotlin
// GET /cart
// POST /cart/add
data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

// PUT /cart/update/{itemId}
// DELETE /cart/remove/{itemId}
// POST /cart/clear
```

### Order Endpoints

```kotlin
// GET /orders
// GET /orders/{orderId}
// POST /orders/checkout
data class CheckoutRequest(
    val cartItems: List<CartItemDto>,
    val shippingAddress: AddressDto,
    val paymentMethod: String
)

// POST /orders/{orderId}/cancel
```

---

## 💾 Database Schema

### Core Entities

**User**
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val avatar: String?,
    val createdAt: Long
)
```

**Product**
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val images: String, // JSON array
    val rating: Float,
    val inStock: Boolean
)
```

**Cart**
```kotlin
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val quantity: Int,
    val price: Double
)
```

**Order**
```kotlin
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val totalPrice: Double,
    val status: String,
    val createdAt: Long,
    val items: String // JSON array
)
```

---

## 📋 Code Organization

### Naming Conventions

**Files & Classes**
- Classes: `PascalCase` (e.g., `ProductDetailScreen`)
- Files: Same as class name
- Interfaces: Prefix with `I` or use adjective (e.g., `ProductRepository`, `Clickable`)

**Functions & Variables**
- Functions: `camelCase` (e.g., `getProducts()`, `calculateTotal()`)
- Variables: `camelCase` (e.g., `productList`, `isLoading`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_ITEMS`, `API_TIMEOUT`)

**Composable Functions**
- Prefix with `@Composable`
- Use PascalCase (e.g., `ProductCard()`, `CartScreen()`)
- Suffix with `Preview` for preview functions

### Code Style Guide

```kotlin
// ✅ Good
fun getProductById(id: String): Flow<Result<Product>> = flow {
    try {
        val product = repository.getProduct(id)
        emit(Result.Success(product))
    } catch (e: Exception) {
        emit(Result.Error(e.message ?: "Unknown error"))
    }
}

// ❌ Bad
fun getproductbyid(id:String):Flow<Result<Product>>=flow{
try{
val product=repository.getProduct(id)
emit(Result.Success(product))
}catch(e:Exception){
emit(Result.Error(e.message?:"Unknown error"))
}
}
```

---

## 👨‍💻 Development Guide

### Adding a New Feature

1. **Create Domain Model**
   ```kotlin
   // domain/model/Feature.kt
   data class Feature(
       val id: String,
       val name: String
   )
   ```

2. **Create Repository Interface**
   ```kotlin
   // domain/repository/FeatureRepository.kt
   interface FeatureRepository {
       suspend fun getFeatures(): Result<List<Feature>>
   }
   ```

3. **Create Use Case**
   ```kotlin
   // domain/usecase/GetFeaturesUseCase.kt
   class GetFeaturesUseCase @Inject constructor(
       private val repository: FeatureRepository
   ) {
       suspend operator fun invoke(): Flow<Result<List<Feature>>> = flow {
           // Implementation
       }
   }
   ```

4. **Create DTO**
   ```kotlin
   // data/api/dto/FeatureDto.kt
   @Serializable
   data class FeatureDto(
       @SerialName("id")
       val id: String,
       @SerialName("name")
       val name: String
   )
   ```

5. **Create Repository Implementation**
   ```kotlin
   // data/repository/FeatureRepositoryImpl.kt
   class FeatureRepositoryImpl @Inject constructor(
       private val apiService: ApiService,
       private val database: AppDatabase
   ) : FeatureRepository {
       override suspend fun getFeatures(): Result<List<Feature>> {
           // Implementation
       }
   }
   ```

6. **Create ViewModel**
   ```kotlin
   // presentation/screen/feature/FeatureViewModel.kt
   @HiltViewModel
   class FeatureViewModel @Inject constructor(
       private val getFeatures: GetFeaturesUseCase
   ) : ViewModel() {
       val features: StateFlow<List<Feature>> = ...
   }
   ```

7. **Create Screen**
   ```kotlin
   // presentation/screen/feature/FeatureScreen.kt
   @Composable
   fun FeatureScreen(
       viewModel: FeatureViewModel = hiltViewModel()
   ) {
       // Implementation
   }
   ```

---

## 🧪 Testing

### Unit Tests

```kotlin
class ProductRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ProductRepository
    private val apiService: ApiService = mockk()

    @Test
    fun testGetProducts() = runTest {
        // Arrange
        coEvery { apiService.getProducts(any()) } returns listOf()

        // Act
        val result = repository.getProducts()

        // Assert
        assertEquals(emptyList(), result)
    }
}
```

### UI Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class ProductScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProductDisplayed() {
        composeTestRule.setContent {
            ProductCard(product = testProduct)
        }

        composeTestRule
            .onNodeWithText(testProduct.name)
            .assertIsDisplayed()
    }
}
```

---

## 📦 Deployment

### Build Release APK

```bash
# Sign release APK
./gradlew bundleRelease

# Sign with key
keytool -genkey -v -keystore noghresod.jks -keyalg RSA -keysize 2048
```

### Upload to Play Store

1. Sign APK with release key
2. Go to Google Play Console
3. Create release
4. Upload signed APK
5. Fill metadata and screenshots
6. Submit for review

---

## ❓ Troubleshooting

### Common Issues

**Issue: Build fails with SDK not found**
```bash
# Solution: Set SDK path
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

**Issue: Gradle sync fails**
```bash
# Solution: Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

**Issue: Compose preview not showing**
- Invalidate caches: File → Invalidate Caches
- Restart Android Studio

**Issue: Database migration error**
- Add `addMigrations()` to Room builder
- Create migration class with proper SQL

---

## 🤝 Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Pull Request Process

1. Create feature branch: `git checkout -b feature/amazing-feature`
2. Commit changes: `git commit -m 'Add amazing feature'`
3. Push to branch: `git push origin feature/amazing-feature`
4. Open Pull Request with description

---

## 📝 License

MIT License - See [LICENSE](LICENSE) file

---

**Status: ✅ Production Ready**

*For questions or issues, please open an issue on GitHub.*
