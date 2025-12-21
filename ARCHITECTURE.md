# Android App Architecture

## Overview

This Android application follows Clean Architecture principles combined with MVVM (Model-View-ViewModel) pattern. The architecture is designed to be:

- **Testable**: Each layer can be tested independently
- **Maintainable**: Clear separation of concerns
- **Scalable**: Easy to add new features
- **Modular**: Components can be reused

## Architecture Layers

```
┌─────────────────────────────────────┐
│     Presentation Layer (UI)         │
│  (Composables, Screens, Components) │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│     ViewModel Layer                 │
│  (State Management, User Actions)   │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│     Repository Layer                │
│  (Data Abstraction)                 │
└──────┬──────────────────────┬───────┘
       │                      │
   ┌───▼────┐          ┌──────▼──────┐
   │  Local │          │   Remote    │
   │Database│          │   (API)     │
   └────────┘          └─────────────┘
```

## Layer Descriptions

### Presentation Layer (UI)
- **Location**: `presentation/`
- **Responsibility**: Rendering UI and handling user interactions
- **Components**:
  - `screen/`: Complete screens (LoginScreen, HomeScreen, etc.)
  - `component/`: Reusable UI components
  - `navigation/`: Navigation setup and routing

**Technologies**: Jetpack Compose, Material Design 3

### ViewModel Layer
- **Location**: `presentation/viewmodel/`
- **Responsibility**: Managing UI state and business logic
- **Characteristics**:
  - Survives configuration changes
  - Manages coroutines for async operations
  - Emits state updates through StateFlow

**Example**: `AuthViewModel`, `ProductViewModel`

### Repository Layer
- **Location**: `data/repository/`
- **Responsibility**: Abstracting data sources
- **Features**:
  - Provides single source of truth
  - Combines local and remote data
  - Implements caching strategies

**Example**: `AuthRepository`, `ProductRepository`

### Data Layer

#### Remote (Network)
- **Location**: `data/remote/`
- **Responsibility**: API communication
- **Components**:
  - `ApiService`: Retrofit interface with all endpoints
  - DTOs: Data Transfer Objects for API responses

#### Local (Database)
- **Location**: `data/local/`
- **Responsibility**: Local data persistence
- **Components**:
  - `PreferencesManager`: User preferences and tokens
  - Room DAOs: Database access objects (future)

### Domain Layer
- **Location**: `domain/`
- **Responsibility**: Business logic and models
- **Components**:
  - `Result<T>`: Sealed class for handling async results
  - Domain models (independent of UI/API)

## Data Flow

### User Interaction Flow

```
User clicks button
        ↓
  UI (Composable)
        ↓
  ViewModel.method()
        ↓
  Repository.operation()
        ↓
  Data Layer (Remote/Local)
        ↓
  Result<T> (Success/Error)
        ↓
  Repository returns Result
        ↓
  ViewModel updates State
        ↓
  StateFlow emits
        ↓
  Composable recomposes
        ↓
  UI updates
```

## State Management

### ViewModel State

```kotlin
data class MyScreenState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String? = null
)

class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyScreenState())
    val uiState: StateFlow<MyScreenState> = _uiState.asStateFlow()
}
```

### State in Composables

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsState()
    val state = uiState.value
    
    // UI based on state
}
```

## Dependency Injection

### Hilt Configuration

```kotlin
@HiltAndroidApp
class NoghreSodApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService = ...
}
```

### ViewModels with Hilt

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()
```

## Error Handling

### Result Sealed Class

```kotlin
seal class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### Usage in Repository

```kotlin
suspend fun login(email: String, password: String): Result<AuthResponse> {
    return try {
        val response = apiService.login(AuthRequest(email, password))
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown error")
    }
}
```

## Network Communication

### Retrofit Setup

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(...)
        .client(httpClient)
        .build()
}
```

### API Service

```kotlin
interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
    
    @GET("api/v1/products")
    suspend fun getProducts(): List<ProductDto>
}
```

## Testing Strategy

### Unit Tests
- ViewModel logic
- Repository operations
- Utility functions

### Integration Tests
- API responses
- Database operations
- Repository interactions

### UI Tests
- Composable rendering
- User interactions
- Navigation flows

## Best Practices

1. **Immutable State**: Use immutable data classes for UI state
2. **Single Responsibility**: Each class has one reason to change
3. **Dependency Injection**: Use Hilt for all dependencies
4. **Coroutines**: Use ViewModelScope for lifecycle-aware operations
5. **Error Handling**: Always handle errors gracefully
6. **Logging**: Use Timber for debug logging
7. **Performance**: Minimize recompositions in Compose

## Future Improvements

- [ ] Add Room database for offline support
- [ ] Implement pagination for large lists
- [ ] Add local caching strategy
- [ ] Implement WorkManager for background sync
- [ ] Add analytics integration
- [ ] Implement more comprehensive error handling
