# 🚀 NoghreSod Android - Implementation Guide

**Version:** 2.0 (Enterprise Edition)
**Status:** Production Ready
**Date:** December 27, 2025

---

## 📊 Table of Contents

1. [Project Structure](#-project-structure)
2. [Module-by-Module Implementation](#-module-by-module-implementation)
3. [Integration Steps](#-integration-steps)
4. [Code Examples](#-code-examples)
5. [Testing Guide](#-testing-guide)
6. [Troubleshooting](#-troubleshooting)
7. [Performance Tips](#-performance-tips)

---

## 📁 Project Structure

```
app/
├── src/
│  ├── main/
│  │  ├── kotlin/com/noghre/sod/
│  │  │  ├── di/                    # Dependency Injection Modules
│  │  │  ├── presentation/         # UI Layer
│  │  │  │  ├── navigation/       # Type-safe routes
│  │  │  │  ├── viewmodel/       # Advanced ViewModels
│  │  │  │  ├── compose/         # Reusable composables
│  │  │  │  └── screens/         # Screen implementations
│  │  │  ├── domain/            # Business Logic Layer
│  │  │  │  ├── usecase/         # Use cases
│  │  │  └── repository/      # Repository interfaces
│  │  │  ├── data/              # Data Layer
│  │  │  │  ├── network/         # API service & responses
│  │  │  │  └── local/           # Database & local storage
│  └── test/
│     ├── kotlin/com/noghre/sod/
│        ├── util/               # Test utilities
└── build.gradle.kts
```

---

## 💬 Module-by-Module Implementation

### 1. Type-Safe Navigation Module

**Location:** `presentation/navigation/Routes.kt`

**Key Components:**
- `Route` - Sealed interface for all navigation destinations
- `NavGraph` - Nested navigation graph definitions

**Usage Pattern:**
```kotlin
// Define routes
sealed interface Route {
    data object Home : Route
    data class ProductDetail(val productId: String) : Route
    data class Search(val query: String) : Route
}

// Navigate
navController.navigate(Route.ProductDetail("123"))

// Handle deep links
val route = Route.ProductList(categoryId = "silver")
```

### 2. Advanced ViewModel

**Location:** `presentation/viewmodel/AdvancedBaseViewModel.kt`

**Key Features:**
- Type-safe state management
- Event-driven side effects
- Built-in error handling
- Navigation state

**Implementation Example:**
```kotlin
data class ProductState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ProductEvent {
    data class ShowToast(val message: String) : ProductEvent()
    data class ShowSnackbar(val message: String) : ProductEvent()
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : AdvancedBaseViewModel<ProductState, ProductEvent>() {
    
    override fun getInitialState() = ProductState()
    
    fun loadProduct(id: String) {
        executeAsync(
            task = { productRepository.getProduct(id) },
            onSuccess = { product ->
                updateState { copy(product = product, isLoading = false) }
            },
            onError = { error ->
                updateState { copy(error = error.message) }
            }
        )
    }
}
```

### 3. Jetpack Compose Utilities

**Location:** `presentation/compose/ComposeUtils.kt`

**Available Components:**
```kotlin
// Loading states
LoadingIndicator()
SkeletonLoader()

// Error handling
ErrorMessage(message, onRetry = {})
EmptyState(title, subtitle)

// Images
NetworkImage(url, contentDescription)

// Animations
FadeInOutAnimation(visible = true) { content() }
SlideUpAnimation(visible = true) { content() }

// Layout helpers
SafeAreaPadding { }
CenterColumn { }
ResponsiveGrid(columns = 2) { }
```

### 4. Network Layer

**Location:** `data/network/ApiResponseWrapper.kt`

**Response Handling:**
```kotlin
sealed class ApiResponse<out T : Any> {
    data class Success<T : Any>(val data: T, val code: Int = 200)
    data class Error<T : Any>(val message: String, val code: Int = 500)
    data class Loading<T : Any>(val isLoading: Boolean = true)
    data class NetworkError<T : Any>(val message: String, val exception: IOException?)
}

// Usage
val response: ApiResponse<List<Product>> = safeApiCall {
    apiService.getProducts()
}

response.handle(
    onSuccess = { products -> updateUI(products) },
    onError = { error -> showError(error) },
    onLoading = { showLoading() }
)
```

### 5. Repository Pattern

**Location:** `data/local/repository/BaseRepository.kt`

**Implementation:**
```kotlin
class ProductRepository(
    private val apiService: ProductApiService,
    private val productDao: ProductDao
) : BaseRepository<Product>() {
    
    fun getProducts(): Flow<RepositoryResult<List<Product>>> {
        return fetchWithCache(
            key = "products",
            remoteCall = { apiService.getProducts() },
            mapper = { response -> response.data }
        )
    }
    
    fun searchProducts(query: String): Flow<RepositoryResult<List<Product>>> {
        return fetchWithCache(
            key = "search_$query",
            remoteCall = { apiService.search(query) }
        )
    }
    
    override suspend fun onSaveLocal(data: Product, key: String) {
        productDao.insert(data)
    }
}

// Usage in ViewModel
productRepository.getProducts().collect { result ->
    result.handle(
        onSuccess = { products, isCached ->
            updateState { copy(products = products) }
        },
        onError = { error ->
            updateState { copy(error = error) }
        }
    )
}
```

### 6. Dependency Injection

**Location:** `di/AnalyticsModule.kt`

**Setup:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    
    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        analytics: FirebaseAnalytics,
        crashlytics: FirebaseCrashlytics
    ): AnalyticsTracker {
        return AnalyticsTrackerImpl(analytics, crashlytics)
    }
}

// Inject in ViewModel
@HiltViewModel
class MyViewModel @Inject constructor(
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    
    fun onUserAction(action: String) {
        analyticsTracker.trackEvent(
            "user_action",
            mapOf("action" to action)
        )
    }
}
```

### 7. Testing Framework

**Location:** `test/kotlin/com/noghre/sod/util/TestDataBuilder.kt`

**Test Examples:**
```kotlin
// Create test data
val testProduct = TestDataBuilder.product {
    name = "Silver Ring"
    price = 299.99
    rating = 4.8
}

val testUser = TestDataBuilder.user {
    email = "test@example.com"
    username = "testuser"
}

// Use in tests
class ProductViewModelTest {
    
    @Test
    fun loadProduct_withValidId_returnsProduct() {
        val viewModel = ProductViewModel(repository)
        val testProduct = TestDataBuilder.product()
        
        viewModel.loadProduct(testProduct["id"] as String)
        
        assertEquals(testProduct["name"], viewModel.state.value.product?.name)
    }
}
```

---

## 💻 Integration Steps

### Step 1: Update Build Configuration
```kotlin
// build.gradle.kts
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.dagger.hilt.android") version "2.48"
    id("com.google.gms.google-services")
}

android {
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:latest")
    implementation("androidx.compose.material3:material3:latest")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:latest")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:latest")
    
    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Database
    implementation("androidx.room:room-runtime:latest")
    kapt("androidx.room:room-compiler:latest")
    
    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
```

### Step 2: Create Application Class
```kotlin
@HiltAndroidApp
class NoghreSodApp : Application()
```

### Step 3: Update AndroidManifest.xml
```xml
<application
    android:name=".NoghreSodApp"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name">
    
    <activity
        android:name=".MainActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

### Step 4: Setup Navigation
```kotlin
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home::class.qualifiedName!!
    ) {
        composable<Route.Home> {
            HomeScreen(navController)
        }
        composable<Route.ProductDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.ProductDetail>()
            ProductDetailScreen(productId = route.productId, navController)
        }
    }
}
```

---

## 💫 Code Examples

### Example 1: Complete Product Screen
```kotlin
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    val error = viewModel.errorEvents.collectAsStateWithLifecycle()
    
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }
    
    when {
        loading.value -> LoadingIndicator()
        error.value != null -> ErrorMessage(
            message = error.value ?: "Unknown error",
            onRetry = { viewModel.loadProduct(productId) }
        )
        state.value.product != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                NetworkImage(
                    url = state.value.product!!.imageUrl,
                    contentDescription = "Product image"
                )
                Text(state.value.product!!.name)
                Text("Price: ${state.value.product!!.price}")
                Button(onClick = {
                    viewModel.addToCart(state.value.product!!)
                    navController.navigate(Route.Cart)
                }) {
                    Text("Add to Cart")
                }
            }
        }
    }
}
```

### Example 2: Test a ViewModel
```kotlin
@RunWith(AndroidTestRunner::class)
class ProductDetailViewModelTest {
    
    private lateinit var viewModel: ProductDetailViewModel
    private val repository = mock<ProductRepository>()
    
    @Before
    fun setup() {
        viewModel = ProductDetailViewModel(repository)
    }
    
    @Test
    fun `loadProduct should update state with product`() = runTest {
        val testProduct = TestDataBuilder.product()
        whenever(repository.getProduct(any()))
            .thenReturn(ApiResponse.Success(testProduct))
        
        viewModel.loadProduct("123")
        
        assertEquals(testProduct["name"], viewModel.state.value.product?.name)
    }
}
```

---

## 🛠️ Troubleshooting

### Issue: Navigation not working
**Solution:** Ensure Route classes have `@Serializable` annotation

### Issue: ViewModel state not updating
**Solution:** Use `updateState { copy(...) }` instead of direct assignment

### Issue: Network requests timing out
**Solution:** Adjust OkHttp timeout in NetworkModule

### Issue: Cache not working
**Solution:** Check TTL duration and cache key consistency

---

## 🚀 Performance Tips

1. **Use remember() for expensive computations**
2. **Implement proper pagination for large lists**
3. **Enable ProGuard/R8 for release builds**
4. **Use LazyColumn for large lists**
5. **Implement image caching with Coil**
6. **Monitor memory usage in production**

---

**Ready to build! ✨**
