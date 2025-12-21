# NoghreSod Android - Setup Guide

## Overview
This is a complete, production-ready Android application for the NoghreSod Marketplace platform built with:
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room
- **API**: Retrofit + OkHttp
- **Dependency Injection**: Hilt
- **Coroutines**: Kotlin Coroutines with Flow

## System Requirements
- Android Studio 2023.1.1 or later
- JDK 17+
- Android SDK 34 (Target)
- Minimum Android API 26 (Android 8.0)
- Gradle 8.0+

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2. Open in Android Studio
- Open Android Studio
- Select "Open" and navigate to the project directory
- Wait for Gradle sync to complete

### 3. Configure BuildConfig
Update `local.properties` with your development environment:
```properties
SDK_ROOT=/path/to/android/sdk
KEYSTORE_PATH=/path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=release-key
KEY_PASSWORD=your_key_password
```

### 4. Sync Gradle
```bash
./gradlew clean
./gradlew sync
```

### 5. Build the App
```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

### 6. Run on Emulator/Device
```bash
./gradlew installDebug
```

## Project Structure
```
app/src/main/
├── kotlin/com/noghre/sod/
│   ├── MainActivity.kt              # Entry point
│   ├── NoghreSodApp.kt             # Application class
│   ├── data/
│   │   ├── model/                  # Data models (Product, Order, etc.)
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── local/                  # Room Database DAOs
│   │   ├── remote/                 # Retrofit API Service
│   │   └── repository/             # Repository pattern implementation
│   ├── ui/
│   │   ├── screens/                # Compose screen composables
│   │   ├── components/             # Reusable UI components
│   │   ├── theme/                  # Theming (colors, typography)
│   │   └── navigation/             # Navigation graph
│   ├── viewmodel/                  # MVVM ViewModels
│   ├── di/                         # Hilt dependency injection modules
│   └── utils/                      # Utility functions and extensions
├── res/
│   ├── values/                     # Resources (strings, colors, themes)
│   └── xml/                        # Configuration files
└── AndroidManifest.xml             # App manifest
```

## Key Features Implemented

### Data Layer
- ✅ Room Database for local caching
- ✅ Retrofit for API communication
- ✅ Repository pattern for data abstraction
- ✅ Network interceptors for auth and logging

### UI Layer
- ✅ Jetpack Compose for modern UI
- ✅ Material Design 3 theming
- ✅ Responsive layouts
- ✅ State management with Flow

### Business Logic
- ✅ MVVM architecture with ViewModels
- ✅ Kotlin Coroutines for async operations
- ✅ Clean Architecture principles

### DevOps
- ✅ Hilt for dependency injection
- ✅ GitHub Actions CI/CD pipeline
- ✅ ProGuard rules for release builds
- ✅ Timber logging

## Development Workflow

### Running Tests
```bash
# Unit tests
./gradlew test

# Connected tests (emulator/device)
./gradlew connectedAndroidTest
```

### Code Quality Checks
```bash
# Run linter
./gradlew lint

# Run detekt (Kotlin static analysis)
./gradlew detekt
```

### Build Variants
```bash
# Debug variant (fast, unoptimized)
./gradlew assembleDebug

# Release variant (optimized, requires signing)
./gradlew assembleRelease
```

## API Configuration

Update the API base URL in `build.gradle.kts`:
```kotlin
buildTypes {
    release {
        buildConfigField("String", "API_BASE_URL", "\"https://api.noghre.sod/\"")
    }
    debug {
        buildConfigField("String", "API_BASE_URL", "\"http://localhost:8080/\"")
    }
}
```

## Security Considerations

1. **Network Security**: Certificate pinning enabled for production
2. **Data Storage**: Sensitive data not stored in SharedPreferences
3. **ProGuard**: Code obfuscation enabled for release builds
4. **Permissions**: Minimal permissions requested
5. **API Auth**: Bearer token authentication with interceptor

## Building for Release

1. Generate signing key:
```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release-key
```

2. Update `local.properties` with keystore details

3. Build release APK/AAB:
```bash
./gradlew bundleRelease
```

## Troubleshooting

### Gradle Sync Issues
```bash
./gradlew clean
rm -rf .gradle
./gradlew sync
```

### Dependency Conflicts
```bash
./gradlew dependencies
./gradlew dependencyInsight --configuration debugRuntimeClasspath --dependency <dependency-name>
```

### Build Fails with "Cannot find symbol"
- Ensure all Kotlin files are in correct package paths
- Run `./gradlew clean` followed by rebuild

## GitHub Actions CI/CD

The project includes automated CI/CD pipeline that:
- Builds on every push to main/develop
- Runs unit tests
- Performs lint checks
- Builds debug APK
- Generates security reports

View workflows in `.github/workflows/android-ci.yml`

## Contributing
1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes and test locally
3. Push: `git push origin feature/your-feature`
4. Create pull request

## License
Copyright © 2025 NoghreSod. All rights reserved.

## Support
For issues or questions, please open an issue on GitHub.
