# 💻 Development Guide - NoghreSod Android

**Complete guide for developers: environment setup, project configuration, coding standards, and workflow.**

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [System Requirements](#system-requirements)
3. [Environment Setup](#environment-setup)
4. [Project Configuration](#project-configuration)
5. [Build & Run](#build--run)
6. [Coding Standards](#coding-standards)
7. [Git Workflow](#git-workflow)
8. [Troubleshooting](#troubleshooting)

---

## Quick Start

**For experienced Android developers:**

```bash
# 1. Clone
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android

# 2. Setup secrets (required!)
cp local.properties.example local.properties
# Edit local.properties with your Zarinpal credentials

# 3. Build
./gradlew assembleDevDebug

# 4. Install
./gradlew installDebug

# 5. Run (on emulator/device)
adb shell am start -n com.noghre.sod.debug/.MainActivity
```

**Still need help?** → Follow [Full Setup](#environment-setup) below.

---

## System Requirements

### Minimum
- **OS:** macOS 10.14+, Windows 10, Ubuntu 18.04+
- **RAM:** 8 GB (4 GB for Gradle)
- **Disk:** 15 GB
- **JDK:** 17+
- **Android SDK:** 34+

### Recommended
- **OS:** macOS 12+, Windows 11, Ubuntu 22.04+
- **RAM:** 16 GB (6-8 GB for Gradle)
- **Disk:** 50+ GB
- **JDK:** 17 LTS (OpenJDK)
- **Android SDK:** 34+ with latest build-tools

### Verify Your System

```bash
# Java version (should be 17+)
java -version

# Android SDK (should exist)
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms/android-34
```

---

## Environment Setup

### Step 1: Install JDK 17

#### macOS (Homebrew)
```bash
brew install openjdk@17
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
java -version  # Verify
```

#### Windows (Chocolatey)
```powershell
choco install openjdk17
# System Properties → Environment Variables
# Add: JAVA_HOME = C:\Program Files\OpenJDK\jdk-17.x.x
java -version  # Verify
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk openjdk-17-jdk-headless
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
java -version  # Verify
```

### Step 2: Install Android SDK

#### Option A: Android Studio (Recommended)
1. Download from [developer.android.com/studio](https://developer.android.com/studio)
2. Install following wizard
3. Launch Android Studio
4. SDK Manager automatically installs required packages

#### Option B: Command Line
```bash
# Download
wget https://dl.google.com/android/repository/commandlinetools-linux-10135233_latest.zip
unzip commandlinetools-*.zip
mv cmdline-tools ~/android-sdk/

# Set ANDROID_HOME
echo 'export ANDROID_HOME=$HOME/android-sdk' >> ~/.bashrc
echo 'export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH' >> ~/.bashrc
source ~/.bashrc

# Install packages
sdkmanager --install "platforms;android-34"
sdkmanager --install "build-tools;34.0.0"
sdkmanager --install "emulator"
sdkmanager --install "platform-tools"
```

### Step 3: Configure IDE

#### Android Studio

1. **JDK Configuration**
   ```
   File → Project Structure → SDK Location
   → Set JDK location to /path/to/java/17
   ```

2. **Memory Settings**
   - Edit `$ANDROID_STUDIO_HOME/bin/studio.vmoptions`
   ```
   -Xms2g
   -Xmx8g
   -XX:MaxMetaspaceSize=2g
   ```

3. **Gradle Configuration**
   ```
   File → Settings → Build, Execution, Deployment → Gradle
   → Gradle VM options: -Xmx6g
   ```

---

## Project Configuration

### Step 1: Clone Repository

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### Step 2: Setup Secrets (REQUIRED)

**This step must be completed before building!**

```bash
# Copy example file
cp local.properties.example local.properties

# Edit with your credentials
vim local.properties  # or your preferred editor
```

**Required properties:**
```properties
# Production Zarinpal Merchant ID
zarinpal.merchant.id=YOUR_PRODUCTION_ID

# Sandbox Zarinpal Merchant ID (for testing)
zarinpal.sandbox.merchant.id=YOUR_SANDBOX_ID
```

### Step 3: Get Zarinpal Credentials

**If you don't have Zarinpal credentials:**

1. Register at [zarinpal.com](https://www.zarinpal.com)
2. Verify your account
3. Navigate to Dashboard → Merchants
4. Create merchant profile
5. Get your Merchant ID for production and sandbox
6. Add to `local.properties`

### Step 4: Sync Gradle

```bash
# In Android Studio: File → Sync Now
# Or command line:
./gradlew clean build
```

---

## Build & Run

### Build Variants

Three build flavors for different environments:

```bash
# Development (Uses Sandbox Merchant ID)
./gradlew assembleDevDebug
./gradlew installDebug

# Staging (Uses Sandbox Merchant ID)
./gradlew assembleStagingRelease
./gradlew installStagingRelease

# Production (Uses Production Merchant ID)
./gradlew assembleProductionRelease
./gradlew installProductionRelease
```

### Run Tests

```bash
# All unit tests
./gradlew test

# With coverage report
./gradlew test --coverage

# Specific test class
./gradlew test --tests "*ProductsViewModelTest"

# Specific test method
./gradlew test --tests "*ProductsViewModelTest.testGetProducts"
```

### Run App

```bash
# Build & install (debug build)
./gradlew installDebug

# Launch with adb
adb shell am start -n com.noghre.sod.debug/.MainActivity

# View logs
adb logcat | grep NoghreSod

# Stop app
adb shell am force-stop com.noghre.sod.debug
```

### Performance Tips

```bash
# Parallel build (faster)
./gradlew assembleDebug --parallel

# Incremental compilation
./gradlew build --build-cache

# Use daemon (reuses process)
./gradlew build -Dorg.gradle.daemon.enable=true

# Increase JVM heap (if needed)
export GRADLE_OPTS="-Xmx8192m"
./gradlew build
```

---

## Coding Standards

### Kotlin Conventions

```kotlin
// Classes: PascalCase
class ProductViewModel { }

// Functions & Variables: camelCase
fun getProducts(): List<Product> { }
val productList = mutableListOf<Product>()

// Constants: UPPER_SNAKE_CASE
companion object {
    private const val DEFAULT_TIMEOUT = 5000L
}

// Type aliases for clarity
typealias ProductList = List<Product>
typealias OnProductSelected = (Product) -> Unit
```

### File Organization

```kotlin
// 1. Package declaration
package com.noghre.sod.presentation.viewmodel

// 2. Imports (organized)
import android.content.Context
import androidx.lifecycle.ViewModel
import com.noghre.sod.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

// 3. Type aliases
typealias OnError = (String) -> Unit

// 4. Class definition
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    // Properties
    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    
    // Methods
    init { loadProducts() }
}
```

### Documentation

```kotlin
/**
 * Fetches products from repository with optional filtering.
 *
 * @param filters List of filters to apply (empty = no filtering)
 * @return Flow of product entities
 * @throws RepositoryException if database query fails
 *
 * Example:
 * ```
 * getProductsUseCase(listOf(PriceFilter(min=100, max=500)))
 *     .collect { products -> /* ... */ }
 * ```
 */
suspend fun getProducts(filters: List<Filter>): Flow<List<ProductEntity>> {
    // Implementation
}
```

### Architecture Rules

#### Presentation Layer
- Only ViewModels, Composables, and Screens
- No business logic
- No direct API/Database calls
- Use sealed classes for state

#### Domain Layer
- UseCases represent operations
- Entities are immutable (data classes)
- No framework dependencies
- Pure Kotlin (stdlib only)

#### Data Layer
- Repository implementations
- Local data source (Room)
- Remote data source (Retrofit)
- Mappers for conversions

### Testing Requirements

- Minimum 80% coverage for new code
- Unit tests for all UseCases
- Integration tests for Repositories
- UI tests for critical Composables
- Mock external dependencies

---

## Git Workflow

### Branch Naming

```bash
# Features
git checkout -b feature/product-filtering

# Bug fixes
git checkout -b fix/cart-calculation-bug

# Documentation
git checkout -b docs/api-reference

# Chores
git checkout -b chore/dependency-update
```

### Commit Messages

**Format:** `type(scope): description`

```bash
# Feature
git commit -m "feat(products): add advanced filtering"

# Fix
git commit -m "fix(cart): fix total calculation with discounts"

# Tests
git commit -m "test(checkout): add payment flow tests"

# Documentation
git commit -m "docs: update README with setup instructions"

# Chore
git commit -m "chore: update Kotlin to 1.9.20"
```

### Pull Request Process

1. Create branch from `main`
2. Implement feature/fix
3. Add tests (minimum 80% coverage)
4. Update CHANGELOG.md
5. Create PR with description
6. Wait for CI/CD checks to pass
7. Code review from team
8. Merge after approval

### Pre-commit Checklist

- [ ] Code follows style guide
- [ ] All tests pass locally
- [ ] Code coverage > 80%
- [ ] No console.log/println left
- [ ] Documentation updated
- [ ] Commits are logical
- [ ] Commit messages are clear

---

## Troubleshooting

### Build Failures

**Issue:** `Gradle sync failed`

```bash
# Solution:
./gradlew clean
rm -rf ~/.gradle/
./gradlew build
```

**Issue:** `JDK not found`

```bash
# Solution:
java -version  # Check if installed
export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # Set path
```

**Issue:** `Android SDK not found`

```bash
# Solution:
echo $ANDROID_HOME  # Check if set
ls $ANDROID_HOME/platforms/android-34  # Verify SDK
# If not found: Install via Android Studio SDK Manager
```

### Runtime Issues

**Issue:** App crashes with `SecurityException`

```
Cause: Secrets not configured
Solution: Setup local.properties (see Step 2 above)
```

**Issue:** Network requests fail

```
Cause: Using wrong Merchant ID
Solution: Verify Zarinpal ID in local.properties matches environment
```

**Issue:** Tests fail with network errors

```kotlin
// Solution: Mock network calls
@RunWith(RobolectricTestRunner::class)
class ProductsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val mockRepository: ProductsRepository = mockk(relaxed = true)
    
    @Test
    fun testGetProducts() {
        coEvery { mockRepository.getProducts() } returns listOf(mockProduct)
        // Test logic
    }
}
```

### Performance Issues

**Issue:** Build takes too long

```bash
# Solution: Increase JVM heap
echo 'org.gradle.jvmargs=-Xmx8192m' >> gradle.properties
```

**Issue:** Compose recompilation is slow

```gradle
// Solution: Enable Compose metrics
// In build.gradle.kts:
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.0"
}
```

---

## Environment Variables

### macOS/Linux

```bash
# ~/.zshrc or ~/.bashrc

export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export ANDROID_HOME=$HOME/android-sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH
export GRADLE_USER_HOME=$HOME/.gradle
export GRADLE_OPTS="-Xmx6g"
```

### Windows

```
System Properties → Environment Variables

User variables:
- JAVA_HOME=C:\Program Files\Java\openjdk-17
- ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk
- GRADLE_USER_HOME=C:\Users\YourName\.gradle

System Path:
- C:\Program Files\Java\openjdk-17\bin
- C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools
```

---

## Next Steps

1. ✅ Completed environment setup? → Try building the project
2. 🚀 First build successful? → Install and run
3. 📋 Ready to code? → Read [ARCHITECTURE.md](ARCHITECTURE.md)
4. 📚 Need help? → Check [Troubleshooting](#troubleshooting) or open an issue

---

**Last Updated:** December 28, 2025  
**Status:** ✅ Production-Ready
