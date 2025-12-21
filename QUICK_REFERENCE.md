# Quick Reference Guide

## Project Structure

```
app/src/main/kotlin/com/noghre/sod/
├── di/                 # Dependency Injection
├── data/              # Data Layer
│   ├── local/        # Local storage
│   ├── remote/       # API layer
│   └── repository/   # Repositories
├── domain/            # Business logic
├── presentation/      # UI Layer
│   ├── navigation/   # Navigation setup
│   ├── screen/       # Screens
│   ├── viewmodel/    # ViewModels
│   └── component/    # Reusable components
├── ui/               # Theme & styling
└── util/             # Utilities
```

## Common Commands

```bash
# Build
./gradlew build              # Full build
./gradlew assembleDebug      # Debug APK
./gradlew bundleRelease      # Release AAB

# Test
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # UI tests

# Clean
./gradlew clean             # Clean build
./gradlew cleanBuildCache   # Clear cache

# Run
./gradlew runDebug          # Install and run
adb install app-debug.apk   # Manual install

# Debug
adb logcat              # View logs
adb devices             # List devices
```

## Key Files

| File | Purpose |
|------|----------|
| `build.gradle.kts` | App configuration |
| `AndroidManifest.xml` | App manifest |
| `build.gradle.kts` (root) | Project config |
| `gradle.properties` | Gradle settings |
| `di/NetworkModule.kt` | API configuration |
| `presentation/navigation/RootNavigation.kt` | App navigation |

## Adding New Feature

1. Create data model in `data/remote/`
2. Add API endpoint in `data/remote/ApiService.kt`
3. Create repository in `data/repository/`
4. Add DI in `di/`
5. Create ViewModel in `presentation/viewmodel/`
6. Create UI screen in `presentation/screen/`
7. Add navigation in `presentation/navigation/`
8. Write tests

## Debugging Tips

```kotlin
// Logging
Timber.d("Message")
Timber.e(exception, "Message")

// Breakpoints
// Click line number in IDE to set breakpoint

// Logcat filtering
adb logcat -s TAG  # Show only this tag
adb logcat -e ERROR  # Show errors only
```

## API Integration

1. Update base URL in `di/NetworkModule.kt`
2. Add endpoints to `data/remote/ApiService.kt`
3. Create DTOs with `@Serializable`
4. Implement repository methods
5. Call from ViewModel
6. Update UI

## Performance Tips

- Use LazyColumn for long lists
- Cache images with Coil
- Minimize recompositions with remember
- Use Flow for state management
- Profile with Android Profiler

## Version Management

**Edit `build.gradle.kts`**:
```kotlin
versionCode = 2
versionName = "1.1.0"
```

## Dependencies

**Add to `build.gradle.kts`**:
```kotlin
implementation("group:artifact:version")
```

## Useful Links

- [Android Docs](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin](https://kotlinlang.org)
- [GitHub Issues](https://github.com/Ya3er02/NoghreSod-Android/issues)
