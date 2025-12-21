# NoghreSod Android Marketplace

> Complete Android marketplace application built with Kotlin and Jetpack Compose

[![Android CI/CD](https://github.com/Ya3er02/NoghreSod-Android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/Ya3er02/NoghreSod-Android/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.8-brightgreen.svg)](https://developer.android.com/compose)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

## Features

### 🛍️ Shopping Experience
- Browse products by category
- Search and filter products
- Detailed product views with ratings and reviews
- Add to cart and manage cart items
- Wishlist functionality
- Order tracking and history

### 👤 User Management
- User registration and authentication
- Profile management
- Address management
- Payment methods
- Order history

### 🏗️ Technical Architecture
- **MVVM + Clean Architecture** - Separation of concerns
- **Jetpack Compose** - Modern declarative UI
- **Room Database** - Local data persistence
- **Retrofit + OkHttp** - Network communication
- **Kotlin Coroutines & Flow** - Asynchronous programming
- **Hilt** - Dependency injection
- **Material Design 3** - Modern UI components

### 🔒 Security
- Network security configuration
- Certificate pinning for API
- Secure data storage
- ProGuard code obfuscation
- Bearer token authentication

### 📊 Quality
- Unit tests with MockK
- UI tests with Compose Testing
- Lint checks
- Static code analysis (Detekt)
- Code coverage reporting

## Quick Start

### Prerequisites
- Android Studio 2023.1.1+
- JDK 17+
- Android SDK 34+
- Gradle 8.0+

### Setup

1. **Clone repository**
```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

2. **Open in Android Studio**
- File → Open → Select project directory
- Wait for Gradle sync

3. **Build & Run**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

4. **Run on emulator**
- Create Android Virtual Device (AVD)
- Select "Run → Run 'app'"

## Project Structure
```
app/src/main/
├── kotlin/com/noghre/sod/
│   ├── data/           # Data layer (models, DAOs, repositories)
│   ├── ui/             # UI layer (screens, components, theme)
│   ├── viewmodel/      # MVVM ViewModels
│   ├── di/             # Dependency injection modules
│   ├── utils/          # Utilities and extensions
│   ├── MainActivity.kt
│   └── NoghreSodApp.kt
├── res/
│   ├── values/         # Strings, colors, themes, dimensions
│   └── xml/            # Network security config
└── AndroidManifest.xml
```

## Dependencies

### Core
- androidx.core:core-ktx:1.13.1
- androidx.appcompat:appcompat:1.7.0
- androidx.multidex:multidex:2.0.1

### Compose
- androidx.compose.ui:ui:1.6.8
- androidx.compose.material3:material3:1.2.1
- androidx.activity:activity-compose:1.8.1
- androidx.navigation:navigation-compose:2.7.7

### Networking
- com.squareup.retrofit2:retrofit:2.11.0
- com.squareup.okhttp3:okhttp:4.12.0
- com.squareup.okhttp3:logging-interceptor:4.12.0

### Database
- androidx.room:room-runtime:2.6.1
- androidx.room:room-ktx:2.6.1

### DI
- com.google.dagger:hilt-android:2.51.1
- androidx.hilt:hilt-navigation-compose:1.2.0

### Coroutines
- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3

### Testing
- junit:junit:4.13.2
- io.mockk:mockk:1.13.8
- app.cash.turbine:turbine:1.0.0

## Development Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Run static analysis
./gradlew detekt

# Build and run on device
./gradlew installDebug
```

## CI/CD Pipeline

GitHub Actions automatically:
- ✅ Builds on every push
- ✅ Runs all tests
- ✅ Performs lint checks
- ✅ Generates reports
- ✅ Builds debug APK

## Roadmap

- [ ] Payment integration (Stripe/PayPal)
- [ ] Real-time notifications
- [ ] Product recommendations
- [ ] Seller analytics dashboard
- [ ] Marketplace admin panel
- [ ] Multi-language support
- [ ] Offline functionality

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Troubleshooting

See [SETUP.md](SETUP.md) for detailed troubleshooting guide.

## License

Copyright © 2025 NoghreSod. All rights reserved.

## Contact

- **GitHub**: [@Ya3er02](https://github.com/Ya3er02)
- **Email**: your-email@example.com

---

Built with ❤️ for the NoghreSod Marketplace
