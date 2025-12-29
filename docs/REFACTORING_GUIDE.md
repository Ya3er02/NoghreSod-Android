# NoghreSod-Android Refactoring Guide v1.0.0

## 📋 Overview

This document details the comprehensive refactoring effort to eliminate duplicate code and unify the architecture of NoghreSod-Android.

**Refactoring Date**: December 29, 2025  
**Duration**: ~45-60 minutes (automated with provided scripts)  
**Criticality**: HIGH  
**Risk Level**: MEDIUM → LOW (with proper execution)  

---

## 🎯 Objectives

✅ **Eliminate Code Duplication**
- Remove 3 duplicate ProductsViewModel implementations
- Consolidate 2 HomeScreen implementations
- Merge conflicting test files

✅ **Unify Architecture**
- Establish single `/presentation/viewmodel/` location for all ViewModels
- Establish single `/ui/screens/` location for all Composables
- Implement consistent MVI pattern across all ViewModels

✅ **Improve Maintainability**
- Reduce cognitive load for new developers
- Single source of truth for each feature
- Easier to track changes and bugs

✅ **Enhance Testability**
- Merge duplicate test cases
- Create unified test suites
- Improve test coverage

---

## 📂 File Path Mapping (OLD → NEW)

### Phase 1: Deleted Files (Superseded)

| OLD Path | Status | Replaced By | Reason |
|----------|--------|-------------|--------|
| `app/src/main/kotlin/com/noghre/sod/presentation/ui/home/HomeScreen.kt` | ❌ DELETED | `app/src/main/kotlin/com/noghre/sod/ui/screens/home/HomeScreen.kt` | Outdated version, complete replacement available |
| `app/src/main/kotlin/com/noghre/sod/presentation/ui/home/HomeViewModel.kt` | ❌ DELETED | `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/HomeViewModel.kt` | Associated with deleted HomeScreen, unified version created |
| `app/src/main/kotlin/com/noghre/sod/presentation/products/ProductsViewModel.kt` | ❌ DELETED | `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModel.kt` | Wrong package, consolidated into unified version |
| `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModelImproved.kt` | ❌ DELETED | `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModel.kt` | Merged into main ProductsViewModel, no longer separate |
| `app/src/test/java/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt` | ❌ DELETED | `app/src/test/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt` | Java test duplicate; Kotlin version is canonical |
| `app/src/main/kotlin/com/noghre/sod/presentation/ui/` | ❌ DELETED | Migrated to `ui.screens.*` | Old directory structure, entirely superseded |

### Phase 2: Created/Updated Files (New Standards)

| Path | Status | Purpose | Type |
|------|--------|---------|------|
| `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModel.kt` | ✅ NEW | Consolidated ProductsViewModel (merged from 3 sources) | Source |
| `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/HomeViewModel.kt` | ✅ NEW | Unified HomeViewModel | Source |
| `app/src/main/kotlin/com/noghre/sod/presentation/viewmodel/base/BaseViewModel.kt` | ✅ NEW | MVI pattern base class | Source |
| `app/src/main/kotlin/com/noghre/sod/domain/model/ProductFilters.kt` | ✅ NEW | Centralized filter domain model | Source |
| `app/src/main/kotlin/com/noghre/sod/ui/screens/home/HomeScreen.kt` | ✅ UPDATED | Unified HomeScreen (merged from 2 sources) | Source |
| `app/src/test/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt` | ✅ MERGED | Consolidated test suite | Test |
| `docs/REFACTORING_GUIDE.md` | ✅ NEW | This document | Documentation |

---

## 🔄 Import Statement Changes

All import statements referencing deleted/moved files must be updated. Use the table below as a reference:

### Search & Replace Examples

```bash
# Replace old HomeScreen import
find app/src -name '*.kt' -type f -exec sed -i \
  's/import com\.noghre\.sod\.presentation\.ui\.home\.HomeScreen/import com.noghre.sod.ui.screens.home.HomeScreen/g' {} +

# Replace old ProductsViewModel import (from wrong package)
find app/src -name '*.kt' -type f -exec sed -i \
  's/import com\.noghre\.sod\.presentation\.products\.ProductsViewModel/import com.noghre.sod.presentation.viewmodel.ProductsViewModel/g' {} +

# Replace ProductsViewModelImproved with unified version
find app/src -name '*.kt' -type f -exec sed -i \
  's/import com\.noghre\.sod\.presentation\.viewmodel\.ProductsViewModelImproved/import com.noghre.sod.presentation.viewmodel.ProductsViewModel/g' {} +

# Replace Java test import
find app/src -name '*.kt' -type f -exec sed -i \
  's/import com\.noghre\.sod\.presentation\.viewmodel\.ProductsViewModelTest/import com.noghre.sod.presentation.viewmodel.ProductsViewModelTest/g' {} +
```

### Manual Updates Needed

Check these files and manually update imports if needed:
- `app/src/main/kotlin/com/noghre/sod/MainActivity.kt`
- `app/src/main/kotlin/com/noghre/sod/navigation/NavGraph.kt`
- `app/src/main/kotlin/com/noghre/sod/ui/screens/products/ProductsScreen.kt`
- Any custom screens or components referencing old packages

---

## 🏗️ New Architecture

### Package Structure (Post-Refactor)

```
app/src/main/kotlin/com/noghre/sod/
├── domain/
│   ├── model/
│   │   ├── Product.kt
│   │   ├── ProductFilters.kt          ← NEW: Centralized
│   │   └── ...
│   └── usecase/
│       └── product/
│           ├── GetProductsUseCase.kt
│           └── SearchProductsUseCase.kt
│
├── presentation/
│   ├── viewmodel/
│   │   ├── base/
│   │   │   └── BaseViewModel.kt       ← NEW: MVI Base
│   │   ├── HomeViewModel.kt           ← UNIFIED
│   │   ├── ProductsViewModel.kt       ← UNIFIED (3→1)
│   │   └── ...
│   │
│   └── ui/
│       ├── screens/
│       │   ├── home/
│       │   │   └── HomeScreen.kt      ← UNIFIED (2→1)
│       │   ├── products/
│       │   │   └── ProductsScreen.kt
│       │   └── ...
│       ├── components/
│       │   ├── ProductCard.kt
│       │   ├── FilterBottomSheet.kt
│       │   └── ...
│       └── theme/
│           └── ...
```

### Why This Structure?

- **Domain Layer** (`domain/`) → Business logic, use cases, domain models
- **Presentation Layer** (`presentation/viewmodel/`) → State management with MVI pattern
- **UI Layer** (`ui/screens/` & `ui/components/`) → Jetpack Compose UI elements

**Benefit**: Clear separation of concerns, easy to navigate, aligns with Clean Architecture best practices.

---

## 🔧 MVI Pattern Implementation

All ViewModels now follow the MVI (Model-View-Intent) pattern:

```kotlin
// 1. UI STATE (What the UI renders)
sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val products: List<Product>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

// 2. USER INTENTS (What user does)
sealed interface ProductsIntent {
    data object LoadProducts : ProductsIntent
    data class SearchProducts(val query: String) : ProductsIntent
}

// 3. ONE-TIME EFFECTS (Navigation, toasts, etc.)
sealed class ProductsEffect {
    data class NavigateToDetail(val productId: String) : ProductsEffect
}

// 4. VIEWMODEL (Processes intents, emits state/effects)
@HiltViewModel
class ProductsViewModel @Inject constructor(...) : ViewModel() {
    val uiState: StateFlow<ProductsUiState> = ...
    val effectFlow: SharedFlow<ProductsEffect> = ...
    
    fun handleIntent(intent: ProductsIntent) { ... }
}
```

**Benefits**:
- Unidirectional data flow (Intent → State → UI)
- Single source of truth for UI state
- Testable and predictable
- Handles side effects cleanly

---

## ⚠️ Breaking Changes & Fixes

### 1. ViewModel Import Updates

**OLD**: Multiple locations for same ViewModel
```kotlin
// ❌ No longer works
import com.noghre.sod.presentation.products.ProductsViewModel
import com.noghre.sod.presentation.viewmodel.ProductsViewModelImproved
```

**NEW**: Single canonical location
```kotlin
// ✅ Use this
import com.noghre.sod.presentation.viewmodel.ProductsViewModel
```

**Fix**: Search & replace in your screens/components using old imports.

### 2. HomeScreen Import Updates

**OLD**:
```kotlin
// ❌ No longer works
import com.noghre.sod.presentation.ui.home.HomeScreen
import com.noghre.sod.presentation.ui.home.HomeViewModel
```

**NEW**:
```kotlin
// ✅ Use these
import com.noghre.sod.ui.screens.home.HomeScreen
import com.noghre.sod.presentation.viewmodel.HomeViewModel
```

**Fix**: Update NavGraph and any manual screen composition calls.

### 3. Filter Logic Updates

**OLD**: Scattered across multiple ViewModels
```kotlin
var minPrice by remember { mutableStateOf(null) }
var maxPrice by remember { mutableStateOf(null) }
var gemType by remember { mutableStateOf("") }
// ...
```

**NEW**: Centralized in domain model
```kotlin
import com.noghre.sod.domain.model.ProductFilters

val filters = viewModel.filters.collectAsStateWithLifecycle()
viewModel.handleIntent(HomeIntent.ApplyFilters(ProductFilters(...)))
```

**Fix**: Use `ProductFilters` data class for all filtering operations.

### 4. Test File Migrations

**OLD**: Java test alongside Kotlin code (type mismatch)
```
app/src/test/java/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt
```

**NEW**: Single Kotlin test
```
app/src/test/kotlin/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt
```

**Fix**: No action needed if using only Kotlin tests. Java test is deleted.

---

## ✅ Testing Checklist

After applying refactoring changes, verify:

### Compilation
- [ ] `./gradlew clean assembleDebug` — NO errors
- [ ] `./gradlew lintDebug` — NO unused imports warnings
- [ ] No unresolved references (IDE shows no red squiggles)

### Unit Tests
- [ ] `./gradlew test` — ALL tests pass
- [ ] ProductsViewModelTest covers:
  - [ ] Loading products
  - [ ] Searching products
  - [ ] Applying filters
  - [ ] Pagination (LoadMore)
  - [ ] Error handling

### UI Tests
- [ ] `./gradlew connectedAndroidTest` — ALL UI tests pass
- [ ] HomeScreen tests verify:
  - [ ] Screen renders successfully
  - [ ] SearchBar input works
  - [ ] FilterBottomSheet opens/closes
  - [ ] Products grid displays
  - [ ] Product click navigation

### Manual Testing
- [ ] App launches without crashes
- [ ] Home screen loads and displays products
- [ ] Search functionality works (debouncing, results)
- [ ] Filters apply correctly (price, weight, gem type)
- [ ] Pagination loads more products
- [ ] Navigation to product details works
- [ ] RTL layout correct for Persian text
- [ ] No memory leaks (check with Profiler)

### Code Quality
- [ ] `./gradlew ktlintFormat` — Format checks pass
- [ ] No orphaned or unused imports
- [ ] No dead code in merged ViewModels
- [ ] KDoc comments present for public APIs

---

## 🚀 Execution Steps

### Quick Start (Manual)

1. **Backup current state**
   ```bash
   git tag backup-before-refactor-20251229
   git checkout -b refactor/consolidation
   ```

2. **Delete duplicate files**
   ```bash
   git rm app/src/main/kotlin/com/noghre/sod/presentation/ui/home/HomeScreen.kt
   git rm app/src/main/kotlin/com/noghre/sod/presentation/ui/home/HomeViewModel.kt
   git rm app/src/main/kotlin/com/noghre/sod/presentation/products/ProductsViewModel.kt
   git rm app/src/test/java/com/noghre/sod/presentation/viewmodel/ProductsViewModelTest.kt
   ```

3. **Update imports** (Use sed commands from section "Import Statement Changes")

4. **Verify**
   ```bash
   ./gradlew clean assembleDebug
   ./gradlew test
   ```

5. **Commit**
   ```bash
   git commit -am "refactor: Remove duplicate files and unify architecture"
   ```

### Automated Approach (Bash Script)

See `scripts/refactor_consolidation.sh` for full automation.

---

## 📚 Additional Resources

- **MVI Pattern**: [MVI by Hannes Dorfmann](https://hannesdorfmann.com/android/mosby3-mvi-1/)
- **Clean Architecture**: [The Clean Code Blog](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- **Jetpack Compose Navigation**: [Android Developers](https://developer.android.com/jetpack/compose/navigation)
- **Kotlin Flows**: [Kotlin Official Docs](https://kotlinlang.org/docs/flow.html)

---

## 🆘 Troubleshooting

### Issue: "Unresolved reference: HomeScreen"
**Cause**: Import not updated  
**Fix**: Change import to `com.noghre.sod.ui.screens.home.HomeScreen`

### Issue: "Cannot find symbol: ProductsViewModelImproved"
**Cause**: File deleted, but old import still exists  
**Fix**: Remove old import and use `ProductsViewModel` instead

### Issue: Tests fail after refactoring
**Cause**: Mock or spy setup broken  
**Fix**: Update test setup to use new ViewModel location and state classes

### Issue: App crashes at runtime
**Cause**: Likely Hilt injection mismatch  
**Fix**: Ensure @HiltViewModel annotation exists and bindings are correct in Hilt modules

---

## 📞 Questions?

For questions about this refactoring:
- Check existing PRs for implementation patterns
- Review merged test files for examples
- Consult Space documentation on NoghreSod architecture

---

**Refactoring v1.0.0 Complete** ✨  
Last Updated: December 29, 2025  
NoghreSod Team
