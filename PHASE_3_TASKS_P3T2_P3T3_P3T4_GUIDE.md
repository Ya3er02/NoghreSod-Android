# 📄 **Phase 3 - Tasks P3-T2, P3-T3, P3-T4 Implementation Guide**

**Date:** December 27, 2025 | **Status:** Ready for Implementation  
**Previous Task (P3-T1):** ✅ COMPLETE - Foundation Ready

---

## 📊 **Overview**

Phase 3 has 4 major tasks:
- **P3-T1:** ✅ COMPLETE - Core error infrastructure (GlobalExceptionHandler, Result, UiState)
- **P3-T2:** 🔄 TODO - Update repositories with error handling
- **P3-T3:** 🔄 TODO - Refactor ViewModels
- **P3-T4:** 🔄 TODO - Update UI components

---

## 📁 **Task P3-T2: Repository Layer Error Handling**

### Current Problem

**Current Code (OLD):**
```kotlin
suspend fun login(email: String, password: String): Result<AuthToken> {
    return try {
        val request = LoginRequestDto(email, password)
        val response = apiService.login(request)
        if (response.success && response.data != null) {
            Result.Success(response.data.toAuthToken())  // Old Result type!
        } else {
            Result.Error(Exception(response.message ?: "Unknown error"))  // Wrong error type!
        }
    } catch (e: Exception) {
        Result.Error(e)  // Generic exception, not AppError
    }
}
```

**Problems:**
1. ❌ Using `Result<T>` from `com.noghre.sod.domain` (old)
2. ❌ `Result.Error` takes `Exception`, not `AppError`
3. ❌ No classification of errors
4. ❌ No logging
5. ❌ Generic error messages

---

### Solution Pattern

**NEW Pattern:**
```kotlin
import com.noghre.sod.core.util.Result  // NEW: From core.util
import com.noghre.sod.core.error.*      // NEW: Error handling
import timber.log.Timber                 // NEW: Logging

suspend fun login(email: String, password: String): Result<AuthToken> {
    return try {
        Timber.d("[AUTH] Attempting login for: $email")
        
        val request = LoginRequestDto(email, password)
        val response = apiService.login(request)
        
        // Check HTTP status
        if (response.isSuccessful) {
            if (response.data != null) {
                Timber.d("[AUTH] Login successful")
                Result.Success(response.data.toAuthToken())
            } else {
                Timber.w("[AUTH] Response data is null")
                Result.Error(AppError.Network(
                    "پاسخ سرور نامعتبر است",
                    statusCode = 200  // HTTP 200 but no data
                ))
            }
        } else {
            // Handle HTTP errors
            val statusCode = response.code()
            val errorMessage = response.message ?: "Unknown error"
            
            Timber.w("[AUTH] Login failed with status $statusCode: $errorMessage")
            
            val error = when (statusCode) {
                401 -> AppError.Authentication(
                    message = "نام کاربری یا رمز عبور اشتباه است",
                    reason = AuthFailureReason.INVALID_CREDENTIALS
                )
                403 -> AppError.Authentication(
                    message = "حساب کاربری شما مسدود شده است",
                    reason = AuthFailureReason.ACCOUNT_LOCKED
                )
                else -> AppError.Network(
                    message = errorMessage,
                    statusCode = statusCode
                )
            }
            Result.Error(error)
        }
    } catch (e: java.net.UnknownHostException) {
        Timber.e(e, "[AUTH] Network error: No internet")
        Result.Error(AppError.Network(
            message = "بدون دسترسی به اینترنت",
            statusCode = null
        ))
    } catch (e: java.net.SocketTimeoutException) {
        Timber.e(e, "[AUTH] Network timeout")
        Result.Error(AppError.Network(
            message = "زمان اتصال تمام شد",
            statusCode = null
        ))
    } catch (e: Exception) {
        Timber.e(e, "[AUTH] Unexpected error during login")
        Result.Error(AppError.Unknown(
            message = e.message ?: "خطای غیرمنتظره",
            throwable = e
        ))
    }
}
```

---

## 📁 **Repositories to Update**

### List of All Repositories

| # | Repository | Functions to Update | Status |
|----|------------|-------------------|--------|
| 1 | **AuthRepositoryImpl.kt** | login, register, logout, getCurrentUser, getShippingAddresses, getSecuritySettings | 🔄 TODO |
| 2 | **ProductRepositoryImpl.kt** | getProducts, getProductById, searchProducts, getFavorites | 🔄 TODO |
| 3 | **CartRepositoryImpl.kt** | getCartItems, addToCart, removeFromCart, clearCart | 🔄 TODO |
| 4 | **OrderRepositoryImpl.kt** | createOrder, getOrders, getOrderById, trackOrder | 🔄 TODO |
| 5 | **UserRepositoryImpl.kt** | getProfile, updateProfile, getAddresses | 🔄 TODO |
| 6 | **CategoryRepositoryImpl.kt** | getCategories, getCategoryById | 🔄 TODO |
| 7 | **FavoriteRepositoryImpl.kt** | getFavorites, toggleFavorite | 🔄 TODO |

---

## 📁 **Task P3-T3: ViewModel Layer Refactoring**

### Current Problem

**Current ViewModel (OLD):**
```kotlin
class LoginViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.login(email, password)
                // Handle result?
            } catch (e: Exception) {
                // Error handling?
            } finally {
                _loading.value = false
            }
        }
    }
}
```

**Problems:**
1. ❌ LiveData (legacy)
2. ❌ No error state
3. ❌ No one-time events
4. ❌ No global exception handler
5. ❌ Manual try-catch

---

### Solution Pattern

**NEW ViewModel:**
```kotlin
import androidx.lifecycle.*
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.Result
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    // UI State - reactive flow
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()
    
    // One-time events
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()
    
    // Error state for additional handling
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()
    
    fun login(email: String, password: String) {
        // Use exception handler scope
        viewModelScope.launch(exceptionHandler.handler) {
            try {
                // Update UI state to loading
                _uiState.value = UiState.Loading
                
                // Call repository
                when (val result = repository.login(email, password)) {
                    // Handle success
                    is Result.Success -> {
                        _authToken.value = result.data.accessToken
                        _uiState.value = UiState.Success(Unit)
                        
                        // Send event for navigation
                        _events.send(UiEvent.Navigate("home_screen"))
                        _events.send(UiEvent.ShowToast("خوش آمدید!"))
                    }
                    
                    // Handle error
                    is Result.Error -> {
                        _uiState.value = UiState.Error(result.error)
                        
                        // Send event to show error
                        _events.send(UiEvent.ShowError(result.error))
                    }
                    
                    // Handle loading (if repository uses it)
                    else -> {}
                }
            } catch (e: Exception) {
                // Global exception handler will catch this
                val appError = exceptionHandler.handleException(e)
                _uiState.value = UiState.Error(appError)
                _events.send(UiEvent.ShowError(appError))
            }
        }
    }
    
    // Retry function
    fun retryLogin(email: String, password: String) {
        login(email, password)
    }
}
```

---

## 📁 **Task P3-T4: UI Layer Updates**

### Current Problem

**Current Composable (OLD):**
```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val loading by viewModel.loading.observeAsState()
    
    if (loading == true) {
        CircularProgressIndicator()
    } else {
        LoginForm()
    }
}
```

**Problems:**
1. ❌ No error display
2. ❌ No empty state
3. ❌ Manual state management
4. ❌ No events handling

---

### Solution Pattern

**NEW Composable:**
```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navController.navigate(event.route)
                is UiEvent.ShowToast -> showToast(event.message)  // TODO: Implement
                is UiEvent.ShowError -> showSnackbar(event.error.toUserMessage())
                else -> {}
            }
        }
    }
    
    // Render UI based on state
    when (val state = uiState) {
        is UiState.Idle -> {
            LoginForm(
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                }
            )
        }
        
        is UiState.Loading -> {
            LoadingView()
        }
        
        is UiState.Success -> {
            // Usually handled by navigation event
            Text("Login successful!")
        }
        
        is UiState.Error -> {
            ErrorView(
                error = state.error,
                onRetry = {
                    // Retry with previous credentials
                    viewModel.retryLogin(email, password)
                }
            )
        }
        
        is UiState.Empty -> {
            EmptyView(message = "No content")
        }
    }
}
```

---

## 📋 **Implementation Checklist**

### Step 1: Update Repositories (P3-T2)
- [ ] Import new Result type from `core.util`
- [ ] Import AppError and custom exceptions from `core.error`
- [ ] Import Timber for logging
- [ ] For each repository function:
  - [ ] Change return type to `Result<T>` (new)
  - [ ] Add try-catch for all exceptions
  - [ ] Add Timber logging (debug for start, warn for issues, error for failures)
  - [ ] Classify exceptions to AppError types
  - [ ] Add HTTP status code checking
  - [ ] Return `Result.Success(data)` on success
  - [ ] Return `Result.Error(appError)` on failure
  - [ ] Return `Result.Error(AppError.Unknown(...))` for unexpected errors

### Step 2: Update ViewModels (P3-T3)
- [ ] Replace LiveData with StateFlow
- [ ] Replace single loading state with UiState<T>
- [ ] Add Channel for events
- [ ] Inject GlobalExceptionHandler
- [ ] Wrap viewModelScope.launch with exceptionHandler.handler
- [ ] Use Result.onSuccess and Result.onError
- [ ] Send UiEvent for navigation, toasts, snackbars
- [ ] Add retry functions

### Step 3: Update UI Screens (P3-T4)
- [ ] Collect UiState instead of individual states
- [ ] Collect events with LaunchedEffect
- [ ] When on UiState.Loading: Show LoadingView
- [ ] When on UiState.Success: Show data or navigate
- [ ] When on UiState.Error: Show ErrorView with retry
- [ ] When on UiState.Empty: Show EmptyView
- [ ] Handle UiEvent navigation

---

## 🎈 **Expected Results After P3-T2, P3-T3, P3-T4**

✅ All repositories return new `Result<T>` type  
✅ All errors classified to `AppError` types  
✅ All ViewModels use `StateFlow` + `UiState`  
✅ All ViewModels have event channels  
✅ All UI screens handle all 5 UiState variants  
✅ Global exception handling active  
✅ User-friendly Persian error messages  
✅ Consistent retry logic across app  

---

## 🔧 **Important Notes**

1. **Import Paths** - Make sure to use:
   ```kotlin
   import com.noghre.sod.core.util.Result     // NEW
   import com.noghre.sod.core.error.*         // Error classes
   import com.noghre.sod.presentation.common.* // UiState, UiEvent
   ```

2. **Never Mix Old and New**:
   ```kotlin
   // ❌ WRONG
   import com.noghre.sod.domain.Result
   val result: Result<String> = ...  // Old type!
   
   // ✅ CORRECT
   import com.noghre.sod.core.util.Result
   val result: Result<String> = ...  // New type!
   ```

3. **HTTP Response Handling**:
   ```kotlin
   // For Retrofit responses
   if (response.isSuccessful) {
       // Use response.body()
   } else {
       // Use response.code() and response.message()
   }
   ```

4. **Logging Pattern**:
   ```kotlin
   Timber.d("[TAG] Starting operation")     // Debug
   Timber.w("[TAG] Unexpected condition")   // Warning
   Timber.e(exception, "[TAG] Error")       // Error with exception
   ```

---

## 🌟 **Estimated Time**

- **P3-T2 (Repositories):** 4-5 hours
- **P3-T3 (ViewModels):** 3-4 hours
- **P3-T4 (UI Screens):** 3-4 hours
- **Testing:** 2-3 hours

**Total:** ~12-16 hours

---

**Status:** Ready for Implementation  
**Difficulty:** Medium-High  
**Impact:** Very High

