# NoghreSod Android App - Project Structure

## 📁 Directory Layout

```
NoghreSod-Android/
├── .github/
│   └── workflows/
│       └── android-ci.yml          # CI/CD Pipeline
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── com/noghre/sod/
│   │   │   │       ├── presentation/   # UI Layer (Composables, ViewModels)
│   │   │   │       ├── domain/         # Business Logic (Use Cases, Models)
│   │   │   │       └── data/           # Data Layer (Repositories, API, DB)
│   │   │   ├── res/
│   │   │   │   ├── drawable/          # Vector & Raster Graphics
│   │   │   │   ├── layout/            # XML Layouts (Legacy)
│   │   │   │   ├── values/            # Strings, Colors, Dimens
│   │   │   │   └── xml/               # Network Security Config
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── test/                       # Unit Tests
│   │   └── androidTest/                # Integration Tests
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── buildSrc/                           # Shared Build Logic
├── scripts/
│   ├── install-hooks.sh               # Git Hooks Setup
│   └── check-dependencies.gradle      # Dependency Analysis
│
├── gradle/
│   └── wrapper/                        # Gradle Wrapper
│
├── libs/                               # Local Library Modules
│   └── shared-ui/                      # Reusable UI Components
│
├── gradle.properties                   # Gradle Configuration
├── gradle-wrapper.properties
├── settings.gradle.kts
├── build.gradle.kts
├── .gitignore
└── README.md
```

## 🏗️ Layer Architecture

### Presentation Layer (`presentation/`)
UI components built with Jetpack Compose.

```
presentation/
├── theme/
│   ├── Color.kt
│   ├── Typography.kt
│   └── Theme.kt
├── screens/
│   ├── home/
│   ├── products/
│   ├── product_detail/
│   ├── cart/
│   └── checkout/
├── components/
│   ├── ProductCard.kt
│   ├── ShimmerLoading.kt
│   └── ErrorDialog.kt
├── navigation/
│   └── Navigation.kt
└── viewmodel/
    ├── HomeViewModel.kt
    ├── ProductViewModel.kt
    └── CartViewModel.kt
```

### Domain Layer (`domain/`)
Business logic and entities.

```
domain/
├── model/
│   ├── Product.kt
│   ├── Order.kt
│   ├── User.kt
│   └── Result.kt
├── repository/
│   ├── ProductRepository.kt
│   ├── OrderRepository.kt
│   └── AuthRepository.kt
└── usecase/
    ├── GetProductsUseCase.kt
    ├── SearchProductsUseCase.kt
    └── PlaceOrderUseCase.kt
```

### Data Layer (`data/`)
Data access and integration.

```
data/
├── remote/
│   ├── api/
│   │   ├── ProductApi.kt
│   │   ├── OrderApi.kt
│   │   └── AuthApi.kt
│   ├── dto/
│   │   ├── ProductDto.kt
│   │   └── OrderDto.kt
│   └── interceptor/
│       └── AuthInterceptor.kt
├── local/
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   └── Migrations.kt
│   ├── dao/
│   │   ├── ProductDao.kt
│   │   ├── OrderDao.kt
│   │   └── CartItemDao.kt
│   ├── entity/
│   │   ├── ProductEntity.kt
│   │   └── OrderEntity.kt
│   ├── converters/
│   │   └── RoomTypeConverters.kt
│   └── preferences/
│       └── UserPreferences.kt
└── repository/
    ├── ProductRepositoryImpl.kt
    ├── OrderRepositoryImpl.kt
    └── AuthRepositoryImpl.kt
```

## 🔧 Build Configuration Files

### `build.gradle.kts` (Root)
- Defines common build configurations
- Shared dependencies using Version Catalog
- Global Gradle options

### `app/build.gradle.kts`
- App-specific dependencies
- Build variants (debug, release)
- Code coverage configuration
- ProGuard/R8 rules

### `gradle/libs.versions.toml` (Version Catalog)
Centralized dependency management:
```toml
[versions]
kotlin = "2.1.0"
compose = "1.8.0"
room = "2.6.1"
retrofit = "2.9.0"

[libraries]
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
androidx-compose-ui = { module = "androidx.compose.ui:ui", version.ref = "compose" }
```

## 📱 Resources Structure

### `res/values/`
- `strings.xml` - App strings (supports i18n)
- `colors.xml` - Color palette
- `dimens.xml` - Dimensions & spacing
- `styles.xml` - Theme styles

### `res/drawable/`
- Vector graphics (.xml)
- PNG/WebP images (compressed)
- App icons

### `res/xml/`
- `network_security_config.xml` - Network security policies
- Backup schemes

## 🧪 Testing Structure

### Unit Tests (`test/`)
```
test/
├── data/
│   └── repository/
│       └── ProductRepositoryTest.kt
├── domain/
│   └── usecase/
│       └── GetProductsUseCaseTest.kt
└── presentation/
    └── viewmodel/
        └── ProductViewModelTest.kt
```

### Integration Tests (`androidTest/`)
```
androidTest/
├── data/
│   └── database/
│       └── ProductDatabaseTest.kt
└── presentation/
    └── screens/
        └── HomeScreenTest.kt
```

## 🛠️ Scripts

### Git Hooks
- `scripts/install-hooks.sh` - Install pre-commit checks
  - Pre-commit: Linting, secret scanning
  - Pre-push: Tests, code quality
  - Commit-msg: Message validation

### Dependency Analysis
- `scripts/check-dependencies.gradle` - Analyze dependencies
  - Unused dependencies
  - Security vulnerabilities
  - License compliance

## 📦 Key Dependencies

### UI & Compose
- `androidx.compose.ui` - Compose UI framework
- `androidx.compose.material3` - Material Design 3
- `androidx.activity:activity-compose` - Activity integration

### Architecture
- `androidx.lifecycle:lifecycle-viewmodel` - ViewModel
- `androidx.lifecycle:lifecycle-runtime-compose` - State management
- `com.google.dagger:hilt-android` - Dependency injection

### Networking
- `com.squareup.retrofit2:retrofit` - HTTP client
- `com.squareup.okhttp3:okhttp` - Network library
- `com.google.code.gson:gson` - JSON parsing

### Database
- `androidx.room:room-runtime` - Local storage
- `androidx.room:room-ktx` - Kotlin extensions
- `androidx.room:room-compiler` - Code generation

### Async & Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines` - Async programming
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Android integration

### Other
- `com.jakewharton.timber:timber` - Logging
- `com.google.firebase:firebase-bom` - Firebase SDK
- `androidx.datastore:datastore-preferences` - Preferences

## 🔄 Build Process

```
[Source Code]
    ↓
[Kotlin Compiler]
    ↓
[Gradle Tasks: lint, detekt, test]
    ↓
[R8/ProGuard Obfuscation]
    ↓
[APK/AAB Generation]
    ↓
[GitHub Actions CI/CD]
    ↓
[Release to Play Store]
```

## 📊 Code Organization Principles

1. **Separation of Concerns** - Each layer has specific responsibility
2. **SOLID Principles** - Especially Single Responsibility & Dependency Inversion
3. **Clean Architecture** - Domain-driven design
4. **Type Safety** - Kotlin's strong typing, sealed classes for states
5. **Reactive** - Coroutines and Flow for async operations
6. **Testable** - Dependency injection for easy mocking

## 🚀 Getting Started

```bash
# Clone repository
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android

# Install Git hooks
bash scripts/install-hooks.sh

# Build project
./gradlew build

# Run tests
./gradlew test

# Generate documentation
./gradlew dokkaHtml
```

---

**Last Updated**: 2025-12-25  
**Version**: 1.0.0
