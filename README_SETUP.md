# NoghreSod Android - Setup Guide

## Prerequisites

- **Android Studio:** Hedgehog (2023.1.1) or later
- **JDK:** Version 17 or later
- **Android SDK:** API 34 (Android 14)
- **Gradle:** 8.0+ (usually bundled with Android Studio)

## Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2. Configure API Keys

Copy the example properties file:

```bash
cp local.properties.example local.properties
```

Edit `local.properties` and add your credentials:

```properties
API_BASE_URL=https://api.noghresod.com
API_KEY=your_actual_key
API_SECRET=your_actual_secret
```

### 3. Build

Sync Gradle and build the project:

```bash
./gradlew clean build
```

### 4. Run

Run on emulator or device:

```bash
./gradlew installDebug
```

Or open in Android Studio and press `Run` (Shift+F10).

## Testing

### Unit Tests

```bash
./gradlew test
```

### Screenshot Tests (Paparazzi)

```bash
# Verify against golden images
./gradlew verifyPaparazziDebug

# Create/update golden images
./gradlew recordPaparazziDebug
```

### Code Coverage

```bash
./gradlew jacocoTestReport
# View: app/build/reports/jacoco/index.html
```

### All Checks

```bash
./gradlew check
```

## Build Variants

- **debug:** Development with logging, debuggable
- **release:** Production with ProGuard/R8 minification

Build release APK:

```bash
./gradlew assembleRelease
```

## IDE Setup (Android Studio)

1. Open `File → Project Structure`
2. Verify SDK location (should auto-detect)
3. Set JDK version to 17 in `Project → SDK Location`
4. Enable code inspections: `File → Settings → Editor → Inspections`
5. Apply EditorConfig: Installed automatically with Android Studio 2023.1+

## Troubleshooting

### Gradle Sync Fails

```bash
./gradlew clean
./gradlew --refresh-dependencies build
```

Or in Android Studio: `File → Invalidate Caches → Invalidate and Restart`

### Build Timeout

Increase Gradle daemon memory in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4096m
```

### local.properties Errors

Ensure file exists and is not in .gitignore violations:

```bash
cp local.properties.example local.properties
echo "local.properties" >> .gitignore
```

### Test Failures

Clear test cache:

```bash
./gradlew cleanTest test
```

## Command Reference

| Command | Purpose |
|---------|----------|
| `./gradlew build` | Clean build |
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew test` | Run unit tests |
| `./gradlew lint` | Run Android lint |
| `./gradlew check` | Run all checks (lint, tests, detekt) |
| `./gradlew jacocoTestReport` | Generate coverage report |
| `./gradlew dokkaHtml` | Generate HTML documentation |

## Performance Tips

- Use incremental builds: `./gradlew build` (not clean)
- Enable Gradle daemon: Already enabled by default
- Use parallel build: Add `org.gradle.parallel=true` to `gradle.properties`
- Build on-device testing: Use Firebase Test Lab for cloud tests

## Documentation

- **Quick Start:** [IMPLEMENTATION_QUICK_START.md](IMPLEMENTATION_QUICK_START.md)
- **Architecture:** [IMPROVEMENTS_PART_1.md](IMPROVEMENTS_PART_1.md)
- **Features:** [IMPROVEMENTS_PART_2.md](IMPROVEMENTS_PART_2.md)
- **Contributing:** [CONTRIBUTING.md](CONTRIBUTING.md)

## Support

For setup issues:

1. Check [CONTRIBUTING.md](CONTRIBUTING.md) troubleshooting section
2. Review GitHub Issues for similar problems
3. Create a new issue with:
   - Error message/logs
   - Android Studio version
   - OS (Windows/Mac/Linux)
   - JDK version output

## Next Steps

1. Read [IMPLEMENTATION_QUICK_START.md](IMPLEMENTATION_QUICK_START.md)
2. Explore the project structure
3. Create your first feature branch
4. Follow [CONTRIBUTING.md](CONTRIBUTING.md) guidelines
