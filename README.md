# NoghreSod Android Marketplace

> Complete Android marketplace application built with Kotlin and Jetpack Compose

[![Android CI](https://github.com/Ya3er02/NoghreSod-Android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/Ya3er02/NoghreSod-Android/actions)
[![codecov](https://codecov.io/gh/Ya3er02/NoghreSod-Android/branch/main/graph/badge.svg)](https://codecov.io/gh/Ya3er02/NoghreSod-Android)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7.5-green.svg)](https://developer.android.com/compose)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
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

### 🧪 Testing & Quality
- ✅ 80%+ Code Coverage with JaCoCo
- ✅ Screenshot Testing with Paparazzi
- ✅ Unit Tests with MockK
- ✅ Integration Tests
- ✅ Automated CI/CD Pipeline
- ✅ Firebase Test Lab Integration

### 🚀 Performance
- ⚡ Optimized Image Loading (Coil)
- ⚡ Shimmer Loading States
- ⚡ Memory Leak Detection (LeakCanary)
- ⚡ Recomposition Tracking
- ⚡ Network Connectivity Monitoring

### ♿ Accessibility
- 🌐 Full RTL Support (Persian/Arabic)
- 🔊 Screen Reader Support
- 📱 TalkBack Compatible
- 🎨 Dark Mode Support
- 🎯 Semantic Labels

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

2. **Configure local properties**
```bash
cp local.properties.example local.properties
# Edit with your API credentials
```

3. **Open in Android Studio**
- File → Open → Select project directory
- Wait for Gradle sync

4. **Build & Run**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

For detailed setup instructions, see [README_SETUP.md](README_SETUP.md).

## Project Structure
```
app/src/main/
├── kotlin/com/noghre/sod/
│   ├── domain/         # Domain layer (entities, use cases, results)
│   ├── data/           # Data layer (models, DAOs, repositories)
│   ├── ui/             # UI layer (screens, components, theme)
│   ├── viewmodel/      # MVVM ViewModels
│   ├── di/             # Dependency injection modules
│   ├── utils/          # Utilities and extensions
│   ├── analytics/      # Analytics tracking
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
- androidx.compose.ui:ui:1.7.5
- androidx.compose.material3:material3:1.2.1
- androidx.activity:activity-compose:1.8.1
- androidx.navigation:navigation-compose:2.8.0

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

### Image Loading
- io.coil-kt:coil-compose:2.5.0

### Testing
- junit:junit:4.13.2
- io.mockk:mockk:1.13.8
- app.cash.turbine:turbine:1.0.0
- app.cash.paparazzi:paparazzi:1.3.1

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

# Run screenshot tests
./gradlew verifyPaparazziDebug

# Generate coverage report
./gradlew jacocoTestReport

# Run lint
./gradlew lint

# Run static analysis
./gradlew detekt

# Generate API documentation
./gradlew dokkaHtml

# Build and run on device
./gradlew installDebug

# Run all checks
./gradlew check
```

## CI/CD Pipeline

GitHub Actions automatically:
- ✅ Builds on every push
- ✅ Runs all tests
- ✅ Performs lint checks
- ✅ Validates commits (Conventional Commits)
- ✅ Generates coverage reports
- ✅ Scans dependencies for vulnerabilities
- ✅ Builds debug APK
- ✅ Updates dependencies (Dependabot)

## Documentation

- **[README_SETUP.md](README_SETUP.md)** - Detailed setup and troubleshooting
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Development guidelines and conventions
- **[IMPROVEMENTS_PART_1.md](IMPROVEMENTS_PART_1.md)** - Architecture and domain layer
- **[IMPROVEMENTS_PART_2.md](IMPROVEMENTS_PART_2.md)** - Utilities and analytics
- **[IMPLEMENTATION_QUICK_START.md](IMPLEMENTATION_QUICK_START.md)** - Quick reference guide

## Roadmap

- [ ] Payment integration (Stripe/PayPal)
- [ ] Real-time notifications
- [ ] Product recommendations
- [ ] Seller analytics dashboard
- [ ] Marketplace admin panel
- [ ] Multi-language support (currently Persian/English)
- [ ] Offline functionality
- [ ] Advanced search with AI suggestions

## Contributing

Contributions are welcome! Please:
1. Read [CONTRIBUTING.md](CONTRIBUTING.md)
2. Fork the repository
3. Create a feature branch: `git checkout -b feature/your-feature`
4. Follow [Conventional Commits](https://www.conventionalcommits.org/)
5. Add tests for your changes
6. Submit a pull request

## Troubleshooting

See [README_SETUP.md](README_SETUP.md) for detailed troubleshooting and [CONTRIBUTING.md](CONTRIBUTING.md) for development issues.

## License

Copyright © 2025 NoghreSod. All rights reserved.

## Contact

- **GitHub**: [@Ya3er02](https://github.com/Ya3er02)
- **Project Issues**: [GitHub Issues](https://github.com/Ya3er02/NoghreSod-Android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Ya3er02/NoghreSod-Android/discussions)

---

Built with ❤️ for the NoghreSod Marketplace
