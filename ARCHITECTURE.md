# 🎯 Architecture Guide - NoghreSod Android

**Comprehensive guide to application architecture, design patterns, and technology decisions.**

- **Pattern:** Clean Architecture + MVVM + Offline-First
- **Status:** Production-Ready
- **Last Updated:** December 28, 2025

---

## Table of Contents

1. [Overview](#overview)
2. [Clean Architecture Layers](#clean-architecture-layers)
3. [Design Patterns](#design-patterns)
4. [Dependency Injection](#dependency-injection)
5. [Data Flow](#data-flow)
6. [Key Components](#key-components)
7. [Offline-First Strategy](#offline-first-strategy)
8. [Technology Stack](#technology-stack)
9. [Architectural Decisions](#architectural-decisions)

---

## Overview

### Architecture Philosophy

NoghreSod follows **Clean Architecture** principles to achieve:
- ✅ **Testability** - Easy to test each layer independently
- ✅ **Maintainability** - Clear separation of concerns
- ✅ **Scalability** - Easy to add new features
- ✅ **Independence** - UI, Database, Network changes don't break business logic

### Layered Structure

```
┌───────────────────────────────────┐
│  PRESENTATION LAYER                        │
│  (User Interface, States, Events)          │
├───────────────────────────────────┤
│  DOMAIN LAYER                              │
│  (Business Logic, UseCases, Entities)     │
├───────────────────────────────────┤
│  DATA LAYER                                │
│  (Repository, Room, Network, Cache)       │
└───────────────────────────────────┘
```

---

## Clean Architecture Layers

### 1. Presentation Layer

**Responsibility:** UI rendering and user interactions

**Components:**
- **Composables** - Jetpack Compose UI components
- **ViewModels** - State management and business logic coordination
- **States** - UI state representation (sealed classes)
- **Events** - User interactions (sealed classes)
- **Screens** - Full page compositions

**Technology:**
- Jetpack Compose (Material 3)
- Material Design System
- Kotlin Coroutines

**Example Structure:**
```kotlin
// Screen
@Composable
fun ProductsScreen(viewModel: ProductsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is ProductsUiState.Loading -> LoadingShimmer()
        is ProductsUiState.Success -> ProductList(state.products)
        is ProductsUiState.Error -> ErrorDialog(state.message)
    }
}

// ViewModel
class ProductsViewModel : ViewModel() {
    val uiState = getProductsUseCase()
        .map { ProductsUiState.Success(it) }
        .stateIn(scope, SharingStarted.WhileSubscribed())
}
```

**Key Principles:**
- No business logic in UI
- State as immutable data classes
- Events for user interactions
- Observable state flows

---

### 2. Domain Layer

**Responsibility:** Business logic and core application rules

**Components:**
- **UseCases** - Represent business operations
- **Entities** - Core business objects (immutable)
- **Repositories (Interfaces)** - Data source abstraction
- **Exceptions** - Domain-specific errors

**Technology:**
- Pure Kotlin (no Android dependencies)
- Sealed classes for type safety
- Extension functions

**Example Structure:**
```kotlin
// UseCase
class GetProductsUseCase(
    private val productsRepository: ProductsRepository,
    private val filterRepository: FilterRepository
) {
    operator fun invoke(
        filters: List<Filter> = emptyList()
    ): Flow<List<ProductEntity>> = flow {
        val products = productsRepository.getProducts()
        emit(filterProducts(products, filters))
    }
}

// Entity
data class ProductEntity(
    val id: String,
    val name: String,
    val price: Double,
    val weight: Double,
    val hallmark: String // 925 silver mark
)

// Repository Interface
interface ProductsRepository {
    suspend fun getProducts(): List<ProductEntity>
    suspend fun getProduct(id: String): ProductEntity
}
```

**Key Principles:**
- Independent of UI framework
- Independent of data source
- Reusable across different platforms
- No external dependencies (besides Kotlin stdlib)

---

### 3. Data Layer

**Responsibility:** Data access and persistence

**Components:**
- **Repositories (Implementations)** - Coordinate data sources
- **Local Data Source** - Room database (offline cache)
- **Remote Data Source** - Retrofit API calls
- **Data Models** - DTOs, Room entities
- **Mappers** - Convert between models

**Technology:**
- Room Database (SQLite)
- Retrofit + OkHttp
- Moshi/Gson
- DataStore

**Example Structure:**
```kotlin
// Repository Implementation (Offline-First)
class ProductsRepositoryImpl(
    private val localDataSource: ProductsLocalDataSource,
    private val remoteDataSource: ProductsRemoteDataSource
) : ProductsRepository {
    override suspend fun getProducts(): List<ProductEntity> {
        // Try local first (offline)
        val cachedProducts = localDataSource.getProducts()
        if (cachedProducts.isNotEmpty()) {
            return cachedProducts.toEntities()
        }
        
        // Fetch from network if no cache
        val remoteProducts = remoteDataSource.getProducts()
        localDataSource.save(remoteProducts)
        return remoteProducts.toEntities()
    }
}

// Room Entity
@Entity(tableName = "products")
data class ProductDbEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double
)

// API DTO
data class ProductDto(
    val id: String,
    val name: String,
    val price: Double
)
```

**Key Principles:**
- Multiple data sources (local + remote)
- Offline-first by default
- Automatic sync when online
- Type-safe database queries

---

## Design Patterns

### 1. MVVM (Model-View-ViewModel)

**Purpose:** Separate UI logic from business logic

```
UI (View) ↔︎ ViewModel ↔︎ Repository ↔︎ Data Source
  │            │              │                  │
  │       observes state   coordinates data    fetches/saves
  │            │              │                  │
```

**Benefits:**
- ViewModel survives configuration changes
- Easy to test (state is observable)
- Clear separation between UI and logic

### 2. Repository Pattern

**Purpose:** Abstract data sources behind a common interface

```kotlin
// Abstraction
interface ProductsRepository {
    suspend fun getProducts(): List<ProductEntity>
}

// Multiple implementations
class ProductsRepositoryImpl(
    private val localDataSource: ProductsLocalDataSource,
    private val remoteDataSource: ProductsRemoteDataSource
) : ProductsRepository

// Can easily swap implementations for testing
class FakeProductsRepository : ProductsRepository
```

**Benefits:**
- Switch data sources without changing business logic
- Easy to mock for testing
- Follows Dependency Inversion Principle

### 3. UseCase Pattern

**Purpose:** Represent distinct business operations

```kotlin
// Each operation is a separate class
class GetProductsUseCase(repository: ProductsRepository)
class FilterProductsUseCase(repository: ProductsRepository)
class AddToCartUseCase(cartRepository: CartRepository)

// Composable
class CheckoutUseCase(
    private val getCartUseCase: GetCartUseCase,
    private val calculateTotalUseCase: CalculateTotalUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase
)
```

**Benefits:**
- Single Responsibility
- Reusable across multiple ViewModels
- Easy to test in isolation

### 4. Sealed Classes for Type Safety

**Purpose:** Represent restricted type hierarchies

```kotlin
// UI State
sealed class ProductsUiState {
    object Loading : ProductsUiState()
    data class Success(val products: List<Product>) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
}

// When expression is exhaustive (compiler checks)
when (uiState) {
    is ProductsUiState.Loading -> { /* ... */ }
    is ProductsUiState.Success -> { /* ... */ }
    is ProductsUiState.Error -> { /* ... */ }
}
```

---

## Dependency Injection

### Hilt Framework

**Purpose:** Manage object creation and dependencies

```kotlin
// 1. Provide dependencies
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .build()
            .create(ApiService::class.java)
    }
    
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "noghre_sod.db"
        ).build()
    }
}

// 2. Inject where needed
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel()

// 3. Mark activities
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

**Benefits:**
- Automatic dependency resolution
- Easy to swap implementations (for testing)
- Compile-time safety
- Reduces boilerplate

---

## Data Flow

### Unidirectional Data Flow (MVI-like)

```
┌───────────────────────┐
│      User Interaction                │
│      (Click, Input, etc.)            │
└───────────────────────┘
         ⬇︎
┌───────────────────────┐
│   ViewModel Handles Event             │
│   (Updates state, calls UseCase)      │
└───────────────────────┘
         ⬇︎
┌───────────────────────┐
│   UseCase Fetches Data                │
│   (From Repository)                   │
└───────────────────────┘
         ⬇︎
┌───────────────────────┐
│   Repository Accesses Data            │
│   (Local/Remote)                       │
└───────────────────────┘
         ⬇︎
┌───────────────────────┐
│   State Updated in ViewModel          │
└───────────────────────┘
         ⬇︎
┌───────────────────────┐
│   Compose Recomposes with New State   │
│   (UI Updated)                        │
└───────────────────────┘
```

**Benefits:**
- Predictable state changes
- Easy to debug
- Time-travel debugging possible
- Testable

---

## Key Components

### ViewModels

```kotlin
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProductsUiState>(
        ProductsUiState.Loading
    )
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    
    init {
        loadProducts()
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase()
                .onStart { _uiState.value = ProductsUiState.Loading }
                .catch { _uiState.value = ProductsUiState.Error(it.message) }
                .collect { products -> 
                    _uiState.value = ProductsUiState.Success(products)
                }
        }
    }
}
```

### StateFlow & Flow

- **StateFlow:** Hot flow, always has a value, emits to new collectors
- **Flow:** Cold flow, only emits when collected

```kotlin
// Collect state
val state by viewModel.uiState.collectAsState()
when (state) {
    is Loading -> {}
    is Success -> {}
    is Error -> {}
}
```

### Coroutines

- **viewModelScope:** Lifecycle-aware, cancels on ViewModel destruction
- **launch:** Fire-and-forget
- **async:** Returns result (Deferred)

```kotlin
viewModelScope.launch {
    val result = withContext(Dispatchers.Default) {
        // CPU-intensive work
    }
}
```

---

## Offline-First Strategy

**See [docs/OFFLINE_FIRST.md](docs/OFFLINE_FIRST.md) for complete details.**

### Overview

1. **Cache First** - Always try local database first
2. **Sync When Online** - Background sync with WorkManager
3. **Conflict Resolution** - Last-write-wins strategy
4. **Queue Operations** - Store offline actions in queue

### Implementation

```kotlin
// Offline-First Repository
class OfflineFirstRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkMonitor: NetworkMonitor
) {
    fun getData(): Flow<List<Entity>> = flow {
        // 1. Emit cached data immediately
        val cached = localDataSource.getAll()
        emit(cached)
        
        // 2. If online, fetch fresh data
        if (networkMonitor.isOnline.value) {
            try {
                val fresh = remoteDataSource.getAll()
                localDataSource.update(fresh)
                emit(fresh)
            } catch (e: Exception) {
                // Use cached data on error
            }
        }
    }
}
```

---

## Technology Stack

### Language
- **Kotlin** 100% exclusive
- **Coroutines** for async operations
- **Collections** API
- **Scope functions** (let, run, apply, also)

### UI Framework
- **Jetpack Compose** for declarative UI
- **Material 3 Design System**
- **Navigation Compose** for routing
- **Hilt** for DI in Compose

### Architecture
- **Clean Architecture** - Clear layer separation
- **MVVM Pattern** - State management
- **Repository Pattern** - Data abstraction
- **UseCase Pattern** - Business operations

### Data Persistence
- **Room Database** - Local SQLite storage
- **DataStore** - Key-value preferences
- **Moshi/Gson** - JSON serialization

### Network
- **Retrofit** - REST client
- **OkHttp** - HTTP client with interceptors
- **Moshi** - JSON parsing

### Background & Async
- **WorkManager** - Background tasks
- **Coroutines** - Structured concurrency
- **Flow** - Reactive streams

### Testing
- **JUnit 4/5** - Unit testing
- **MockK** - Mocking library
- **Turbine** - Flow testing
- **Coroutines Test** - Coroutine testing
- **Google Truth** - Assertions
- **Espresso** - UI testing (pending)

### Payment Integration
- **Zarinpal API** - Payment gateway
- **BuildConfig** - Secure secrets
- **OkHttp Interceptors** - Request/response handling

### Localization
- **String resources** - Multi-language
- **RTL Layout** - Right-to-left support
- **Jalali Calendar** - Persian dates

---

## Architectural Decisions

See [docs/ADR/](docs/ADR/) for detailed Architecture Decision Records:

- [ADR-001: MVVM Architecture](docs/ADR/ADR-001-MVVM-Architecture.md)
- [ADR-002: Offline-First Strategy](docs/ADR/ADR-002-Offline-First.md)
- [ADR-003: Payment Integration](docs/ADR/ADR-003-Payment-Integration.md)

### Summary

| Decision | Rationale | Trade-offs |
|----------|-----------|------------|
| **Clean Architecture** | Testability, Maintainability | More files/classes |
| **MVVM Pattern** | Clear separation, ViewModel reuse | Learning curve |
| **Offline-First** | Better UX, Works without internet | More complex sync |
| **Jetpack Compose** | Modern, Declarative | Less ecosystem |
| **Hilt DI** | Compile-time safety, Less boilerplate | Annotation processing |

---

## Conclusion

NoghreSod's architecture is designed for:
- ✅ **Reliability** - Comprehensive testing
- ✅ **Maintainability** - Clear structure and patterns
- ✅ **Scalability** - Easy to add features
- ✅ **Performance** - Optimized queries and caching
- ✅ **User Experience** - Works offline, smooth UI

**For more details:**
- Architecture patterns: See [DEVELOPMENT.md](DEVELOPMENT.md)
- Implementation examples: Check source code in `app/src/main/kotlin/`
- Testing strategy: Read [TESTING.md](TESTING.md)

---

**Last Updated:** December 28, 2025  
**Status:** ✅ Production-Ready
