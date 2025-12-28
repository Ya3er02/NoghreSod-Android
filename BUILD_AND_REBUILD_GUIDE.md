# 🏗️ Build & Rebuild Guide - NoghreSod Android

## Table of Contents
1. [Quick Build Commands](#quick-build-commands)
2. [First-Time Setup](#first-time-setup)
3. [Clean Rebuild](#clean-rebuild)
4. [Build Variants](#build-variants)
5. [Performance Tips](#performance-tips)
6. [Troubleshooting](#troubleshooting)
7. [CI/CD Integration](#cicd-integration)

---

## Quick Build Commands

### Debug APK (Development)
```bash
# Simple debug build (fastest)
./gradlew assembleDebug

# With specific flavor
./gradlew assembleDevDebug

# Install directly to device
./gradlew installDebug
```

### Release APK (Production)
```bash
# Production build with optimizations
./gradlew assembleProductionRelease

# Staging build (pre-production)
./gradlew assembleStagingRelease
```

### Tests
```bash
# Run all unit tests
./gradlew test

# Run with coverage
./gradlew test --coverage

# Run specific test
./gradlew test --tests "*ProductsViewModelTest"

# Run instrumentation tests (on device/emulator)
./gradlew connectedAndroidTest
```

---

## First-Time Setup

### 1️⃣ Clone Repository
```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2️⃣ Setup Secrets (REQUIRED)
```bash
# Copy example file
cp local.properties.example local.properties

# Edit with your credentials
cat > local.properties << 'EOF'
zarinpal.merchant.id=YOUR_PRODUCTION_MERCHANT_ID
zarinpal.sandbox.merchant.id=YOUR_SANDBOX_MERCHANT_ID
EOF
```

**🔒 CRITICAL**: See [SETUP_SECRETS.md](SETUP_SECRETS.md) for complete setup instructions.

### 3️⃣ Install JDK 17+
```bash
# macOS (using Homebrew)
brew install openjdk@17

# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk

# Verify installation
java -version
# Should output: openjdk version "17.x.x" or similar
```

### 4️⃣ Initial Build
```bash
# Sync Gradle and download dependencies
./gradlew assemble

# Build debug APK
./gradlew assembleDevDebug
```

### 5️⃣ Open in Android Studio
```bash
# Using Android Studio
# File → Open → Select NoghreSod-Android
# Let it sync and index
```

---

## Clean Rebuild

### Full Clean + Rebuild (When Things Break)
```bash
# Option 1: Complete clean
./gradlew clean build

# Option 2: Nuclear option (if stuck)
./gradlew clean
rm -rf .gradle/
rm -rf build/
rm -rf app/build/
./gradlew build
```

### Clear Android Studio Cache
```bash
# macOS
rm -rf ~/Library/Caches/JetBrains/AndroidStudio*
rm -rf ~/Library/Application\ Support/JetBrains/AndroidStudio*

# Linux
rm -rf ~/.cache/JetBrains/AndroidStudio*
rm -rf ~/.config/JetBrains/AndroidStudio*

# Windows
rmdir %APPDATA%\JetBrains\AndroidStudio* /s /q
```

### Invalidate IDE Caches
In Android Studio:
```
File → Invalidate Caches → Invalidate and Restart
```

---

## Build Variants

### Product Flavors (Environment)

| Flavor | Purpose | Merchant ID | API |
|--------|---------|-------------|-----|
| `dev` | Local development | Sandbox | Dev server |
| `staging` | Pre-production | Sandbox | Staging server |
| `production` | Live release | Production | Production |

### Build Types

| Type | Minified | Debugging | Dex | Usage |
|------|----------|-----------|-----|-------|
| `debug` | ❌ | ✅ | Unoptimized | Development |
| `release` | ✅ | ❌ | Optimized | Production |

### Build Variants Combinations

```bash
# Dev + Debug (fastest, sandbox payments)
./gradlew assembleDevDebug          # APK: app-dev-debug.apk

# Dev + Release (with optimizations, sandbox)
./gradlew assembleDevRelease        # APK: app-dev-release.apk

# Staging + Release (pre-production testing)
./gradlew assembleStagingRelease    # APK: app-staging-release.apk

# Production + Release (live release, REAL PAYMENTS)
./gradlew assembleProductionRelease # APK: app-production-release.apk
```

### Output Locations
```
app/build/outputs/apk/
├── dev/
│   ├── debug/
│   │   └── app-dev-debug.apk
│   └── release/
│       └── app-dev-release.apk
├── staging/
│   └── release/
│       └── app-staging-release.apk
└── production/
    └── release/
        └── app-production-release.apk
```

---

## Performance Tips

### ⚡ Fastest Builds

```bash
# 1. Use daemon mode (enabled by default in gradle.properties)
# Reuses JVM across builds

# 2. Disable unnecessary checks
./gradlew assembleDebug -x lint

# 3. Skip tests during development
./gradlew assembleDebug --exclude-task test

# 4. Incremental builds (only build what changed)
# Default behavior, but can be optimized in gradle.properties

# 5. Parallel build
./gradlew assembleDebug --parallel
```

### 📊 Build Time Analysis

```bash
# Analyze build time
./gradlew assembleDebug --profile

# Output: build/reports/profile/*.html
# Open in browser to see bottlenecks
```

### 🔧 JVM Memory Tuning

In `gradle.properties`, adjust for your system:

```properties
# For 8GB RAM machine
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1g

# For 16GB+ RAM machine
org.gradle.jvmargs=-Xmx8192m -XX:MaxMetaspaceSize=2g

# For lower-end machines
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.workers.max=4
```

### 🚫 Disable Optimizations During Development

```bash
# Faster debug builds (no minification/shrinking)
./gradlew assembleDebug

# These are already disabled in debug builds,
# but you can force by setting:
export GRADLE_OPTS="-Xmx6g"
```

---

## Troubleshooting

### Issue 1: "BUILD FAILED - Merchant ID not found"

```
Error: Zarinpal merchant ID is not configured
```

**Solution:**
```bash
# 1. Check local.properties exists
ls local.properties

# 2. Verify credentials are set
grep zarinpal local.properties

# 3. Rebuild gradle cache
./gradlew clean
./gradlew assembleDebug
```

### Issue 2: "Gradle daemon died unexpectedly"

```bash
# Stop gradle daemon
./gradlew --stop

# Clear gradle cache
rm -rf ~/.gradle/

# Rebuild
./gradlew assembleDebug
```

### Issue 3: "Out of Memory" during build

```bash
# Increase JVM memory in gradle.properties
org.gradle.jvmargs=-Xmx8192m -XX:MaxMetaspaceSize=2g

# Or set temporarily
export GRADLE_OPTS="-Xmx8g"
./gradlew assembleDebug
```

### Issue 4: "Cannot find symbol" errors

```bash
# Rebuild with fresh cache
./gradlew clean build

# Or invalidate AS caches
# File → Invalidate Caches → Invalidate and Restart
```

### Issue 5: "Timeout waiting for exclusive access to file"

```bash
# Another gradle process is running
./gradlew --stop

# Also kill any background processes
pkill -f gradle

# Try again
./gradlew assembleDebug
```

### Issue 6: "Failed to resolve google-services plugin"

```bash
# Update repositories
./gradlew clean

# Add to settings.gradle.kts if missing
# google()
# mavenCentral()
```

### Issue 7: BuildConfig fields are outdated

```bash
# Gradle cache issue
./gradlew clean

# Restart Android Studio
# File → Invalidate Caches → Invalidate and Restart

# Rebuild
./gradlew assembleDebug
```

---

## CI/CD Integration

### GitHub Actions Build Workflow

```yaml
name: Build & Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Create secrets
        run: |
          echo "zarinpal.merchant.id=${{ secrets.ZARINPAL_PROD_ID }}" > local.properties
          echo "zarinpal.sandbox.merchant.id=${{ secrets.ZARINPAL_SANDBOX_ID }}" >> local.properties
      
      - name: Run tests
        run: ./gradlew test
      
      - name: Build debug APK
        run: ./gradlew assembleDebug
      
      - name: Build release APK
        run: ./gradlew assembleProductionRelease
      
      - name: Upload to artifacts
        uses: actions/upload-artifact@v3
        with:
          name: apk
          path: app/build/outputs/apk/
```

### Local Environment Variables

```bash
# Set for this session
export GRADLE_OPTS="-Xmx6g"
./gradlew assembleDebug

# Or create .env file (not committed)
echo 'GRADLE_OPTS=-Xmx6g' > .env
source .env
```

---

## Best Practices

### ✅ Do's

- ✅ Always run `./gradlew clean` after major changes
- ✅ Use specific flavors when building (dev/staging/production)
- ✅ Keep `gradle.properties` optimized for your machine
- ✅ Use `--offline` flag for faster builds if all deps cached
- ✅ Enable gradle daemon (default)

### ❌ Don'ts

- ❌ Don't commit `local.properties`
- ❌ Don't commit BuildCache or .gradle folder
- ❌ Don't manually edit generated files (R.java, BuildConfig, etc.)
- ❌ Don't ignore BuildConfig failures
- ❌ Don't use old Gradle versions (< 8.0)

---

## Gradle Wrapper Management

### Update Gradle Wrapper

```bash
# Check current version
./gradlew --version

# Update to latest
./gradlew wrapper --gradle-version=8.7

# Commit wrapper
git add gradle/wrapper/
git commit -m "chore: Update gradle wrapper to 8.7"
```

### Offline Mode

```bash
# Build without downloading (uses cached deps)
./gradlew assembleDebug --offline

# Only works if all dependencies are cached
```

---

## Expected Build Times

| Build Type | Machine | Time | Notes |
|------------|---------|------|-------|
| Clean Debug | Modern (2024) | 45-60s | Fresh build, all deps |
| Incremental Debug | Modern | 5-10s | Small change |
| Clean Release | Modern | 90-120s | With R8 optimization |
| CI/CD Pipeline | GitHub Actions | 3-5m | First run |
| CI/CD Pipeline | GitHub Actions (cached) | 1-2m | Subsequent runs |

---

## Support & Issues

If build fails:
1. Check [SETUP_SECRETS.md](SETUP_SECRETS.md) for secrets configuration
2. Review [Troubleshooting](#troubleshooting) section above
3. Check Android Gradle Plugin documentation
4. Create GitHub issue with error logs

---

**Last Updated:** 2025-12-28
**Gradle Version:** 8.0+
**JDK Version:** 17+
**Status:** ✅ Production-Ready
