# NoghreSod Android - Architecture Documentation

## Architecture Pattern: MVVM + Clean Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER (UI)                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Jetpack Compose Screens & Components                 │  │
│  │  - HomeScreen           - CartScreen                  │  │
│  │  - ProductScreen        - OrderScreen                 │  │
│  │  - ProfileScreen        - BottomNav                   │  │
│  │  - Material Design 3 Components                       │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           ↑ Observes
┌─────────────────────────────────────────────────────────────┐
│              APPLICATION LAYER (VIEWMODEL)                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Hilt-Injected ViewModels with State Management       │  │
│  │  - HomeViewModel        - CartViewModel               │  │
│  │  - ProductViewModel     - OrderViewModel              │  │
│  │  - ProfileViewModel                                   │  │
│  │                                                       │  │
│  │  State Flow for reactive updates                      │  │
│  │  Error handling and Loading states                    │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           ↑ Uses
┌─────────────────────────────────────────────────────────────┐
│                  DOMAIN LAYER (REPOSITORY)                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Repository Pattern: Business Logic Abstraction       │  │
│  │  - ProductRepository    - CartRepository              │  │
│  │                                                       │  │
│  │  Flow-based stream of data                            │  │
│  │  Error handling and Result wrapper                    │  │
│  │  Offline-first data management                        │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                    ↑ Coordinates Data From
       ├────────────────────────────┬────────────────────────┤
       ↑                            ↑
┌──────────────────────┐    ┌──────────────────────┐
│   LOCAL DATA SOURCE  │    │  REMOTE DATA SOURCE  │
│  ┌────────────────┐  │    │  ┌────────────────┐  │
│  │ Room Database  │  │    │  │  Retrofit API  │  │
│  │                │  │    │  │                │  │
│  │ ProductDao     │  │    │  │ ApiService     │  │
│  │ CartDao        │  │    │  │ (18 endpoints) │  │
│  │ UserDao        │  │    │  │                │  │
│  │ CategoryDao    │  │    │  │ RetrofitClient │  │
│  │                │  │    │  │ Interceptors   │  │
│  │ Converters     │  │    │  │                │  │
│  │ Database       │  │    │  │ OkHttp Client  │  │
│  └────────────────┘  │    │  └────────────────┘  │
│                      │    │                      │
└──────────────────────┘    └──────────────────────┘
```

## Dependency Injection with Hilt

```
┌─────────────────────────────────────────────────────┐
│          DEPENDENCY INJECTION (HILT)                │
│  ┌───────────────────────────────────────────────┐  │
│  │ AppModule                                     │  │
│  │ - Application context provision               │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │ NetworkModule                                 │  │
│  │ - Retrofit instance creation                  │  │
│  │ - ApiService interface binding                │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │ DatabaseModule                                │  │
│  │ - Room database instance                      │  │
│  │ - All DAO bindings                            │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │ RepositoryModule                              │  │
│  │ - Repository implementations                  │  │
│  │ - Scope and lifecycle management              │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## Data Flow

### Read Flow (User Views Products)
```
UI (ProductScreen)
  ↑
  ↑ Observes Flow<List<Product>>
  ↑
ViewModel (ProductViewModel)
  ↑
  ↑ Calls repository.getProductById()
  ↑
Repository (ProductRepository)
  ↑
  ↑ Checks local first
  ↑ Then fetches remote if needed
  ↑
DAO & API
  Local: Room ProductDao
  Remote: Retrofit ApiService
```

### Write Flow (User Adds to Cart)
```
UI (Button Click)
  ↑
  ↑ viewModel.addToCart(productId, quantity)
  ↑
ViewModel (CartViewModel)
  ↑
  ↑ viewModelScope.launch
  ↑ repo.addToCart()
  ↑
Repository (CartRepository)
  ↑
  ↑ Saves to local first
  ↑ Syncs to remote API
  ↑
DAO & API
  Local: CartDao.addToCart()
  Remote: ApiService.addToCart()
```

## Model & DTO Conversion

```
┌──────────────────────────────────────────┐
│  Network Response (Json from API)        │
│                                          │
│  {
│    "id": "123",
│    "name": "Product",
│    "price": 99.99
│  }
└──────────────────────────────────────────┘
             ↑
             ↑ Gson deserialization
             ↑
┌──────────────────────────────────────────┐
│  ProductDto (Data Transfer Object)       │
│                                          │
│  @Serializable
│  data class ProductDto(
│    val id: String,
│    val name: String,
│    val price: Double
│  )
└──────────────────────────────────────────┘
             ↑
             ↑ Extension function: toEntity()
             ↑
┌──────────────────────────────────────────┐
│  Product Entity (Room Database)          │
│                                          │
│  @Entity(tableName = "products")
│  data class Product(
│    @PrimaryKey val id: String,
│    val name: String,
│    val price: Double
│  )
└──────────────────────────────────────────┘
             ↑
             ↑ Repository abstraction
             ↑
┌──────────────────────────────────────────┐
│  ViewModel/UI Layer                      │
│  - Uses Product entity directly          │
│  - No DTO exposure to UI                 │
└──────────────────────────────────────────┘
```

## Error Handling Strategy

```
┌─────────────────────────────────────────┐
│  Try-Catch at Repository Layer          │
│                                         │
│  fun getProducts(): Flow<Result> {      │
│    return flow {                        │
│      try {                              │
│        emit(Result.Loading)             │
│        val response = api.get()         │
│        emit(Result.Success(data))       │
│      } catch (e: Exception) {           │
│        emit(Result.Error(message))      │
│      }                                  │
│    }                                    │
│  }                                      │
└─────────────────────────────────────────┘
             ↑
             ↑ Observe in ViewModel
             ↑
┌─────────────────────────────────────────┐
│  ViewModel State Management              │
│                                         │
│  sealed class Result<T> {               │
│    class Success<T>(val data: T)        │
│    class Error<T>(val message: String)  │
│    class Loading<T>                     │
│  }                                      │
└─────────────────────────────────────────┘
             ↑
             ↑ Update UI State
             ↑
┌─────────────────────────────────────────┐
│  UI Composables                         │
│                                         │
│  if (state.isLoading) {                 │
│    LoadingIndicator()                   │
│  } else if (state.error != null) {      │
│    ErrorMessage(state.error)            │
│  } else {                               │
│    ProductList(state.products)          │
│  }                                      │
└─────────────────────────────────────────┘
```

## Lifecycle Management

```
App Launch
  ↓
NoghreSodApp.onCreate()
  - Initialize Timber logging
  - Initialize Hilt
  ↓
MainActivity.onCreate()
  - Set Compose content
  - Initialize NavGraph
  ↓
Navigation
  - Screen pushed on stack
  - ViewModel created (scope: navigation backstack)
  - Repositories initialized
  - Database/API services loaded
  ↓
Screen Lifecycle
  - Composable renders
  - ViewModel observes repositories
  - Flow subscriptions activated
  - Data loads from DB or API
  ↓
Screen Destruction
  - Composable unmounts
  - Flow subscriptions cancelled
  - ViewModel cleared
  ↓
App Termination
  - All resources released
  - Database connections closed
  - SharedPreferences saved
```

## State Management Pattern

```
ViewModel State Flow:
  MutableStateFlow<UiState>() → asStateFlow()
  ↗ ↙
  ↗ Update state based on repository results
  ↗ Emit new state
  ↗ UI observes and recomposes
  ↗
Composable Observation:
  val state = viewModel.uiState.collectAsState()
  ↑
  ↑ Automatic recomposition on state change
  ↑
UI Rendering:
  When(state.value) {
    Loading → Show progress
    Error → Show error message
    Success → Show content
  }
```

## Threading Model

```
Main Thread (UI)
  ↑
  ↑ Jetpack Compose recomposition
  ↑
ViewModel Scope
  ↑
  ↑ viewModelScope.launch { }
  ↑ Default dispatcher context
  ↑
IO Thread (Network/Database)
  ↑
  ↑ Retrofit API calls
  ↑ Room database queries
  ↑ IO coroutine dispatcher
  ↑
Results returned to Main Thread
  ↑
  ↑ StateFlow emits on main
  ↑ Compose recomposes
  ↑
UI Updates
```

## Security Layers

```
┌─────────────────────────────────────────┐
│  Network Security                       │
│  - Certificate pinning (optional)       │
│  - HTTPS only in production             │
│  - Security config file                 │
└─────────────────────────────────────────┘
             ↑
┌─────────────────────────────────────────┐
│  API Authentication                     │
│  - Bearer token in Authorization header │
│  - AuthInterceptor adds token           │
│  - Token refresh handling               │
└─────────────────────────────────────────┘
             ↑
┌─────────────────────────────────────────┐
│  Data Storage Security                  │
│  - SharedPreferences for non-sensitive  │
│  - Room database encrypted (optional)   │
│  - Sensitive data in memory only        │
└─────────────────────────────────────────┘
             ↑
┌─────────────────────────────────────────┐
│  Code Security                          │
│  - ProGuard obfuscation (release)       │
│  - Resource shrinking enabled           │
│  - Lint security checks                 │
└─────────────────────────────────────────┘
```

## Testing Architecture

```
┌─────────────────────────────────────────┐
│  Unit Tests (Repository/ViewModel)      │
│  - MockK for mocking dependencies       │
│  - Coroutines testing library           │
│  - Turbine for Flow testing             │
└─────────────────────────────────────────┘
             ↑
┌─────────────────────────────────────────┐
│  Integration Tests (Database/API)       │
│  - Room testing library                 │
│  - MockWebServer for API mocking        │
└─────────────────────────────────────────┘
             ↑
┌─────────────────────────────────────────┐
│  UI Tests (Compose Preview/Emulator)    │
│  - Compose testing library              │
│  - UI state verification                │
└─────────────────────────────────────────┘
```

---

## Key Architectural Principles

1. **Separation of Concerns**
   - UI doesn't access database directly
   - ViewModels don't contain UI logic
   - Repositories don't know about UI

2. **Unidirectional Data Flow**
   - Data flows down from repositories
   - Events flow up from UI
   - ViewModel bridges communication

3. **Dependency Inversion**
   - High-level modules depend on abstractions
   - Low-level modules implement abstractions
   - Hilt manages dependencies automatically

4. **Single Responsibility**
   - Each class has one reason to change
   - ViewModel: state management
   - Repository: data fetching logic
   - DAO: database operations

5. **Reactive Programming**
   - Flows for data streams
   - State flows for UI state
   - Coroutines for async operations
   - No callbacks or blocking operations

---

This architecture ensures maintainability, testability, and scalability.
