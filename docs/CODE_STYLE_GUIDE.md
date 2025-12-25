# 📄 NoghreSod Android - Code Style Guide

**Version:** 1.0.0  
**Last Updated:** 2025-12-25  
**Standard:** Google Android Kotlin Style Guide (KTLint)

---

## Table of Contents

1. [Kotlin/Java Naming Conventions](#naming-conventions)
2. [File Organization](#file-organization)
3. [Class/Interface Design](#class-interface-design)
4. [Function Guidelines](#function-guidelines)
5. [Formatting](#formatting)
6. [Comments and Documentation](#comments-documentation)
7. [Testing](#testing)
8. [Best Practices](#best-practices)

---

## Naming Conventions

### Package Names

Package names are **lowercase**, following reverse domain notation:

```kotlin
// ✅ CORRECT
package com.noghre.sod.ui.screens
package com.noghre.sod.data.api

// ❌ INCORRECT
package com.noghre.sod.UI.screens
package Com.Noghre.Sod.Data.Api
```

### Class Names

Class names use **PascalCase** (UpperCamelCase):

```kotlin
// ✅ CORRECT
class ProductViewModel
class CertificatePinningManager
interface PaymentRepository

// ❌ INCORRECT
class product_view_model
class certificatePinningManager
interface payment_repository
```

**Naming conventions by type:**

| Type | Suffix | Example |
|------|--------|----------|
| ViewModel | `ViewModel` | `ProductViewModel` |
| Repository | `Repository` | `ProductRepository` |
| UseCase | `UseCase` | `GetProductsUseCase` |
| Service | `Service` | `PaymentService` |
| Manager | `Manager` | `BiometricAuthManager` |
| Adapter | `Adapter` | `ProductListAdapter` |
| Listener | `Listener` or `Callback` | `OnProductClickListener` |
| Composable | `Screen` | `ProductDetailScreen` |

### Function/Method Names

Function names use **camelCase** (lowerCamelCase) and should be **verbs**:

```kotlin
// ✅ CORRECT
fun loadProducts() { }
fun saveUserToken(token: String) { }
fun validateEmail(email: String): Boolean { }
fun onProductClicked(productId: String) { }

// ❌ INCORRECT
fun LoadProducts() { }               // PascalCase
fun load_products() { }              // snake_case
fun products() { }                   // Not a verb
fun product_clicked(id: String) { } // snake_case
```

### Variable Names

Variable names use **camelCase** and should be **descriptive**:

```kotlin
// ✅ CORRECT
val userName: String = "Ali Rezaei"
var userCount: Int = 0
val isProductAvailable: Boolean = true

// ❌ INCORRECT
val user_name: String                // snake_case
var u: Int = 0                       // Too short
val available: Boolean               // Ambiguous
```

### Constants

Constants use **UPPER_SNAKE_CASE**:

```kotlin
// ✅ CORRECT
companion object {
    private const val REQUEST_TIMEOUT_SECONDS = 30
    private const val MAX_RETRY_COUNT = 3
    private const val API_BASE_URL = "https://api.noghresod.ir"
}

// ❌ INCORRECT
companion object {
    private const val requestTimeoutSeconds = 30  // camelCase
    private const val Max_Retry_Count = 3         // PascalCase
}
```

### Private Fields

Private fields start with **underscore** for reactive properties:

```kotlin
// ✅ CORRECT
class ProductViewModel {
    private val _productsState = MutableStateFlow<UiState>(UiState.Loading)
    val productsState: StateFlow<UiState> = _productsState.asStateFlow()
}

// ❌ INCORRECT
class ProductViewModel {
    val _productsState = MutableStateFlow<UiState>(UiState.Loading)
    private val productsState: StateFlow<UiState> = _productsState.asStateFlow()
}
```

---

## File Organization

### File Structure

```kotlin
// 1. Package declaration
package com.noghre.sod.ui.screens

// 2. Imports (organized)
import android.app.Application
import android.os.Build
import androidx.compose.runtime.Composable
import com.noghre.sod.domain.entities.Product
import com.noghre.sod.data.api.ProductApi
import kotlinx.coroutines.launch
import javax.inject.Inject

// 3. Constants and top-level declarations
private const val TAG = "ProductScreen"

// 4. Main class/interface
class ProductViewModel {
    // ... implementation
}

// 5. Supporting classes/interfaces at end
data class UiState(...) { }
interface ProductListener { }
```

### Import Ordering

1. Platform imports (android, androidx, kotlinx)
2. Third-party imports
3. App imports
4. Alphabetically within each group

```kotlin
// ✅ CORRECT
import android.os.Build
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.noghre.sod.domain.entities.Product
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
```

---

## Class/Interface Design

### Single Responsibility

Each class should have **one reason to change**:

```kotlin
// ✅ CORRECT - Single responsibility
class ProductRepository @Inject constructor(
    private val productApi: ProductApi
) {
    suspend fun getProducts(): Result<List<Product>> { }
}

// ❌ INCORRECT - Multiple responsibilities
class ProductRepository {
    fun getProducts() { }        // Fetch from API
    fun saveToDatabase() { }     // Save to DB
    fun uploadToAnalytics() { }  // Track analytics
    fun formatPrice() { }        // Format for UI
}
```

### Access Modifiers

Use most restrictive access modifiers first:

```kotlin
// ✅ CORRECT
class ViewModel {
    private val repository: Repository  // Private by default
    internal val analytics: Analytics   // Internal across module
    protected val logger: Logger        // Protected in hierarchy
    val public: String                  // Public (default)
}

// ❌ INCORRECT
class ViewModel {
    val repository: Repository  // Unnecessarily public
    val analytics: Analytics    // Unnecessarily public
}
```

### Dependency Injection

Use Hilt for constructor injection:

```kotlin
// ✅ CORRECT
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() { }

// ❌ INCORRECT
class ProductViewModel {
    private val repository = ProductRepository()  // Hard dependency
    private val analytics = Analytics()           // Service locator
}
```

---

## Function Guidelines

### Function Length

Functions should be **short and focused** (ideally < 20 lines):

```kotlin
// ✅ CORRECT - Short, focused
fun validateEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".")
}

// ❌ INCORRECT - Too long
fun validateEmail(email: String): Boolean {
    if (email.isEmpty()) return false
    if (!email.contains("@")) return false
    val parts = email.split("@")
    if (parts.size != 2) return false
    val domain = parts[1]
    if (domain.isEmpty()) return false
    if (!domain.contains(".")) return false
    // ... 50+ more lines
}
```

### Parameters

Limit function parameters to **3-4 maximum**. Use data class for more:

```kotlin
// ✅ CORRECT
fun createOrder(
    cartId: String,
    shippingAddress: Address,
    paymentMethod: PaymentMethod
) { }

// ❌ INCORRECT - Too many parameters
fun createOrder(
    cartId: String,
    name: String,
    email: String,
    phone: String,
    address: String,
    city: String,
    province: String,
    zipCode: String,
    paymentMethod: String
) { }
```

### Return Types

Always specify explicit return types:

```kotlin
// ✅ CORRECT
fun getProductName(): String { return "Necklace" }
fun isUserLoggedIn(): Boolean { return true }

// ❌ INCORRECT
fun getProductName() = "Necklace"    // Implicit type
fun isUserLoggedIn() = true          // Implicit type
```

### Error Handling

Use Result type or exceptions, avoid null:

```kotlin
// ✅ CORRECT - Result type
suspend fun getProduct(id: String): Result<Product> {
    return try {
        Result.Success(api.getProduct(id))
    } catch (e: Exception) {
        Result.Failure(e)
    }
}

// ✅ CORRECT - Sealed Result
seal class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val error: Throwable) : Result<T>()
}

// ❌ INCORRECT - Null checking
fun getProduct(id: String): Product? {
    return try { api.getProduct(id) } catch(e: Exception) { null }
}
```

---

## Formatting

### Line Length

Maximum **100 characters** per line (soft limit, 120 hard limit):

```kotlin
// ✅ CORRECT
analytics.trackEvent("product_viewed", mapOf(
    "product_id" to productId,
    "category" to category
))

// ❌ INCORRECT - Too long
analytics.trackEvent("product_viewed", mapOf("product_id" to productId, "category" to category))
```

### Indentation

Use **4 spaces** per indentation level:

```kotlin
// ✅ CORRECT
class MyClass {
    private val property: String = ""
    
    fun myMethod() {
        if (condition) {
            doSomething()
        }
    }
}

// ❌ INCORRECT - 2 spaces or tabs
class MyClass {
  private val property: String = ""
}
```

### Spacing

```kotlin
// ✅ CORRECT
val x = 1 + 2      // Space around operators
fun test() { }     // Space before braces
if (condition) { } // Space after keywords

// ❌ INCORRECT
val x=1+2          // No spaces
fun test(){}       // No space
if(condition){}    // No space
```

### Blank Lines

```kotlin
class Example {
    private val property1: String = ""
    private val property2: String = ""
    
    // Blank line before function
    fun method1() { }
    
    fun method2() { }
}
```

---

## Comments and Documentation

### KDoc Comments

Document public API with **KDoc** comments:

```kotlin
/**
 * 📄 Loads products from the API
 *
 * Fetches paginated list of products with optional filtering
 * and caches results locally.
 *
 * @param page Page number (starting from 1)
 * @param pageSize Number of items per page
 * @return List of products or error result
 * @throws NetworkException if API request fails
 *
 * @sample loadProductsSample
 *
 * @see Product
 * @see ProductRepository
 *
 * @since 1.0.0
 */
suspend fun loadProducts(
    page: Int = 1,
    pageSize: Int = 20
): Result<List<Product>> { }
```

### Inline Comments

Use inline comments for **why**, not **what**:

```kotlin
// ✅ CORRECT - Explains WHY
// Retry up to 3 times for network timeouts (temporary issues)
var retryCount = 0
while (retryCount < 3) {
    try {
        return api.fetchData()
    } catch (e: TimeoutException) {
        retryCount++
    }
}

// ❌ INCORRECT - Explains WHAT (obvious from code)
// Increment retry count
retryCount++

// Try API call
return api.fetchData()
```

### TODO Comments

Use TODO/FIXME sparingly, always with context:

```kotlin
// ✅ CORRECT
// TODO: Implement biometric authentication (JIRA-123)
// Blocked by security team review
fun authenticateUser() { }

// ❌ INCORRECT
// TODO: Fix this
fun authenticateUser() { }
```

---

## Testing

### Test Naming

Test methods follow **givenWhenThen** or **subjectWhenAction** pattern:

```kotlin
// ✅ CORRECT - Clear what is being tested
@Test
fun givenValidEmail_whenValidating_thenReturnTrue() { }

@Test
fun emailValidator_withValidEmail_shouldReturnTrue() { }

// ❌ INCORRECT
@Test
fun test() { }

@Test
fun emailTest() { }
```

### Test Organization

```kotlin
class ProductViewModelTest {
    
    private lateinit var viewModel: ProductViewModel
    private lateinit var mockRepository: MockProductRepository
    
    @Before
    fun setUp() {
        // Setup test fixtures
    }
    
    @After
    fun tearDown() {
        // Cleanup
    }
    
    // Given-When-Then structure
    @Test
    fun loadProducts_withValidPage_shouldSucceed() {
        // Given
        val expectedProducts = listOf(testProduct)
        mockRepository.setProducts(expectedProducts)
        
        // When
        viewModel.loadProducts(page = 1)
        
        // Then
        assertEquals(UiState.Success(expectedProducts), viewModel.state.value)
    }
}
```

---

## Best Practices

### 1. Use Sealed Classes

```kotlin
// ✅ CORRECT - Type-safe
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: Product) : UiState()
    data class Error(val message: String) : UiState()
}

when (state) {
    is UiState.Loading -> showLoader()
    is UiState.Success -> showProduct(state.data)
    is UiState.Error -> showError(state.message)
}
```

### 2. Use Extension Functions

```kotlin
// ✅ CORRECT - More readable
fun String.isValidEmail(): Boolean = this.contains("@")
fun List<Product>.totalPrice(): Long = this.sumOf { it.price }

// Usage
if (email.isValidEmail()) { }
val total = products.totalPrice()
```

### 3. Use Scope Functions

```kotlin
// ✅ CORRECT
Product().apply {
    id = "123"
    name = "گردنبند"
}.let { analyticsTracker.trackProductView(it) }

// ✅ CORRECT - Safe navigation
user?.let {
    saveUserToken(it.token)
    updateUserProfile(it)
}
```

### 4. Avoid Magic Numbers

```kotlin
// ✅ CORRECT
private const val REQUEST_TIMEOUT_SECONDS = 30
val timeoutMillis = REQUEST_TIMEOUT_SECONDS * 1000

// ❌ INCORRECT
val timeoutMillis = 30000  // Magic number
```

### 5. Use Data Classes

```kotlin
// ✅ CORRECT
data class Product(
    val id: String,
    val name: String,
    val price: Long
)

// Automatically gets: equals, hashCode, toString, copy
```

---

## Tools & Configuration

### KTLint Setup

```gradle
plugins {
    id "org.jlleitschuh.gradle.ktlint" version "11.5.1"
}

ktlint {
    version = "0.48.2"
    android = true
    ignoreFailures = false
    reporters {
        reporter "plain"
        reporter "checkstyle"
    }
}
```

### Run Formatting

```bash
# Check style
./gradlew ktlintCheck

# Auto-format
./gradlew ktlintFormat
```

---

**References:**
- [Google Android Kotlin Style Guide](https://android.github.io/kotlin-guides/style-guide.html)
- [KTLint Documentation](https://ktlint.github.io/)
- [Kotlin Official Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
