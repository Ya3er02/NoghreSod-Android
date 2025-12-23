# 🎗️ Noghresod Android Architecture

## Overview

Noghresod follows a **Clean Architecture** approach combined with **MVVM** pattern, ensuring scalability, testability, and maintainability.

---

## Architectural Layers

### 1. **Presentation Layer**

**Responsibility:** Display data to users and handle user interactions

**Components:**
- **Screens** (`HomeScreen`, `ProductDetailScreen`, etc.)
  - Composable functions that build UI
  - Handle user interactions
  - Call ViewModel methods

- **ViewModels** (`HomeViewModel`, `CartViewModel`, etc.)
  - Manage UI state with `StateFlow`
  - Call use cases
  - Handle events from UI
  - Survive configuration changes

- **Components** (Reusable UI elements)
  - `ProductCard` - Product display card
  - `LoadingScreen` - Loading indicator
  - `ErrorMessage` - Error display
  - `PrimaryButton` - Action buttons

- **Navigation**
  - `NavGraph` - Navigation structure
  - `Routes` - Screen routes
  - `BottomNavigationBar` - Tab navigation

- **Theme**
  - Material 3 theming
  - Color scheme (light/dark)
  - Typography definitions

**Data Flow:**
```
User Interaction → Screen → ViewModel → UseCase → Repository
```

---

### 2. **Domain Layer**

**Responsibility:** Define business logic and rules

**Components:**
- **Models** - Business entities
  - `Product` - Product domain model
  - `User` - User profile
  - `Cart` - Shopping cart
  - `Order` - Customer order

- **Use Cases** - Business operations
  - `GetProductsUseCase` - Fetch products
  - `AddToCartUseCase` - Add item to cart
  - `PlaceOrderUseCase` - Create order
  - `LoginUseCase` - User authentication

- **Repository Interfaces** - Data contracts
  - `ProductRepository`
  - `CartRepository`
  - `OrderRepository`
  - `AuthRepository`

**Key Principles:**
- Independent of UI framework
- No dependencies on external libraries
- Pure business logic
- Easy to test

---

### 3. **Data Layer**

**Responsibility:** Manage data sources (local and remote)

**Components:**

#### **Remote Data Source** (API)
- `ApiService` - Retrofit interface
  - 60+ endpoints
  - Type-safe requests
  - Automatic serialization

- `Dtos` - Data Transfer Objects
  - Request DTOs - Outgoing data
  - Response DTOs - Incoming data
  - Auto-mapping to domain models

- `AuthInterceptor` - Token management
  - Automatic token injection
  - Token refresh handling
  - Request signing

- `RetrofitClient` - HTTP client setup
  - OkHttp configuration
  - Logging
  - SSL configuration

#### **Local Data Source** (Database)
- `Entities` - Room database models
  - `ProductEntity`
  - `CartEntity` & `CartItemEntity`
  - `OrderEntity` & `OrderTrackingEntity`
  - `UserEntity` & `AddressEntity`

- `DAOs` - Data Access Objects
  - `ProductDao` - Product CRUD
  - `CartDao` - Cart operations
  - `OrderDao` - Order management
  - `UserDao` - User profile

- `AppDatabase` - Room database
  - Database configuration
  - Entity definitions
  - Migration handling

- `LocalDataSources` - Local data operations
  - `LocalProductDataSource`
  - `LocalCartDataSource`
  - `LocalOrderDataSource`
  - `LocalUserDataSource`

#### **Repositories** - Data orchestration
- `ProductRepositoryImpl` - Product operations
- `CartRepositoryImpl` - Cart management
- `OrderRepositoryImpl` - Order handling
- `AuthRepositoryImpl` - Authentication

**Data Flow:**
```
Remote API ←→ Retrofit ←→ Repository ←→ Local DB
                                    │
                            ←→ Domain Model
```

---

## Design Patterns

### 1. **MVVM (Model-View-ViewModel)**
```
View (Composable)
    │
    │ observes StateFlow
    │
    ↓
 ViewModel
    │
    │ calls UseCase
    │
    ↓
 UseCase / Repository
```

### 2. **Repository Pattern**
- Single source of truth for data
- Abstracts data sources (local/remote)
- Handles data caching
- Manages data consistency

### 3. **Use Case Pattern**
- Encapsulates business logic
- Single responsibility
- Easy to test
- Reusable across features

### 4. **Dependency Injection (Hilt)**
- Constructor injection
- Module-based configuration
- Scope management
- Type-safe bindings

### 5. **State Management (StateFlow)**
- Reactive state updates
- Thread-safe
- Lifecycle-aware
- Efficient emissions

---

## Data Flow Examples

### Example 1: Loading Products

```
HomeScreen
    │
    ├─→ collects uiState from HomeViewModel
    │
    └─ displays ProductCards
        
 HomeViewModel (init)
    │
    ├─→ calls GetProductsUseCase
    │
    └─ updates uiState with products
        
 GetProductsUseCase
    │
    ├─→ calls ProductRepository.getProducts()
    │
    └─ returns Result<List<Product>>
        
 ProductRepositoryImpl
    │
    ├─→ checks local cache (Room)
    │
    ├─→ if empty, fetches from API (Retrofit)
    │
    ├─→ saves to local DB
    │
    └─ returns products
```

### Example 2: Adding to Cart

```
ProductDetailScreen
    │
    ├─ user clicks "Add to Cart"
    │
    └─→ calls viewModel.addToCart(product, quantity)
        
ProductDetailViewModel
    │
    ├─→ calls AddToCartUseCase
    │
    └─ updates uiState
        
AddToCartUseCase
    │
    ├─→ calls CartRepository.addItem()
    │
    └─ returns Result<Cart>
        
CartRepositoryImpl
    │
    ├─→ gets current cart from local DB
    │
    ├─→ adds item to cart
    │
    ├─→ saves to local DB
    │
    └─ syncs with API (background)
```

---

## Dependency Management

### Modules (Hilt)

1. **NetworkModule** - API setup
   ```kotlin
   @Singleton Retrofit
   @Singleton OkHttpClient
   @Singleton ApiService
   ```

2. **DatabaseModule** - Database setup
   ```kotlin
   @Singleton AppDatabase
   @Singleton ProductDao
   @Singleton CartDao
   ```

3. **RepositoryModule** - Data layer
   ```kotlin
   @Singleton ProductRepository
   @Singleton CartRepository
   @Singleton OrderRepository
   ```

4. **UseCaseModule** - Business logic
   ```kotlin
   @Singleton GetProductsUseCase
   @Singleton AddToCartUseCase
   @Singleton PlaceOrderUseCase
   ```

### Scopes
- **@Singleton** - App lifecycle (Repositories, Databases)
- **@ActivityScoped** - Activity lifecycle
- **@ViewModelScoped** - ViewModel lifecycle

---

## Testing Strategy

### Unit Tests
- ViewModel logic
- UseCase execution
- Repository operations
- Utility functions

### Integration Tests
- Database operations
- API interactions
- Complete feature flows

### UI Tests
- Screen rendering
- User interactions
- Navigation flows

---

## Best Practices

✅ **Separation of Concerns**
- Each layer has clear responsibility
- Minimal dependencies between layers

✅ **Testability**
- Dependencies injected
- Use interfaces for mocking
- No side effects in pure functions

✅ **Maintainability**
- Clear naming conventions
- Consistent code structure
- Comprehensive documentation

✅ **Performance**
- Lazy loading of data
- Efficient state management
- Database indexing
- Memory-efficient collections

✅ **Security**
- Encrypted storage
- HTTPS only
- Token rotation
- Input validation

---

## Migration & Scalability

This architecture supports:
- Easy feature addition
- New data source integration
- Multi-platform expansion
- Backend migration
- Third-party service integration

---

## References

- [Google Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Jetpack Documentation](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
