# Contributing to NoghreSod Android App

## 🎯 Code Documentation Guidelines

### KDoc Comments (Required for Public APIs)

All public classes, functions, and properties must have KDoc comments.

#### Class Documentation
```kotlin
/**
 * ProductRepository is responsible for managing product data operations.
 * It acts as a bridge between the domain layer and data layer, providing
 * a clean abstraction for data access patterns.
 *
 * The repository implements the Repository Pattern with:
 * - Network calls via Retrofit API
 * - Local database caching via Room
 * - Automatic fallback to cached data on network failure
 *
 * @see ProductRepositoryImpl
 * @since 1.0.0
 */
interface ProductRepository {
    // ...
}
```

#### Function Documentation
```kotlin
/**
 * Retrieves a paginated list of products with optional filtering.
 *
 * This function fetches products from the remote API first, and caches the result
 * in the local database for offline access. If the network request fails, it
 * automatically returns the cached data.
 *
 * @param page The page number (starting from 1) for pagination
 * @param limit Number of items per page (default 20, max 100)
 * @param category Optional category filter. If null, no filtering is applied.
 *
 * @return List of [Product] objects for the requested page
 *
 * @throws AppException.NetworkError if no internet connection and cache is empty
 * @throws AppException.ServerError if server returns 5xx error
 *
 * @since 1.0.0
 * @see Product
 *
 * Example:
 * ```
 * val products = repository.getProducts(page = 1, limit = 20, category = "Electronics")
 * ```
 */
suspend fun getProducts(
    page: Int,
    limit: Int = 20,
    category: String? = null
): List<Product>
```

#### Property Documentation
```kotlin
/**
 * The current UI state representing products list view.
 *
 * This StateFlow emits:
 * - [UiState.Loading] when fetching products
 * - [UiState.Success] when products are loaded successfully
 * - [UiState.Error] when an error occurs
 * - [UiState.Empty] when no products are found
 */
val uiState: StateFlow<UiState<List<Product>>>
```

### Inline Comments

Use inline comments sparingly for complex logic only.

```kotlin
// ❌ AVOID - Too obvious
val name = product.name // Get product name

// ✅ GOOD - Explains WHY, not WHAT
// Cache key includes category to enable category-specific offline access
val cacheKey = "products_${category}_page_$page"
```

### Code Examples in Documentation

Include practical usage examples in KDoc:

```kotlin
/**
 * Filters products by price range.
 *
 * Example:
 * ```kotlin
 * val products = repository.getProductsByPriceRange(
 *     minPrice = 50.0,
 *     maxPrice = 200.0
 * )
 * products.forEach { println(it.name) }
 * ```
 */
suspend fun getProductsByPriceRange(
    minPrice: Double,
    maxPrice: Double
): List<Product>
```

---

## 📋 Code Style Guidelines

### Naming Conventions

```kotlin
// Classes: PascalCase
class ProductRepository

// Functions/Variables: camelCase
fun getProductById(id: String)
val productName: String

// Constants: UPPER_SNAKE_CASE
const val MAX_PAGE_SIZE = 100

// Private properties: _leading underscore
private val _products = MutableStateFlow<List<Product>>()
```

### Function Organization

Organize functions in logical sections:

```kotlin
class ProductViewModel : ViewModel() {
    
    // ============== PUBLIC API ============== //
    fun loadProducts() { ... }
    fun selectProduct(product: Product) { ... }
    
    // ============== PRIVATE FUNCTIONS ============== //
    private fun fetchProducts() { ... }
    private fun handleError(exception: Exception) { ... }
}
```

### Error Handling

Use sealed classes for type-safe error handling:

```kotlin
// ❌ AVOID
try {
    val data = api.getData()
} catch (e: Exception) {
    Log.e("Error", e.message)
}

// ✅ GOOD
val result = safeExecute {
    api.getData()
}
when (result) {
    is Result.Success -> { /* handle success */ }
    is Result.Error -> { /* handle specific error */ }
}
```

---

## 🧪 Testing Standards

### Unit Test Documentation

```kotlin
/**
 * Tests for ProductRepository.getProducts() functionality.
 * Covers:
 * - Successful product retrieval
 * - Pagination
 * - Offline cache fallback
 * - Network error handling
 */
class ProductRepositoryTest {
    
    @Test
    fun `getProducts returns paginated list when successful`() {
        // Given - setup
        val expectedProducts = listOf(mockProduct1, mockProduct2)
        
        // When - action
        val result = repository.getProducts(page = 1, limit = 20)
        
        // Then - assertion
        assertEquals(expectedProducts, result)
    }
}
```

### Test Naming

Use descriptive names following the pattern:
`<functionName>_<scenario>_<expected result>`

```kotlin
@Test
fun getProducts_withValidPage_returnsPaginatedList() { }

@Test
fun getProducts_withNetworkError_returnsCachedData() { }

@Test
fun getProducts_withEmptyCategory_returnsAllProducts() { }
```

---

## 📝 Commit Message Guidelines

Follow conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring without feature changes
- `docs`: Documentation updates
- `test`: Test additions/modifications
- `ci`: CI/CD changes
- `chore`: Build, dependencies, etc.

### Examples

```
feat(products): Add pagination support to product listing

Implement offset-based pagination with configurable page size.
Add new getProducts(page, limit) API endpoint.
Include caching strategy for pagination.

Closes #123
```

```
fix(auth): Handle token refresh on 401 response

Previously, 401 responses would always log user out.
Now attempts to refresh token automatically before logout.

Fixes #456
```

---

## 🏗️ Architecture Guidelines

### Layer Responsibilities

**Presentation Layer:**
- UI rendering only
- State management via ViewModel
- NO business logic

**Domain Layer:**
- Business logic
- Use cases
- Repository interfaces

**Data Layer:**
- Remote API calls
- Local database
- Repository implementations

### Import Organization

```kotlin
// 1. Android imports
import android.content.Context

// 2. AndroidX imports
import androidx.compose.runtime.Composable

// 3. Kotlin stdlib
import kotlinx.coroutines.launch

// 4. Third-party libraries
import retrofit2.http.GET

// 5. Project imports
import com.noghre.sod.domain.model.Product
```

---

## ✅ Pre-Commit Checklist

Before committing:

- [ ] Code follows naming conventions
- [ ] Public APIs have KDoc comments
- [ ] No `TODO` comments left behind
- [ ] No debugging code (Log, println)
- [ ] Tests pass locally
- [ ] No API keys or secrets in code
- [ ] Commit message follows convention
- [ ] Code formatted with Kotlin style guide

---

## 🔍 Code Review Process

All code must pass:

1. **Lint Checks**: `./gradlew lint`
2. **Unit Tests**: `./gradlew testDebugUnitTest`
3. **Integration Tests**: `./gradlew connectedAndroidTest`
4. **Peer Review**: At least one approval

---

## 📚 Useful Resources

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Architecture Guide](https://developer.android.com/guide/architecture)
- [KDoc Format](https://kotlinlang.org/docs/kotlin-doc.html)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

**Last Updated**: 2025-12-25  
**Maintainer**: NoghreSod Development Team
