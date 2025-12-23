# 💍 Noghresod - Silver Jewelry E-Commerce Android App

## Overview

Noghresod is a **production-ready silver jewelry e-commerce platform** for Android, built with modern Android development practices and Jetpack Compose.

### Key Features

✅ **Product Management**
- Comprehensive product catalog with filtering and search
- High-quality images and detailed product information
- Category-based browsing

✅ **Shopping Experience**
- Smooth cart management
- Multiple checkout options
- Real-time order tracking

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

## Architecture

### Layered Architecture

```
┌────────────────────────────┐
│   PRESENTATION LAYER                    │
│   (Screens, ViewModels, Components)     │
└───────────┬─────────────────┘
             │
┌───────────┴─────────────────┐
│   APPLICATION LAYER                    │
│   (ViewModels, State Management)        │
└───────────┬─────────────────┘
             │
┌───────────┴─────────────────┐
│   DOMAIN LAYER                         │
│   (Use Cases, Business Logic)           │
└───────────┬─────────────────┘
             │
┌───────────┴─────────────────┐
│   DATA LAYER                           │
│   (Repositories, DB, APIs)             │
└────────────────────────────┘
```

---

## Project Structure

```
app/src/main/kotlin/com/noghre/sod/
├── di/                          # Dependency Injection
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
├── domain/                      # Business Logic
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/                        # Data Implementation
│   ├── local/
│   │   ├── entity/
│   │   ├── dao/
│   │   ├── database/
│   │   └── datasource/
│   ├── remote/
│   │   ├── api/
│   │   ├── dto/
│   │   ├── interceptor/
│   │   └── client/
│   └── repository/
├── presentation/                # UI Controllers
│   ├── ui/
│   │   ├── home/
│   │   ├── product/
│   │   ├── cart/
│   │   ├── checkout/
│   │   ├── orders/
│   │   ├── auth/
│   │   ├── profile/
│   ├── viewmodel/
│   ├── components/
│   ├── navigation/
│   ├── theme/
│   └── MainActivity.kt
├── utils/
│   ├── Extensions.kt
│   ├── InputValidators.kt
│   ├── AnalyticsHelper.kt
│   ├── LoggingUtils.kt
│   └── EncryptionUtils.kt
├── core/                        # Core Utilities
└── NoghreSodApp.kt
```

---

## Tech Stack

### Language & Frameworks
- **Kotlin** - Modern, concise, and null-safe
- **Jetpack Compose** - Declarative UI framework
- **Android Jetpack** - Lifecycle, Navigation, Hilt, Room, DataStore

### Network & Data
- **Retrofit 2** - Type-safe HTTP client
- **OkHttp 3** - HTTP client with interceptors
- **Gson** - JSON serialization/deserialization
- **Room** - Type-safe local database

### Architecture & DI
- **Hilt** - Compile-time safe dependency injection
- **MVVM** - Clean separation of concerns
- **Clean Architecture** - Layered approach

### Async & Reactive
- **Coroutines** - Lightweight concurrency
- **Flow** - Reactive stream handling
- **StateFlow** - Reactive state management

### Security & Storage
- **EncryptedSharedPreferences** - Secure local storage
- **DataStore** - Modern preferences replacement
- **HTTPS Enforcement** - Secure network communication

### Testing
- **JUnit 4** - Unit testing
- **Espresso** - UI testing
- **Compose Testing** - Compose UI testing

### Firebase & Analytics
- **Firebase Analytics** - User behavior tracking
- **Firebase Messaging** - Push notifications
- **Firebase Crashlytics** - Crash reporting

---

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android API Level 24 or higher
- Java 17 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ya3er02/NoghreSod-Android.git
   cd NoghreSod-Android
   ```

2. **Create local.properties**
   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on emulator or device**
   ```bash
   ./gradlew installDebug
   ```

---

## API Integration

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
- `GET /orders` - List user orders
- `POST /orders` - Create order
- `GET /orders/{id}` - Order detail
- `GET /orders/{id}/tracking` - Order tracking

---

## Database Schema

### Entities
1. **ProductEntity** - Product catalog
2. **CartEntity & CartItemEntity** - Shopping cart
3. **OrderEntity & OrderTrackingEntity** - Orders
4. **UserEntity & AddressEntity** - User profiles
5. **AuthTokenEntity** - Auth tokens

---

## Screens

- **HomeScreen** - Featured and trending products
- **ProductListScreen** - Searchable product catalog
- **ProductDetailScreen** - Detailed product info
- **CartScreen** - Shopping cart management
- **CheckoutScreen** - Address and payment selection
- **OrdersScreen** - Order history
- **LoginScreen** - User authentication
- **RegisterScreen** - New user signup
- **ProfileScreen** - User profile & settings

---

## Build & Deploy

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Sign APK
```bash
./gradlew bundleRelease
```

---

## Performance Optimizations

- 🚀 Lazy loading of images with Coil
- 🚀 Efficient list rendering with LazyColumn/LazyVerticalGrid
- 🚀 Database indexing for fast queries
- 🚀 Coroutine-based async operations
- 🚀 Memory-efficient state management

---

## Security Practices

- 🔐 HTTPS-only API communication
- 🔐 Encrypted local storage with EncryptedSharedPreferences
- 🔐 Input validation and sanitization
- 🔐 Secure token management
- 🔐 ProGuard/R8 code obfuscation in release builds

---

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch
2. Commit with clear messages
3. Write tests for new features
4. Submit a pull request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support

For issues and questions:
- Open an issue on GitHub
- Contact: support@noghresod.com

---

**Made with ❤️ by Yaser**
