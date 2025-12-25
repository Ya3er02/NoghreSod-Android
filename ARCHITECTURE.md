# NoghreSod Android App - Architecture Guide

## 📐 Overall Architecture

This project follows **Clean Architecture** principles combined with **MVVM** pattern:

```
Presentation Layer (Jetpack Compose, ViewModel)
         ↓
Domain Layer (Use Cases, Entities, Repositories Interface)
         ↓
Data Layer (Remote API, Local Database, Repositories Implementation)
```

---

## 📂 Project Structure

```
app/src/main/kotlin/com/noghre/sod/
├── di/                          # Dependency Injection (Hilt)
│   ├── NetworkModuleEnhanced.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
│
├── domain/                      # Domain Layer (Business Logic)
│   ├── model/
│   │   ├── AppException.kt      # Exception Hierarchy
│   │   ├── Result.kt            # Result Wrapper
│   │   └── *.kt                 # Domain Models
│   ├── repository/              # Repository Interfaces
│   └── usecase/                 # Use Cases
│
├── data/                        # Data Layer
│   ├── remote/
│   │   ├── api/                 # Retrofit Services
│   │   ├── dto/                 # API DTOs
│   │   ├── interceptor/         # OkHttp Interceptors
│   │   └── security/            # Auth & Encryption
│   ├── local/
│   │   ├── database/            # Room Entities & DAOs
│   │   ├── mapper/              # Data Mappers
│   │   └── security/            # Token Management
│   └── repository/              # Repository Implementations
│
├── presentation/                # Presentation Layer
│   ├── component/               # Reusable Compose Components
│   ├── screen/                  # Full Screens
│   ├── theme/                   # Material Design 3 Theme
│   ├── viewmodel/               # ViewModels
│   └── navigation/              # Navigation Setup
│
└── util/                        # Utilities & Extensions
    ├── extension/
    ├── constant/
    └── helper/
```

---

## 🏗️ Core Components

### 1. **Exception Handling**

Using sealed class `AppException` for type-safe exception handling:

```kotlin
sealed class AppException : Exception() {
    data class NetworkError(...) : AppException()
    data class ServerError(...) : AppException()
    data class ValidationError(...) : AppException()
    data class AuthenticationError(...) : AppException()
    // ... more types
}
```

### 2. **Result Wrapper Pattern**

Functional approach to handle success/error states:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: AppException) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    fun map(...) : Result<R>
    fun flatMap(...) : Result<R>
    fun onSuccess(...) : Result<T>
    fun onError(...) : Result<T>
}
```

### 3. **Network Layer**

**Enhanced OkHttpClient** with:
- ✅ HTTP Caching (100MB)
- ✅ Request/Response Logging
- ✅ Auth Token Management
- ✅ Certificate Pinning Ready
- ✅ Connection Pooling
- ✅ Automatic Retry

**Retrofit Integration:**
```kotlin
// Automatic configuration from BuildConfig
Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### 4. **Database Layer**

**Room Database** with:
- ✅ Full-Text Search (FTS4)
- ✅ Smart Indexing
- ✅ Automatic Migrations
- ✅ Type-Safe Queries

```kotlin
// Indexed ProductEntity for performance
@Entity(
    indices = [
        Index(value = ["name"], unique = false),
        Index(value = ["category"], unique = false),
        Index(value = ["name", "category"], unique = false)
    ]
)
data class ProductEntity(...)

// FTS for search
@Fts4
data class ProductSearchFts(...)
```

### 5. **Authentication & Security**

**Enhanced Auth Interceptor:**
- ✅ JWT Token Management
- ✅ Automatic Token Refresh
- ✅ 401 Response Handling
- ✅ Secure Token Storage

```kotlin
class AuthInterceptorEnhanced {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Add Authorization header
        // Handle 401 and refresh token
        // Retry request
    }
}
```

### 6. **UI Layer - Jetpack Compose**

**Best Practices:**
- ✅ `remember` for state management
- ✅ `derivedStateOf` for computed values
- ✅ `CompositionLocal` for theme
- ✅ Preview functions for testing

```kotlin
@Composable
fun ProductCardOptimized(
    product: Product,
    onClick: (Product) -> Unit
) {
    val discountedPrice by remember(...) { derivedStateOf { ... } }
    // Optimized UI
}
```

---

## 🔄 Data Flow

```
┌─────────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                     │
│          ProductsScreen → ProductCardOptimized           │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ↓ (observes StateFlow)
┌─────────────────────────────────────────────────────────┐
│               Presentation Layer (ViewModel)            │
│  ProductsViewModel: StateFlow<Result<List<Product>>>    │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ↓ (calls)
┌─────────────────────────────────────────────────────────┐
│                 Domain Layer (Use Cases)                │
│         GetProductsUseCase: suspend () -> ...           │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ↓ (calls)
┌─────────────────────────────────────────────────────────┐
│            Data Layer (Repositories)                    │
│    ProductRepository: suspend getProducts()             │
└──────────────────┬──────────────────────────────────────┘
                   │
        ┌──────────┴───────────┐
        ↓                      ↓
┌──────────────┐       ┌──────────────┐
│ Remote (API) │       │ Local (Room) │
│  Retrofit    │       │  Database    │
└──────────────┘       └──────────────┘
```

---

## 🧪 Testing Strategy

### Unit Tests
```kotlin
// Using MockK for mocking
class GetProductsUseCaseTest {
    @Test
    fun `invoke returns success when repository returns products`() = runTest {
        // Given
        val mockProducts = listOf(...)
        coEvery { repository.getProducts(...) } returns mockProducts
        
        // When
        val result = useCase.invoke()
        
        // Then
        assertEquals(mockProducts, result)
    }
}
```

### Integration Tests (Upcoming)
- API Integration with Mock Server
- Database Migration Tests
- End-to-End UI Tests

---

## 🔐 Security Measures

✅ **API Security:**
- Certificate Pinning (ready to implement)
- HTTPS only
- Request signing
- JWT tokens with refresh mechanism

✅ **Data Security:**
- Encrypted SharedPreferences for tokens
- Database encryption (SQLCipher)
- No sensitive data in logs

✅ **Code Security:**
- ProGuard/R8 Obfuscation
- Sensitive code in BuildConfig removed
- API keys in local.properties (not committed)

---

## 📊 Build Variants

### Development
```bash
./gradlew assembleDevDebug
# Base URL: https://dev-api.noghresod.com/api/v1/
```

### Staging
```bash
./gradlew assembleStagingRelease
# Base URL: https://staging-api.noghresod.com/api/v1/
```

### Production
```bash
./gradlew assembleProductionRelease
# Base URL: https://api.noghresod.com/api/v1/
```

---

## 📈 Performance Optimizations

✅ **Network:**
- HTTP caching with smart strategies
- Connection pooling
- Gzip compression
- Request timeout: 30s

✅ **Database:**
- FTS4 for fast search
- Smart indexing on frequently queried columns
- Lazy loading with pagination

✅ **UI:**
- Compose recomposition optimization
- `remember` for state preservation
- `derivedStateOf` for computed values
- LazyColumn for large lists

✅ **Code:**
- Proguard/R8 optimization
- Resource shrinking enabled
- Unnecessary layout inflation removed

---

## 🚀 Deployment

### CI/CD Pipeline
GitHub Actions automatically:
1. Builds Debug APK
2. Runs Unit Tests
3. Performs Lint Checks
4. Uploads Artifacts
5. Builds Release APK/Bundle (on main)
6. Creates GitHub Releases

### Manual Release
```bash
# Build release APK
./gradlew assembleProductionRelease

# Build release bundle for Google Play
./gradlew bundleProductionRelease
```

---

## 📚 Dependencies

### Core
- Android SDK 34
- Kotlin 1.9.10+
- Jetpack Compose 1.6+

### Network
- Retrofit 2.11.0
- OkHttp 4.11.0
- Gson 2.10.1

### Database
- Room 2.6.0
- SQLCipher (for encryption)

### DI
- Hilt 2.48+

### UI
- Material 3
- Coil for images

### Testing
- JUnit 4
- MockK
- Coroutines Test

---

## 🔗 Useful Commands

```bash
# Clean build
./gradlew clean

# Build & run
./gradlew installDebug

# Run tests
./gradlew testDebugUnitTest

# Lint check
./gradlew lint

# Build analysis
./gradlew assembleDebug --profile

# Generate dependency graph
./gradlew app:dependencies
```

---

## 📞 Support

For issues or questions, please create an issue on GitHub.

---

**Last Updated:** 2025-12-25  
**Maintainer:** NoghreSod Development Team
