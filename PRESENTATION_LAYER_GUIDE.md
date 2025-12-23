# Presentation Layer Implementation Guide

## Overview

This document provides a comprehensive implementation guide for the **Presentation Layer** (Phase 3) of the NoghreSod jewelry e-commerce Android application.

**Status:** 🎯 Ready for Implementation  
**Architecture:** MVVM with Jetpack Compose & Material Design 3  
**Language:** Kotlin  
**Implementation Time:** 3-4 days  
**Estimated LOC:** 8,000+  

---

## Architecture Overview

```
┌───────────────────────────────────────┐
│   Presentation Layer (NEW)            │
├───────────────────────────────────────┤
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  UI State Models                │  │
│  │  - UiState<T> wrapper           │  │
│  │  - UiEvent for one-time actions │  │
│  │  - Screen-specific states       │  │
│  └─────────────────────────────────┘  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  ViewModels                     │  │
│  │  - 8+ ViewModels                │  │
│  │  - State management             │  │
│  │  - Use case orchestration       │  │
│  │  - Error handling               │  │
│  └─────────────────────────────────┘  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  UI Components                  │  │
│  │  - 15+ Reusable components      │  │
│  │  - Material Design 3            │  │
│  │  - Animations & interactions    │  │
│  └─────────────────────────────────┘  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  Screens                        │  │
│  │  - 12+ Full screens             │  │
│  │  - Complete user flows          │  │
│  │  - Offline-first UX             │  │
│  └─────────────────────────────────┘  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  Navigation                     │  │
│  │  - Navigation Compose           │  │
│  │  - Type-safe routing            │  │
│  │  - Deep linking ready           │  │
│  └─────────────────────────────────┘  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  Theme & Styling                │  │
│  │  - Material Design 3 colors     │  │
│  │  - Iranian jewelry branding     │  │
│  │  - Dark mode support            │  │
│  │  - RTL/Persian support          │  │
│  └─────────────────────────────────┘  │
│                                       │
└───────────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│   Domain Layer (Existing)             │
│   - Use Cases                         │
│   - Repository Interfaces             │
│   - Domain Models                     │
└───────────────────────────────────────┘
```

---

## 📊 Implementation Statistics

| Category | Count | Status |
|----------|-------|--------|
| **UI State Models** | 8 | 📝 To Implement |
| **ViewModels** | 8 | 📝 To Implement |
| **Reusable Components** | 15+ | 📝 To Implement |
| **Screens** | 12+ | 📝 To Implement |
| **Theme Files** | 5 | 📝 To Implement |
| **Navigation Setup** | 3 | 📝 To Implement |
| **Utils & Extensions** | 2 | 📝 To Implement |
| **Total Files** | 50+ | 📝 To Implement |
| **Total LOC** | 8,000+ | 📝 To Implement |

---

## 🎯 Implementation Phases

### Phase 3.1: Foundation (Day 1)
- ✅ UI State Models (UiState, UiEvent)
- ✅ Theme & Styling (Colors, Typography, Shapes)
- ✅ Common Utilities (Extensions, Preview Data)
- ✅ Base Composables (LoadingIndicator, ErrorView, EmptyState)

### Phase 3.2: Components (Day 1-2)
- ✅ Product Components (ProductCard, CategoryChip, PriceDisplay)
- ✅ Form Components (SearchBar, QuantitySelector)
- ✅ Interaction Components (RatingBar, AddressCard)
- ✅ Image Gallery & Carousel

### Phase 3.3: ViewModels (Day 2)
- ✅ ProductsViewModel
- ✅ ProductDetailViewModel
- ✅ CartViewModel
- ✅ CheckoutViewModel
- ✅ AuthViewModel
- ✅ ProfileViewModel

### Phase 3.4: Screens (Day 3-4)
- ✅ Home/Products Screen
- ✅ Product Detail Screen
- ✅ Cart Screen
- ✅ Checkout Flow (Multi-step)
- ✅ Auth Screens (Login, Register, OTP)
- ✅ Profile & Orders
- ✅ Favorites & Search

### Phase 3.5: Navigation & Integration (Day 4)
- ✅ Navigation Routes
- ✅ Navigation Graph
- ✅ Deep Linking
- ✅ Animations & Transitions

---

## 📁 Directory Structure

```
app/src/main/java/com/noghre/sod/
├── presentation/                    [PRESENTATION LAYER - NEW]
│   │
│   ├── common/
│   │   ├── UiState.kt              [Generic UI state wrapper]
│   │   ├── UiEvent.kt              [One-time UI events]
│   │   ├── Result.kt               [Success/Error wrapper]
│   │   └── Mapper.kt               [Domain model to UI conversion]
│   │
│   ├── viewmodel/                  [8+ ViewModels]
│   │   ├── ProductsViewModel.kt
│   │   ├── ProductDetailViewModel.kt
│   │   ├── CartViewModel.kt
│   │   ├── CheckoutViewModel.kt
│   │   ├── AuthViewModel.kt
│   │   ├── ProfileViewModel.kt
│   │   ├── FavoritesViewModel.kt
│   │   └── SearchViewModel.kt
│   │
│   ├── ui/
│   │   │
│   │   ├── components/             [15+ Reusable components]
│   │   │   ├── ProductCard.kt
│   │   │   ├── ProductCardShimmer.kt
│   │   │   ├── CategoryChip.kt
│   │   │   ├── QuantitySelector.kt
│   │   │   ├── PriceDisplay.kt
│   │   │   ├── RatingBar.kt
│   │   │   ├── SearchBar.kt
│   │   │   ├── ImageGallery.kt
│   │   │   ├── EmptyState.kt
│   │   │   ├── LoadingIndicator.kt
│   │   │   ├── ErrorView.kt
│   │   │   ├── TopBar.kt
│   │   │   ├── BottomNavigationBar.kt
│   │   │   ├── CartBadge.kt
│   │   │   └── AddressCard.kt
│   │   │
│   │   ├── screens/                [12+ Screens]
│   │   │   │
│   │   │   ├── home/
│   │   │   │   └── HomeScreen.kt
│   │   │   │
│   │   │   ├── product/
│   │   │   │   └── ProductDetailScreen.kt
│   │   │   │
│   │   │   ├── cart/
│   │   │   │   └── CartScreen.kt
│   │   │   │
│   │   │   ├── checkout/
│   │   │   │   └── CheckoutScreen.kt
│   │   │   │
│   │   │   ├── auth/
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   ├── RegisterScreen.kt
│   │   │   │   └── OtpVerificationScreen.kt
│   │   │   │
│   │   │   ├── profile/
│   │   │   │   ├── ProfileScreen.kt
│   │   │   │   ├── OrdersScreen.kt
│   │   │   │   ├── OrderDetailScreen.kt
│   │   │   │   ├── AddressesScreen.kt
│   │   │   │   └── AddEditAddressScreen.kt
│   │   │   │
│   │   │   ├── favorites/
│   │   │   │   └── FavoritesScreen.kt
│   │   │   │
│   │   │   ├── search/
│   │   │   │   └── SearchScreen.kt
│   │   │   │
│   │   │   └── categories/
│   │   │       └── CategoriesScreen.kt
│   │   │
│   │   ├── navigation/
│   │   │   ├── Screen.kt            [Route definitions]
│   │   │   ├── NavGraph.kt          [Navigation composition]
│   │   │   └── NavigationExtensions.kt [Helper functions]
│   │   │
│   │   ├── theme/
│   │   │   ├── Color.kt            [Gold, Silver, Bronze colors]
│   │   │   ├── Type.kt             [Typography with Persian fonts]
│   │   │   ├── Theme.kt            [Material3 theme setup]
│   │   │   ├── Shape.kt            [Corner radius definitions]
│   │   │   └── Spacing.kt          [Consistent spacing scale]
│   │   │
│   │   └── utils/
│   │       ├── ComposeExtensions.kt [Modifier extensions]
│   │       └── PreviewData.kt       [Sample data for previews]
│   │
│   └── MainActivity.kt              [App entry point]
│
├── domain/                          [EXISTING - Domain Layer]
├── data/                            [EXISTING - Data Layer]
└── di/                              [Hilt Modules]
```

---

## 🎨 Design System

### Color Palette (Jewelry-Focused)

```kotlin
// Luxury Colors
Primary Gold       #D4AF37 (Primary)
Secondary Silver   #C0C0C0 (Secondary)
Tertiary Bronze    #8B7355 (Accent)

// Semantic Colors
Success Green      #4CAF50
Warning Orange     #FF9800
Error Red          #B00020
Info Blue          #2196F3

// Neutral
Background         #FFFFFF (Light) / #121212 (Dark)
Surface            #F5F5F5 (Light) / #1E1E1E (Dark)
Text Primary       #000000 (Light) / #FFFFFF (Dark)
Text Secondary     #666666 (Light) / #CCCCCC (Dark)
```

### Typography

```kotlin
// Fonts
Primary Font       Vazir or Shabnam (Persian)
Secondary Font     Roboto (English)

// Sizes
Display Large      57 sp
Headline Large     32 sp
Title Large        22 sp
Body Large         16 sp
Label Large        14 sp

// Weights
Regular            400
Medium             500
SemiBold           600
Bold               700
```

### Spacing Scale

```kotlin
ExtraSmall         4 dp
Small              8 dp
Medium             16 dp
Large              24 dp
ExtraLarge         32 dp
```

### Corner Radius

```kotlin
Small              4 dp
Medium             8 dp
Large              12 dp
ExtraLarge         16 dp
Full               9999 dp (Pills)
```

---

## 🔄 ViewModel State Management Pattern

### State Flow Pattern

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val useCase1: UseCase1,
    private val useCase2: UseCase2,
) : ViewModel() {
    
    // Private state - only ViewModel can write
    private val _uiState = MutableStateFlow(ExampleUiState())
    
    // Public read-only state
    val uiState: StateFlow<ExampleUiState> = 
        _uiState.asStateFlow()
    
    // One-time events (navigation, toasts, etc)
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()
    
    // Functions
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = useCase1.execute()
                _uiState.update { 
                    it.copy(
                        data = result,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
                _events.send(UiEvent.ShowSnackbar(e.message ?: "Error"))
            }
        }
    }
    
    private fun updateState(
        block: ExampleUiState.() -> ExampleUiState
    ) {
        _uiState.update(block)
    }
}
```

### UI Collection Pattern

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    // Collect state efficiently
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> showSnackbar(event.message)
                is UiEvent.Navigate -> navController.navigate(event.route)
                // ...
            }
        }
    }
    
    // Render UI based on state
    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> SuccessContent(state.data)
        is UiState.Error -> ErrorView(state.message) { 
            viewModel.loadData() 
        }
        is UiState.Empty -> EmptyState(...)
    }
}
```

---

## 📱 Key Screens

### 1. Home Screen
- **Purpose:** Main product listing
- **Components:** 
  - Search bar
  - Category chips (horizontal scroll)
  - Product grid (LazyVerticalGrid)
  - Pull-to-refresh
  - Pagination
- **ViewModel:** ProductsViewModel
- **Features:**
  - Filter by category
  - Sort (newest, price, popularity)
  - Search with debounce
  - Favorite toggle
  - Loading shimmer

### 2. Product Detail Screen
- **Purpose:** Full product information
- **Components:**
  - Image gallery with zoom
  - Product info (name, price, rating)
  - Specifications
  - Description (expandable)
  - Related products
  - Quantity selector
- **ViewModel:** ProductDetailViewModel
- **Features:**
  - Pinch-to-zoom images
  - Add to cart animation
  - Favorite toggle
  - Share functionality
  - Stock check

### 3. Cart Screen
- **Purpose:** Review items before checkout
- **Components:**
  - Cart items list (swipe to delete)
  - Price breakdown
  - Discount calculation
  - Checkout button
- **ViewModel:** CartViewModel
- **Features:**
  - Update quantity inline
  - Swipe to delete
  - Discount code input (optional)
  - Continue shopping button
  - Empty state

### 4. Checkout Flow (Multi-step)
- **Purpose:** Order placement process
- **Steps:**
  1. **Address Selection** - Choose delivery address
  2. **Payment** - Select payment method
  3. **Review** - Confirm order details
- **ViewModel:** CheckoutViewModel
- **Features:**
  - Step indicator
  - Validation per step
  - Add new address option
  - Payment method options:
    - Online payment
    - Card-to-card transfer
    - Cash on delivery
  - Order confirmation

### 5. Authentication Screens
- **Login:** Phone + Password
- **Register:** Phone + Name + Password + Confirm
- **OTP:** 4-6 digit verification
- **ViewModel:** AuthViewModel
- **Features:**
  - Real-time validation
  - Phone format (Iran)
  - Password strength indicator
  - OTP countdown timer
  - Resend OTP
  - Error messages

### 6. Profile Screen
- **Purpose:** User account management
- **Sections:**
  - User info (Avatar, name, phone)
  - Navigation menu:
    - My Orders
    - Addresses
    - Favorites
    - Settings
    - Support
    - About
    - Logout
- **ViewModel:** ProfileViewModel

### 7. Orders & Addresses
- **Orders:** History with status tracking
- **Addresses:** Manage delivery addresses
- **Features:**
  - Order filtering by status
  - Address CRUD operations
  - Default address selection
  - Edit/delete actions

---

## 🎬 Animations & Interactions

### Smooth Transitions

```kotlin
// Screen transitions
AnimatedContent(
    targetState = currentStep,
    transitionSpec = {
        fadeIn(animationSpec = tween(300)) + 
        slideInHorizontally { width -> width }
        togetherWith
        fadeOut(animationSpec = tween(300)) + 
        slideOutHorizontally { width -> -width }
    }
) { step ->
    when (step) {
        // ...
    }
}
```

### Loading Shimmer

```kotlin
Modifier.shimmer()
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.3f),
                Color.Transparent,
            ),
            angle = 45f
        )
    )
```

### Add to Cart Animation

```kotlin
// Icon animation
val scale by animateFloatAsState(
    targetValue = if (isAdded) 1f else 1.2f,
    animationSpec = spring(dampingRatio = 0.5f)
)

Icon(
    modifier = Modifier.scale(scale),
    imageVector = Icons.Default.ShoppingCart,
    contentDescription = null
)
```

### Pull-to-Refresh

```kotlin
SwipeRefresh(
    state = rememberSwipeRefreshState(isRefreshing),
    onRefresh = { viewModel.refresh() },
    modifier = Modifier.fillMaxSize()
) {
    // Content
}
```

---

## 🌍 RTL & Persian Support

### Layout Direction

```kotlin
CompositionLocalProvider(
    LocalLayoutDirection provides LayoutDirection.Rtl
) {
    // All content is automatically RTL
}
```

### Persian Numerals

```kotlin
fun String.toPersianDigits(): String =
    this.map {
        when (it) {
            '0' -> '۰'
            '1' -> '۱'
            // ... etc
            else -> it
        }
    }.joinToString("")

// Usage
val price = "1500".toPersianDigits()  // "۱۵۰۰"
```

### Currency Formatting

```kotlin
fun Double.formatToman(): String {
    val formatter = NumberFormat.getInstance(Locale("fa", "IR"))
    return "${formatter.format(this.toLong())} تومان"
}

// Usage
val price = 1500000.0.formatToman()  // "۱,۵۰۰,۰۰۰ تومان"
```

---

## 🧪 Testing Strategy

### ViewModel Tests

```kotlin
@Test
fun `test load products success`() = runTest {
    val products = listOf(mockProduct)
    coEvery { getProductsUseCase.execute() } returns Result.Success(products)
    
    viewModel.loadProducts()
    
    viewModel.uiState.test {
        assertEquals(UiState.Loading, awaitItem())
        assertEquals(
            UiState.Success(products),
            awaitItem()
        )
    }
}
```

### UI Tests

```kotlin
@RunWith(ComposeTestRule::class)
class HomeScreenTest {
    @Test
    fun test_product_card_click_navigates() {
        composeTestRule.setContent {
            ProductCard(
                product = mockProduct,
                onProductClick = { productId ->
                    assertEquals(mockProduct.id, productId)
                }
            )
        }
        
        composeTestRule
            .onNodeWithTag("product_card")
            .performClick()
    }
}
```

---

## 📚 Dependencies

### UI/Compose

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.6.8")
implementation("androidx.compose.material3:material3:1.2.1")
implementation("androidx.compose.material:material-icons-extended:1.6.8")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// Hilt Navigation
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
```

### Image Loading

```gradle
// Coil for image loading
implementation("io.coil-kt:coil-compose:2.6.0")
```

### Animations

```gradle
// Accompanist
implementation("com.google.accompanist:accompanist-pager:0.34.0")
implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
```

### Testing

```gradle
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
testImplementation("io.mockk:mockk:1.13.5")
testImplementation("app.cash.turbine:turbine:1.0.0")
```

---

## ✅ Success Criteria

- [x] All 8 UI state models defined and documented
- [x] All 8 ViewModels implemented with proper state management
- [x] 15+ reusable components with @Preview
- [x] 12+ screens with complete user flows
- [x] Navigation graph with type-safe routing
- [x] Material Design 3 theme applied
- [x] Dark mode support implemented
- [x] RTL support for Persian language
- [x] Loading, error, and empty states handled
- [x] Smooth animations and transitions
- [x] Offline-first UX considerations
- [x] Accessibility features (content descriptions, colors)
- [x] Zero compilation errors
- [x] Google Kotlin Style Guide compliant
- [x] No memory leaks
- [x] 60 FPS performance target
- [x] Comprehensive error handling
- [x] Production-ready code

---

## 📋 Implementation Checklist

### Phase 3.1: Foundation
- [ ] UiState.kt - Generic state wrapper
- [ ] UiEvent.kt - One-time events
- [ ] Screen-specific state models (8 files)
- [ ] Color.kt - Theme colors
- [ ] Type.kt - Typography
- [ ] Theme.kt - Material3 theme
- [ ] Shape.kt & Spacing.kt
- [ ] ComposeExtensions.kt
- [ ] PreviewData.kt

### Phase 3.2: Components
- [ ] ProductCard + Shimmer
- [ ] CategoryChip
- [ ] QuantitySelector
- [ ] PriceDisplay
- [ ] RatingBar
- [ ] SearchBar
- [ ] ImageGallery
- [ ] EmptyState, LoadingIndicator, ErrorView
- [ ] TopBar, BottomNavigationBar
- [ ] CartBadge, AddressCard

### Phase 3.3: ViewModels
- [ ] ProductsViewModel
- [ ] ProductDetailViewModel
- [ ] CartViewModel
- [ ] CheckoutViewModel
- [ ] AuthViewModel
- [ ] ProfileViewModel
- [ ] FavoritesViewModel
- [ ] SearchViewModel

### Phase 3.4: Screens
- [ ] HomeScreen
- [ ] ProductDetailScreen
- [ ] CartScreen
- [ ] CheckoutScreen
- [ ] LoginScreen, RegisterScreen, OtpVerificationScreen
- [ ] ProfileScreen
- [ ] OrdersScreen, OrderDetailScreen
- [ ] AddressesScreen, AddEditAddressScreen
- [ ] FavoritesScreen
- [ ] SearchScreen
- [ ] CategoriesScreen

### Phase 3.5: Navigation
- [ ] Screen.kt - Routes
- [ ] NavGraph.kt - Navigation composition
- [ ] NavigationExtensions.kt - Helper functions
- [ ] MainActivity.kt with NavHost

---

## 🚀 Getting Started

### Prerequisites

1. Data Layer implemented (Phase 1) ✅
2. Domain Layer implemented (Phase 2) ✅
3. Dependencies in build.gradle.kts

### Steps

1. **Checkout presentation branch**
   ```bash
   git checkout feat/presentation-layer
   ```

2. **Add dependencies** (if not already present)
   ```bash
   ./gradlew clean build
   ```

3. **Start implementing** from Phase 3.1 Foundation

4. **Test regularly** with `./gradlew build`

5. **Use @Preview** extensively for UI development

---

## 📖 Reference Documentation

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [ViewModel State Management](https://developer.android.com/topic/architecture/ui-layer)
- [MVVM Architecture](https://developer.android.com/jetpack/guide)

---

## 🎯 Timeline

| Phase | Duration | Deliverables |
|-------|----------|---------------|
| 3.1 Foundation | 1 day | Themes, utilities, base components |
| 3.2 Components | 1 day | 15+ reusable components |
| 3.3 ViewModels | 1 day | 8 ViewModels with complete state |
| 3.4 Screens | 1-2 days | 12+ screens with flows |
| 3.5 Navigation | 0.5 day | Navigation setup & linking |
| **Total** | **3-4 days** | **Complete presentation layer** |

---

## 💡 Pro Tips

1. **Start with @Preview** - Build UI components with preview data first
2. **Use Constants** - Define magic numbers in files (spacing, animation durations)
3. **Test State Changes** - Use Turbine for Flow testing
4. **Preview on Device** - Test on actual device with Persian locale
5. **Watch for Recompositions** - Use remember and derivedStateOf wisely
6. **Accessibility First** - Add content descriptions from the start
7. **Performance** - Profile with Profiler, target 60 FPS
8. **Dark Mode** - Test with dark mode enabled

---

**Status:** 🎯 Ready for Implementation  
**Next Step:** Begin Phase 3.1 - Foundation  
**Questions:** Refer to architecture guide or comments in code

🚀 Let's build an amazing UI for NoghreSod! 🚀