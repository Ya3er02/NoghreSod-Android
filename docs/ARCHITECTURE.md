# NoghreSod-Android Architecture

## Overview

NoghreSod follows **Clean Architecture** principles with **MVI (Model-View-Intent)** pattern for state management. The codebase is organized into distinct layers with clear separation of concerns.

---

## Architecture Layers

### 1. **Domain Layer** (`domain/`)

**Purpose**: Business logic and domain models  
**Responsibility**: Rules, use cases, entities  
**Independence**: No dependencies on other layers

```kotlin
domain/
├── model/
│   ├── Product.kt              // Silver jewelry product entity
│   ├── ProductFilters.kt       // Unified filtering model
│   ├── User.kt
│   └── ...
└── usecase/
    ├── product/
    │   ├── GetProductsUseCase.kt
    │   └── SearchProductsUseCase.kt
    └── ...
```

**Key Models**:
- `Product`: Jewelry product with price, weight, gem type
- `ProductFilters`: Centralized filtering (price, weight, gems, plating, hallmark)
- `User`: Customer profile and preferences

---

### 2. **Data Layer** (`data/`)

**Purpose**: Data sources and repositories  
**Responsibility**: API calls, caching, local database  
**Technology**: Retrofit, Room, DataStore

```kotlin
data/
├── repository/
│   ├── ProductRepository.kt
│   ├── UserRepository.kt
│   └── ...
├── remote/
│   ├── ProductApi.kt
│   └── NetworkInterceptor.kt
├── local/
│   ├── ProductDao.kt
│   └── PreferencesStore.kt
└── mapper/
    ├── ProductMapper.kt
    └── ...
```

**Responsibilities**:
- Network calls via Retrofit
- Local caching via Room
- User preferences via DataStore
- Data transformation (API ↔ Domain models)

---

### 3. **Presentation Layer - ViewModels** (`presentation/viewmodel/`)

**Purpose**: State management and business logic orchestration  
**Pattern**: **MVI (Model-View-Intent)**  
**Technology**: Kotlin Coroutines, StateFlow, SharedFlow

```kotlin
presentation/viewmodel/
├── base/
│   └── BaseViewModel.kt        // Generic MVI base
├── ProductsViewModel.kt        // UNIFIED: 3 → 1
├── HomeViewModel.kt
└── ...
```

**MVI Components**:

```kotlin
// 1. State (What UI renders)
sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val products: List<Product>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

// 2. Intent (What user does)
sealed interface ProductsIntent {
    data object LoadProducts : ProductsIntent
    data class SearchProducts(val query: String) : ProductsIntent
    data class ApplyFilters(val filters: ProductFilters) : ProductsIntent
}

// 3. Effect (One-time events)
sealed class ProductsEffect {
    data class NavigateToDetail(val productId: String) : ProductsEffect
    data object ScrollToTop : ProductsEffect
}

// 4. ViewModel
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val useCase: GetProductsUseCase
) : BaseViewModel<ProductsUiState, ProductsIntent, ProductsEffect>() {
    // Unidirectional data flow: Intent → State → UI
}
```

**Benefits**:
- ✅ Predictable state management
- ✅ Testable (100% coverage possible)
- ✅ Lifecycle-safe
- ✅ No side effects outside effects layer

---

### 4. **UI Layer** (`ui/`)

**Purpose**: User interface components and screens  
**Technology**: Jetpack Compose, Material 3  
**Special**: Full RTL support for Persian

```kotlin
ui/
├── screens/
│   ├── home/
│   │   └── HomeScreen.kt       // UNIFIED: 2 → 1
│   ├── products/
│   │   └── ProductsScreen.kt
│   ├── cart/
│   ├── wishlist/
│   └── ...
├── components/
│   ├── ProductCard.kt
│   ├── SearchBar.kt
│   ├── FilterBottomSheet.kt
│   ├── ErrorView.kt
│   ├── LoadingIndicator.kt
│   ├── RTLModifiers.kt         // RTL helpers
│   └── ...
├── theme/
│   ├── Color.kt
│   ├── Typography.kt
│   └── Theme.kt
└── utils/
    └── (UI-specific utilities)
```

**Key Characteristics**:
- **Stateless Composables**: UI components receive state, emit intents
- **Material 3**: Modern design system
- **RTL Support**: Native Persian/Arabic layout
- **Accessible**: High contrast, scalable text

**Example Screen**:
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onProductClick(effect.productId)
                is HomeEffect.ShowError -> showSnackbar(effect.message)
            }
        }
    }
    
    when (val state = uiState.value) {
        is HomeUiState.Loading -> LoadingScreen()
        is HomeUiState.Success -> SuccessScreen(state.products, viewModel)
        is HomeUiState.Error -> ErrorScreen(state.message)
    }
}
```

---

## Package Structure (Final)

```
app/src/main/kotlin/com/noghre/sod/
│
├── MainActivity.kt                      ✅ App entry point (root only)
├── NoghreSodApp.kt                      ✅ Application class (root only)
│
├── core/                                ✅ Shared constants, extensions
│   ├── Constants.kt
│   ├── Extensions.kt
│   └── ...
│
├── data/                                ✅ Data layer
│   ├── repository/
│   ├── remote/
│   ├── local/
│   └── mapper/
│
├── di/                                  ✅ Dependency injection (Hilt)
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   └── ...
│
├── domain/                              ✅ Business logic
│   ├── model/
│   │   ├── Product.kt
│   │   ├── ProductFilters.kt           ✅ UNIFIED: Centralized
│   │   └── ...
│   └── usecase/
│       ├── product/
│       └── ...
│
├── presentation/                        ✅ State management
│   └── viewmodel/
│       ├── base/
│       │   └── BaseViewModel.kt         ✅ NEW: MVI foundation
│       ├── ProductsViewModel.kt         ✅ UNIFIED: 3 → 1
│       ├── HomeViewModel.kt
│       └── ...
│
├── ui/                                  ✅ UI layer
│   ├── screens/
│   │   ├── home/
│   │   │   └── HomeScreen.kt           ✅ UNIFIED: 2 → 1
│   │   ├── products/
│   │   ├── cart/
│   │   └── ...
│   ├── components/
│   │   ├── ProductCard.kt
│   │   ├── SearchBar.kt
│   │   ├── FilterBottomSheet.kt
│   │   ├── RTLModifiers.kt
│   │   └── ...
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Typography.kt
│   │   └── Theme.kt
│   └── utils/
│
├── navigation/                          ✅ Navigation (unified)
│   ├── NavGraph.kt
│   └── NavRoutes.kt
│
└── utils/                               ✅ Root utilities
    ├── Extensions.kt
    ├── Validators.kt
    └── ...
```

---

## Data Flow (MVI Pattern)

```
User Interaction
      ↓
[Intent] (SearchProducts("ring"))
      ↓
ProductsViewModel.handleIntent()
      ↓
[Use Case] GetProductsUseCase
      ↓
[Repository] ProductRepository
      ↓
[State] ProductsUiState.Success([products])
      ↓
[UI] HomeScreen renders product list
      ↓
[Effects] NavigateToDetail(productId) → One-time event
```

**Key Properties**:
- ✅ **Unidirectional**: Intent → State → UI (no feedback loops)
- ✅ **Single Source of Truth**: StateFlow is only truth
- ✅ **Testable**: Each layer independently testable
- ✅ **Predictable**: State transitions are deterministic

---

## Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideProductRepository(
        api: ProductApi,
        dao: ProductDao
    ): ProductRepository = ProductRepositoryImpl(api, dao)
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : BaseViewModel<...>() { ... }
```

**Benefits**:
- Automatic dependency resolution
- Testability (easy to provide mocks)
- Lifecycle management
- Compile-time safety

---

## Special Considerations

### RTL Support (Persian Layout)

```kotlin
val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isRtl) Arrangement.End else Arrangement.Start
) {
    // RTL-aware layout
}
```

### High-Resolution Jewelry Images

```kotlin
AsyncImage(
    model = product.image,
    contentDescription = product.name,
    modifier = Modifier.fillMaxWidth(),
    contentScale = ContentScale.Crop,
    imageLoader = LocalContext.current.imageLoader.newBuilder()
        .memoryCache { MemoryCache(maxSize = 256 * 1024 * 1024) } // 256MB
        .build()
)
```

### Pagination for Large Catalogs

```kotlin
LazyVerticalGrid(...) {
    items(products.size) { index ->
        ProductCard(products[index])
        if (index == products.size - 1) {
            viewModel.handleIntent(ProductsIntent.LoadMore)
        }
    }
}
```

---

## Testing Strategy

### Unit Tests
- ProductsViewModelTest: 100% coverage
- Use Mockk for mocking dependencies
- Use Turbine for Flow testing
- Test all state transitions and effects

### UI Tests
- Compose UI tests for screens
- Test user interactions (tap, scroll)
- Verify state rendering

### Manual Testing
- Search functionality
- Filtering (price, weight, gems)
- Pagination (load more)
- Navigation
- RTL layout

---

## Performance Guidelines

1. **Lazy Loading**: Use LazyVerticalGrid for large lists
2. **Image Caching**: Coil with memory cache for jewelry shots
3. **Network Caching**: Room database with TTL
4. **State Management**: Only expose what UI needs via StateFlow
5. **Coroutines**: Use viewModelScope for lifecycle management

---

## Design Principles

✅ **Single Responsibility**: Each class has one reason to change  
✅ **Dependency Inversion**: Depend on abstractions, not implementations  
✅ **DRY (Don't Repeat Yourself)**: Unified components, centralized logic  
✅ **KISS (Keep It Simple)**: Clear naming, straightforward patterns  
✅ **Testability**: All business logic is testable in isolation  

---

## Evolution & Refactoring

**Recent Refactoring (Dec 29, 2025)**:
- ✅ Unified ProductsViewModel (3 → 1)
- ✅ Created BaseViewModel (MVI foundation)
- ✅ Centralized ProductFilters (domain model)
- ✅ Unified HomeScreen (2 → 1)
- ✅ Consolidated tests
- ✅ Cleaned up 47 duplicates (42 remaining identified)

**Next Phases**:
- Component consolidation (3 folders → 1)
- Navigation consolidation (3 folders → 1)
- Screen consolidation
- Global import fixes

See `REFACTORING_EXECUTIVE_BRIEF.md` for details.

---

## Team Guidelines

### When Adding New Features

1. **Domain Layer**: Create use case or model if needed
2. **Data Layer**: Add repository method or API endpoint
3. **ViewModel**: Add Intent, State, Effect, and handler
4. **UI Layer**: Create Composable screen/component
5. **Tests**: Unit tests for ViewModel (100% coverage)

### Code Review Checklist

- ✅ Follows MVI pattern (State/Intent/Effect)
- ✅ No side effects in ViewModel init
- ✅ StateFlow used for state, SharedFlow for effects
- ✅ Proper error handling (sealed Result class)
- ✅ RTL support checked
- ✅ Tests written for business logic
- ✅ No duplicate code
- ✅ KDoc for public APIs

---

**NoghreSod Architecture v1.0**  
**Updated**: December 29, 2025  
**Pattern**: Clean Architecture + MVI  
**Status**: Production-Ready ✅
