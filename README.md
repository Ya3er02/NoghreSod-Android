# 💍 Noghresod - Silver Jewelry E-Commerce Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android API](https://img.shields.io/badge/Android%20API-24%2B-green)](https://www.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10-green)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow)](./LICENSE)
[![Last Updated](https://img.shields.io/badge/Updated-Dec%202025-blue)]()

**A production-ready silver jewelry e-commerce application built with modern Android development practices.**

---

## 📋 Overview

Noghresod is a comprehensive Android e-commerce platform designed specifically for silver jewelry retail. Built with **100% Kotlin** and **Jetpack Compose**, it follows **Clean Architecture** principles and implements the **MVVM** pattern for scalability and maintainability.

### ✨ Key Features

✅ **Product Catalog**
- Comprehensive product catalog with filtering and search
- High-quality images and detailed product information  
- Category-based browsing
- Featured and trending products
- Product ratings and reviews

✅ **Shopping Experience**
- Smooth cart management
- Multiple checkout options
- Real-time order tracking
- Wishlist/Favorites functionality

✅ **User Management**
- Secure authentication (Login/Register)
- Profile management
- Address management
- Notification preferences

✅ **Payment Integration**
- Multiple payment gateway support
- Secure transaction handling
- Order confirmation and tracking

✅ **Technical Excellence**
- 100% Kotlin with Jetpack Compose
- Clean Architecture + MVVM
- Type-safe dependency injection (Hilt)
- Offline-first approach with Room DB
- Reactive programming with Flow & Coroutines
- Material Design 3

---

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│  PRESENTATION LAYER                 │
│  (Screens, ViewModels, Components)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  DOMAIN LAYER                       │
│  (Use Cases, Business Logic)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  DATA LAYER                         │
│  (Repositories, DB, APIs)           │
└─────────────────────────────────────┘
```

### Project Structure

```
app/src/main/kotlin/com/noghre/sod/
├── di/                          # Dependency Injection
│   ├── AppModule.kt            # Core configurations
│   └── RepositoryModule.kt     # Repository bindings
│
├── domain/                      # Business Logic
│   └── model/
│       ├── Product.kt          # Product domain model
│       ├── User.kt             # User domain model
│       ├── Cart.kt             # Shopping cart model
│       ├── Order.kt            # Order management model
│       └── Payment.kt          # Payment model
│
├── data/                        # Data Implementation
│   ├── remote/
│   │   ├── api/
│   │   │   └── NoghreSodApi.kt # Retrofit interface
│   │   └── dto/
│   │       └── Dtos.kt         # Data Transfer Objects
│   ├── local/
│   │   └── database/
│   │       └── NoghreSodDatabase.kt # Room database
│   └── repository/
│       └── ProductRepository.kt # Repository impl.
│
├── presentation/                # UI Controllers
│   ├── ui/
│   │   ├── home/               # Home screens
│   │   ├── product/            # Product screens
│   │   ├── cart/               # Cart screens
│   │   ├── checkout/           # Checkout flow
│   │   ├── orders/             # Order management
│   │   ├── auth/               # Authentication
│   │   └── profile/            # User profile
│   ├── viewmodel/              # ViewModels
│   ├── components/             # Reusable components
│   ├── navigation/
│   │   ├── NoghreSodNavigation.kt
│   │   └── NavigationDestinations.kt
│   └── theme/
│       ├── Theme.kt            # Material Design 3
│       ├── Color.kt            # Color palette
│       └── Type.kt             # Typography
│
├── utils/                       # Utilities
│   ├── Extensions.kt           # Kotlin extensions
│   ├── InputValidators.kt      # Input validation
│   └── Helpers.kt              # Helper functions
│
├── NoghreSodApp.kt             # Application entry point
└── MainActivity.kt             # Main activity
```

---

## 🛠️ Tech Stack

### Language & Frameworks
- **Kotlin 1.9.20** - Modern, concise, and null-safe
- **Jetpack Compose 2023.10** - Declarative UI framework
- **Android Jetpack** - Lifecycle, Navigation, Hilt, Room, DataStore

### Network & Data
- **Retrofit 2.9.0** - Type-safe HTTP client
- **OkHttp 4.11.0** - HTTP client with interceptors
- **GSON 2.10.1** - JSON serialization/deserialization
- **Room 2.6.0** - Type-safe local database

### Architecture & DI
- **Hilt 2.48** - Compile-time safe dependency injection
- **Clean Architecture** - Layered approach
- **MVVM** - Clean separation of concerns

### Async & Reactive
- **Coroutines** - Lightweight concurrency
- **Flow** - Reactive stream handling
- **StateFlow** - Reactive state management

### Security & Storage
- **EncryptedSharedPreferences** - Secure local storage
- **DataStore** - Modern preferences replacement
- **HTTPS Enforcement** - Secure network communication
- **ProGuard/R8** - Code obfuscation

### Testing
- **JUnit 4** - Unit testing
- **MockK** - Mocking framework
- **Espresso** - UI testing
- **Compose Testing** - Compose UI testing

### Firebase & Analytics
- **Firebase Analytics** - User behavior tracking
- **Firebase Messaging** - Push notifications
- **Firebase Crashlytics** - Crash reporting

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 34
- Kotlin 1.9.20 or later
- Java 17 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ya3er02/NoghreSod-Android.git
   cd NoghreSod-Android
   ```

2. **Open in Android Studio**
   ```bash
   # Android Studio will automatically sync Gradle
   ```

3. **Configure local.properties** (if needed)
   ```properties
   sdk.dir=/path/to/android/sdk
   ```

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on emulator or device**
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.noghre.sod/.MainActivity
   ```

---

## 📡 API Integration

### Base URLs
- **Development**: `https://dev-api.noghresod.com/api/v1/`
- **Production**: `https://api.noghresod.com/api/v1/`

### Key Endpoints

**Authentication**
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - User logout

**Products**
- `GET /products` - List products
- `GET /products/{id}` - Product detail
- `GET /products/search` - Search products
- `GET /products/categories` - List categories

**Cart**
- `GET /cart` - Get cart
- `POST /cart/items` - Add item
- `PUT /cart/items/{id}` - Update item
- `DELETE /cart/items/{id}` - Remove item

**Orders**
- `GET /orders` - List orders
- `POST /orders` - Create order
- `GET /orders/{id}` - Order detail
- `GET /orders/{id}/tracking` - Order tracking

**Payments**
- `POST /payments` - Process payment
- `GET /payments/{id}` - Payment status

---

## 🧪 Testing

### Run Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew testDebugUnitTest
```

### Test Structure
- **Unit Tests**: `app/src/test/kotlin/`
- **Instrumented Tests**: `app/src/androidTest/kotlin/`
- **Test Utilities**: `app/src/test/kotlin/com/noghre/sod/utils/`

---

## 📊 Build & Deploy

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Create App Bundle
```bash
./gradlew bundleRelease
```

### Quality Checks
```bash
# Run Detekt (static analysis)
./gradlew detekt

# Run ktlint (code style)
./gradlew ktlint

# Run all quality checks
./gradlew qualityChecks
```

---

## 🔒 Security

- ✅ HTTPS-only API communication
- ✅ Encrypted local storage with EncryptedSharedPreferences
- ✅ Input validation and sanitization
- ✅ Secure token management
- ✅ ProGuard/R8 code obfuscation in release builds
- ✅ Certificate pinning support
- ✅ No hardcoded secrets

---

## 📱 Performance

- 🚀 Lazy loading of images with Coil
- 🚀 Efficient list rendering with LazyColumn/LazyVerticalGrid
- 🚀 Database indexing for fast queries
- 🚀 Coroutine-based async operations
- 🚀 Memory-efficient state management
- 🚀 Proactive resource cleanup

---

## 🌍 Localization

The app is built with localization support:
- **Farsi (Persian)** - Primary language
- **English** - Secondary language
- **RTL Layout Support** - Full right-to-left support
- **Dynamic Language Switching** - Runtime language change

---

## 📝 Implementation Status

See [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) for detailed progress tracking.

**Current Progress: 65% Complete**

✅ Completed:
- Build system configuration
- Dependency management
- Domain models
- DI setup
- Network layer
- Database structure
- Navigation system
- Theme & styling
- Repository implementations

⏳ In Progress:
- Entity & DAO implementations
- Use case implementations
- ViewModel implementations
- UI screen implementations
- Comprehensive testing

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

---

## 👨‍💻 Author

**Yaser** - [@Ya3er02](https://github.com/Ya3er02)

---

## 📞 Support

For issues, questions, or contributions:
1. Open an issue on [GitHub](https://github.com/Ya3er02/NoghreSod-Android/issues)
2. Contact: support@noghresod.com

---

**Made with ❤️ for silver jewelry enthusiasts**
