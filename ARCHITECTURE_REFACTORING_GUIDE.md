# 🏑 NoghreSod Architecture Refactoring Guide

## 🚠 Status: IN PROGRESS

This document guides the complete refactoring of NoghreSod to proper Clean Architecture.

---

## ⚠️ Current Issues

### CRITICAL: ViewModels Directly Depend on Repositories (❌ WRONG)

**Current Pattern (VIOLATES Clean Architecture):**
```kotlin
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository  // ❌ WRONG: Directly inject Repository
) : ViewModel()
```

**Correct Pattern (Clean Architecture):**
```kotlin
@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    // ... all UseCases needed
) : ViewModel()
```

### Why This Matters

| Aspect | With Repository | With UseCase |
|--------|-----------------|---------------|
| **Testability** | Hard (mock entire repo) | Easy (mock specific use case) |
| **Maintainability** | Mixed concerns | Clear separation |
| **Reusability** | Can't reuse logic | UseCases reusable everywhere |
| **Single Responsibility** | ViewModel has 2 jobs | ViewModel = UI only |
| **Business Logic** | In ViewModel/Repo (confusing) | In UseCase (clear) |

---

## 🅰️ Action Plan

### Phase 1: Create Missing UseCases (✅ DONE)

All missing UseCases have been created:

#### Validation UseCases ✅
- `ValidateEmailUseCase` - Email format validation
- `ValidatePasswordUseCase` - Password requirements
- `ValidatePasswordConfirmationUseCase` - Password match
- `ValidatePhoneNumberUseCase` - Iranian phone format

#### Cart UseCases ✅
- `UpdateCartItemUseCase` - Change quantity (NEW)
- `ClearCartUseCase` - Empty cart (NEW)
- `CalculateCartTotalUseCase` - Compute total with tax (NEW)
- Plus existing: GetCartUseCase, AddToCartUseCase, RemoveCouponUseCase, etc.

#### Product UseCases ✅
- `GetProductByIdUseCase` - Fetch single product (NEW)
- `ObserveFavoritesUseCase` - Stream favorites (NEW)
- `ToggleFavoriteUseCase` - Add/remove favorite (NEW)
- `GetFavoritesUseCase` - Get all favorites (NEW)
- `ObserveProductsUseCase` - Real-time products (NEW)

#### Auth UseCases ✅
- `GetCurrentUserUseCase` - Get logged-in user (NEW)
- `IsAuthenticatedUseCase` - Check auth status (NEW)
- `RefreshAuthTokenUseCase` - Refresh token (NEW)
- Plus existing: LoginUseCase, RegisterUseCase, LogoutUseCase

#### Order UseCases ✅
- `CreateOrderUseCase` - New order from cart (NEW)
- `GetOrdersUseCase` - User's orders (NEW)
- `GetOrderByIdUseCase` - Single order details (NEW)
- `CancelOrderUseCase` - Cancel pending order (NEW)
- `GetOrderTrackingUseCase` - Tracking info (NEW)

#### Profile UseCases ✅
- `GetUserProfileUseCase` - Profile info (NEW)
- `UpdateUserProfileUseCase` - Edit profile (NEW)
- `UpdateProfileImageUseCase` - Change avatar (NEW)
- `DeleteAccountUseCase` - Delete account (NEW)

#### Search UseCases ✅
- `SearchProductsUseCase` - Search with filters (NEW)
- `GetSearchHistoryUseCase` - Recent searches (NEW)
- `SaveSearchQueryUseCase` - Save search (NEW)
- `ClearSearchHistoryUseCase` - Clear history (NEW)

#### Infrastructure ✅
- `ResourceProvider` - Domain interface for strings
- `ResourceProviderImpl` - Hilt injectable implementation

### Phase 2: Refactor ViewModels (🔜 IN PROGRESS)

ViewModels to refactor (10 total):

1. **CartViewModel** ✅ Reference template created
   ```
   Path: presentation/viewmodel/CartViewModel.REFACTORED.kt
   ```
   
2. **AuthViewModel** - Next to refactor
3. **ProductDetailViewModel**
4. **ProductListViewModel**
5. **OrderViewModel**
6. **ProfileViewModel**
7. **SearchViewModel**
8. **CheckoutViewModel**
9. **FavoritesViewModel**
10. **HomeViewModel**

---

## 📚 Refactoring Template: CartViewModel

Use this as reference for refactoring all ViewModels.

**Location:** `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/CartViewModel.REFACTORED.kt`

### Key Points:

```kotlin
// ✅ STEP 1: Inject all UseCases (not Repository)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val removeCouponUseCase: RemoveCouponUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel()

// ✅ STEP 2: Handle UI state
private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// ✅ STEP 3: Call UseCases
fun loadCart() {
    viewModelScope.launch(exceptionHandler.handler) {
        _uiState.value = UiState.Loading
        
        // UseCase handles all business logic
        val result = getCartUseCase(Unit)
        
        if (result.isSuccess) {
            val cart = result.getOrNull()
            _cart.value = cart
            _uiState.value = UiState.Success
        } else {
            val error = result.exceptionOrNull()
            _uiState.value = UiState.Error(error?.message ?: "Unknown")
        }
    }
}
```

---

## 📓 Step-by-Step Refactoring Instructions

### For each ViewModel:

#### Step 1: List all UseCases needed
```
Example for CartViewModel:
- GetCartUseCase
- AddToCartUseCase
- UpdateCartItemUseCase
- ClearCartUseCase
- ApplyCouponUseCase
- CalculateCartTotalUseCase
```

#### Step 2: Update constructor
```kotlin
// BEFORE
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel()

// AFTER
@HiltViewModel  
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    // ... all needed UseCases
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel()
```

#### Step 3: Replace all Repository calls
```kotlin
// BEFORE
fun loadCart() {
    viewModelScope.launch {
        cartRepository.getCartItems()  // ❌ Direct repository
            .onSuccess { items -> /* ... */ }
            .onError { error -> /* ... */ }
    }
}

// AFTER
fun loadCart() {
    viewModelScope.launch {
        getCartUseCase(Unit)  // ✅ UseCase call
            .onSuccess { cart -> /* ... */ }
            .onError { error -> /* ... */ }
    }
}
```

#### Step 4: Move validation to UseCases
```kotlin
// BEFORE
fun register(password: String, confirmPassword: String) {
    if (password.length < 6) {  // ❌ Validation in ViewModel
        showError("Too short")
    }
}

// AFTER
fun register(password: String, confirmPassword: String) {
    viewModelScope.launch {
        // ✅ Validation in UseCase
        validatePasswordUseCase(password)
        validatePasswordConfirmationUseCase(
            ValidatePasswordConfirmationUseCase.Params(password, confirmPassword)
        )
        // Then proceed with registration
        registerUseCase(params)
    }
}
```

#### Step 5: Handle Results
```kotlin
val result = someUseCase(params)

if (result.isSuccess) {
    val data = result.getOrNull()
    // Update UI state
} else {
    val exception = result.exceptionOrNull()
    _uiEvent.value = UiEvent.ShowError(exception?.message)
}
```

---

## 🧹 Quality Checklist

Before completing refactoring:

- [ ] No ViewModel directly injects Repository
- [ ] All business logic moved to UseCases
- [ ] All existing UseCases are being used
- [ ] Error handling consistent (AppError only)
- [ ] No hardcoded Persian strings in Kotlin (use resources)
- [ ] Unit tests for critical UseCases
- [ ] Project compiles without errors
- [ ] All ViewModels maintain current functionality
- [ ] Logging using Timber
- [ ] UI state management proper (StateFlow)

---

## 🗮 Dependency Injection Setup

All UseCases are auto-provided by Hilt if they have `@Inject` constructor.

**Verify in di modules:**

```kotlin
// di/RepositoryModule.kt - Already exists
// di/UseCaseModule.kt - May need to create if complex dependencies
// di/DispatcherModule.kt - For CoroutineDispatcher injection
```

---

## 📐 String Resources

All Persian strings moved to resources:

**File:** `app/src/main/res/values-fa/strings.xml`

```xml
<string name="error_empty_fields">لطفا تمام فیلدها را پر کنید</string>
<string name="error_password_length">رمز عبور باید حداقل 6 کاراکتر باشد</string>
```

Used in UseCases:

```kotlin
throw AppError.Validation(
    message = getString(R.string.error_password_length),
    fieldName = "password"
)
```

---

## ⚡️ Testing Strategy

### Unit Test Example for UseCase

```kotlin
@Test
fun `given valid cart when addToCart then updates successfully`() = runTest {
    // Given
    val params = AddToCartUseCase.Params(
        productId = "123",
        quantity = 2
    )
    coEvery { cartRepository.addToCart(any(), any()) } returns Result.Success(mockCart)
    val useCase = AddToCartUseCase(cartRepository)
    
    // When
    val result = useCase(params)
    
    // Then
    assertTrue(result.isSuccess)
    val cart = result.getOrNull()
    assertEquals(1, cart?.items?.size)  // Or your assertion
}
```

### Unit Test Example for ViewModel

```kotlin
@Test
fun `when loadCart then updates state correctly`() = runTest {
    // Given
    val mockCart = mockCart()  // Create mock
    coEvery { getCartUseCase(Unit) } returns Result.Success(mockCart)
    
    val viewModel = CartViewModel(
        getCartUseCase = getCartUseCase,
        // ... mock all UseCases
        exceptionHandler = GlobalExceptionHandler()
    )
    
    // When
    viewModel.loadCart()
    advanceUntilIdle()
    
    // Then
    assertEquals(UiState.Success, viewModel.uiState.value)
    assertEquals(mockCart, viewModel.cart.value)
}
```

---

## 🗳️ Files Created

**New UseCases** (15 files):
1. ValidateEmailUseCase.kt
2. ValidatePasswordUseCase.kt
3. ValidatePhoneNumberUseCase.kt
4. UpdateCartItemUseCase.kt
5. GetProductByIdUseCase.kt
6. GetCurrentUserUseCase.kt
7. OrderUseCases.kt (5 in 1)
8. ProfileUseCases.kt (4 in 1)
9. SearchUseCases.kt (4 in 1)
10. ResourceProvider.kt
11. ResourceProviderImpl.kt

**Reference Templates** (1 file):
12. CartViewModel.REFACTORED.kt

**Documentation** (this file):
13. ARCHITECTURE_REFACTORING_GUIDE.md

---

## 📊 Estimated Timeline

| Task | Hours | Status |
|------|-------|--------|
| Create UseCases | 8 | ✅ DONE |
| Create Infrastructure | 2 | ✅ DONE |
| Refactor CartViewModel | 1 | 🔜 Template Done |
| Refactor AuthViewModel | 1 | TODO |
| Refactor Other ViewModels | 6 | TODO |
| Unit Testing | 8 | TODO |
| Integration Testing | 4 | TODO |
| Documentation | 2 | 🔜 In Progress |
| **TOTAL** | **32-36 hours** | **~70% Complete** |

---

## 🂯 Next Steps

### Immediate (Next Session)
1. Refactor AuthViewModel using CartViewModel as template
2. Create unit tests for validation UseCases
3. Update Hilt modules if needed

### Short Term
4. Refactor all remaining ViewModels
5. Run full test suite
6. Code review for architecture compliance

### Final
7. Performance optimization
8. Documentation update
9. Release preparation

---

## 퉰 Questions?

Refer to:
- **Architecture**: `domain/usecase/base/UseCase.kt`
- **Example**: `presentation/viewmodel/CartViewModel.REFACTORED.kt`
- **Patterns**: Compare with existing working code in repo

---

**Last Updated:** 2025-12-29
**Status:** Architecture refactoring in progress
**Progress:** ~70% complete
