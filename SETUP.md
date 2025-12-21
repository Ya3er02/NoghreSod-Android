# Development Setup Guide

## System Requirements

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Gradle**: 8.1.1 or later
- **Android SDK**: API 34
- **Min SDK**: API 24

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2. Open in Android Studio

```bash
android-studio .
```

### 3. Configure Android SDK

1. Open **File** → **Project Structure**
2. Set SDK Location (or let Android Studio auto-detect)
3. Download required SDK components

### 4. Update API Configuration

Edit `app/src/main/kotlin/com/noghre/sod/di/NetworkModule.kt`:

```kotlin
@Provides
@Singleton
fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://your-api-domain.com/")  // ← Update this
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
```

### 5. Build the Project

```bash
./gradlew clean build
```

### 6. Run on Emulator/Device

**On Emulator**:
```bash
./gradlew installDebug
```

**On Connected Device**:
```bash
adb devices  # Check connected devices
./gradlew installDebug
```

## Development Workflow

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install and run
./gradlew runDebug
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.noghre.sod.presentation.viewmodel.AuthViewModelTest"

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Cleaning

```bash
# Clean build
./gradlew clean

# Clear cache
./gradlew cleanBuildCache
```

## IDE Configuration

### Code Style

1. Install "Kotlin" plugin in Android Studio
2. **Settings** → **Editor** → **Code Style** → **Kotlin**
3. Choose "Kotlin Convention" or custom style

### Formatter

```bash
# Format all Kotlin files
./gradlew ktlintFormat
```

### Linting

```bash
# Check linting issues
./gradlew ktlint
```

## Debugging

### Android Studio Debugger

1. Set breakpoints by clicking on line numbers
2. Run in debug mode:
   ```bash
   ./gradlew installDebug
   ```
3. Wait for execution to hit breakpoint
4. Inspect variables in debugger

### Logcat

```bash
# View logs
adb logcat

# Filter by tag
adb logcat | grep "NoghreSod"

# Or use in Android Studio: Logcat tab
```

### Android Profiler

1. **Run** → **Attach Debugger to Android Process**
2. Select your process
3. Open **View** → **Tool Windows** → **Profiler**

## Emulator Setup

### Creating AVD

1. **Tools** → **Device Manager**
2. Click **Create Device**
3. Select device type (Phone)
4. Select system image (API 34 recommended)
5. Configure settings
6. Click **Finish**

### Running AVD

```bash
# List available AVDs
emulator -list-avds

# Launch specific AVD
emulator -avd Pixel_6_API_34
```

## Building APK

### Debug APK

```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

1. Create keystore (if not exists):
   ```bash
   keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```

2. Update `build.gradle.kts`:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("release.keystore")
           storePassword = "your-password"
           keyAlias = "release"
           keyPassword = "your-password"
       }
   }
   ```

3. Build:
   ```bash
   ./gradlew assembleRelease
   # Output: app/build/outputs/apk/release/app-release.apk
   ```

## Useful Gradle Tasks

```bash
# List all available tasks
./gradlew tasks

# View dependencies
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates

# Build report
./gradlew build --scan
```

## Troubleshooting

### Gradle Sync Issues

```bash
# Clean gradle
./gradlew clean

# Refresh all dependencies
./gradlew build --refresh-dependencies
```

### Out of Memory

Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m
```

### Build Fails

1. Check **Build** → **Clean Project**
2. **File** → **Invalidate Caches**
3. Update Android Studio
4. Check Java version:
   ```bash
   java -version  # Should be 17+
   ```

## Git Setup

### Initial Clone

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### Create Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### Commit Changes

```bash
git add .
git commit -m "[FEATURE] Add your feature"
git push origin feature/your-feature-name
```

## Environment Variables

Create `.env.local` (not committed):

```properties
API_KEY=your_api_key
API_SECRET=your_api_secret
BASE_URL=https://your-domain.com/
```

## Additional Resources

- [Android Documentation](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Retrofit Documentation](https://square.github.io/retrofit/)
