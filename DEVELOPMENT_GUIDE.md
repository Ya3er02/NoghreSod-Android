# 💴 نقره‌سود (NoghreSod) - دستنامه توسعه

## 💫 فهرست‌مطالب
- [معماری پروژه](#معماری-پروژه)
- [پیش‌نیازها](#پیش‌نیازها)
- [نصب و تنظیم](#نصب-و-تنظیم)
- [ساختار پروژه](#ساختار-پروژه)
- [دستورات ساخت](#دستورات-ساخت)
- [تست‌ها](#تست‌ها)
- [راهنمای مشارکت](#راهنمای-مشارکت)
- [بهترین روش‌ها](#بهترین-روش‌ها)

---

## 💫 معماری پروژه

### Clean Architecture + MVVM

```
app/
├── core/              # Core modules
│   ├── di/           # Dependency Injection
│   ├── security/     # Security & encryption
│   ├── network/      # Network configuration
│   └── image/        # Image loading
├── data/              # Data layer
│   ├── local/        # Room database
│   ├── remote/       # API services
│   ├── dto/          # Data transfer objects
│   └── repository/   # Repository implementations
├── domain/            # Domain layer
│   ├── model/        # Domain models
│   ├── repository/   # Repository interfaces
│   └── usecase/      # Business logic
└── presentation/      # Presentation layer
    ├── home/         # Home screen
    ├── product/      # Product details
    ├── cart/         # Shopping cart
    ├── checkout/     # Checkout flow
    ├── common/       # Common components
    └── theme/        # Theme & styling
```

### لایه‌های معماری

#### 🎨 Presentation Layer
- Jetpack Compose UI
- ViewModels with Hilt
- State management (Flow, StateFlow)
- Navigation with Compose

#### 🏢 Domain Layer
- Use Cases (Business Logic)
- Domain Models (Pure Kotlin)
- Repository Interfaces
- Entities and Value Objects

#### 💾 Data Layer
- Repository Implementations
- API Services (Retrofit)
- Local Database (Room)
- DTOs and Mappers

---

## 🛠️ پیش‌نیازها

### نرم‌افزار مورد نیاز
- **Android Studio**: Hedgehog | 2023.1.1 یا بالاتر
- **JDK**: 17
- **Android SDK**: API 34
- **Gradle**: 8.0+

### تنظیمات کامپیوتر
```bash
# macOS
brew install java@17
brew cask install android-studio

# Ubuntu
sudo apt-get install openjdk-17-jdk-headless

# Windows
# دانلود از: https://www.oracle.com/java/technologies/javase-jdk17-downloads.html
```

---

## 🔓 نصب و تنظیم

### 1️⃣ Clone Repository
```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2️⃣ تنظیم متغیرهای محیط

فایل `local.properties` در ریشه پروژه ایجاد کنید:

```properties
sdk.dir=/Users/yourname/Library/Android/sdk

# برای ساخت Release
RELEASE_KEYSTORE_PATH=/path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password

# API Configuration
API_BASE_URL=https://api.noghresod.ir/v1/
DEBUG=true
```

### 3️⃣ تنظیم Gradle
```bash
# Download dependencies
./gradlew clean build

# Optional: Update gradle wrapper
./gradlew wrapper --gradle-version=8.0
```

### 4️⃣ اجرای پروژه
```bash
# Debug build
./gradlew assembleDebug

# اجرا روی Emulator/Device
./gradlew installDebug
adb shell am start -n com.noghre.sod/.MainActivity
```

---

## 📁 ساختار پروژه

### Key Files
- `build.gradle.kts`: gradle configuration
- `settings.gradle.kts`: root settings
- `gradle/libs.versions.toml`: dependency management
- `proguard-rules.pro`: obfuscation rules
- `.github/workflows/android-ci.yml`: CI/CD pipeline

### Resource Structure
```
src/main/
├── kotlin/
│   └── com/noghre/sod/
├── res/
│   ├── drawable/
│   ├── layout/
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── mipmap/
└── AndroidManifest.xml
```

---

## 📈 دستورات ساخت

### Build Commands
```bash
# Clean build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# App Bundle (Google Play)
./gradlew bundleRelease

# Install on device
./gradlew installDebug

# Run on connected device
./gradlew runDebug

# Build with specific flavor
./gradlew assembleDevDebug  # Development
./gradlew assembleProdRelease # Production
```

### Useful Gradle Tasks
```bash
# List all tasks
./gradlew tasks

# Check dependencies
./gradlew dependencyReport

# Lint check
./gradlew lint

# Code coverage
./gradlew jacocoTestReport

# Performance profiling
./gradlew connectedCheck
```

---

## ✅ تست‌ها

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests="*ProductRepositoryTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Instrumented Tests (Android)
```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest

# Run specific test
./gradlew connectedAndroidTest --tests="*HomeScreenTest"
```

### Test Structure
```
app/src/
├── test/kotlin/          # Unit tests
│   └── com/noghre/sod/
│       ├── domain/
│       ├── data/
│       └── presentation/
└── androidTest/kotlin/   # Instrumented tests
    └── com/noghre/sod/
        └── ui/
```

---

## 🌟 بهترین روش‌ها

### Code Style
1. **Google Kotlin Style Guide** اتباع کنید
2. 4 spaces برای indentation
3. Descriptive variable names
4. Maximum line length: 100 characters

### Documentation
- **KDoc** استفاده کنید برای public APIs
- Code comments برای complex logic
- README به‌روز نگه دارید

### Security
- ✅ Use ProGuard rules
- ✅ Never hardcode secrets
- ✅ Certificate pinning enabled
- ✅ Root detection implemented
- ✅ Validate user input

### Performance
- ✅ Image caching configured
- ✅ Lazy loading for lists
- ✅ Database indexes optimized
- ✅ Network timeouts set
- ✅ Memory leaks prevented

### Testing
- ✅ Unit test coverage > 80%
- ✅ Integration tests for repositories
- ✅ UI tests for critical flows
- ✅ Mock external dependencies

---

## 🚀 راهنمای مشارکت

### قبل از شروع
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Follow code style guidelines
4. Add tests for new features
5. Update documentation

### Commit Convention
```bash
# Format: <type>(<scope>): <subject>
# Example:
git commit -m "feat(products): add product search functionality"
git commit -m "fix(cart): resolve item removal bug"
git commit -m "docs(readme): update setup instructions"
```

Types: feat, fix, docs, style, refactor, perf, test, ci, chore

### Pull Request Process
1. Update branch: `git pull origin main`
2. Create descriptive PR title
3. Link related issues
4. Ensure CI/CD passes
5. Request review from team

---

## 📄 نکات مهم

### لاگ‌کردن
```kotlin
import android.util.Log

Log.d("ProductTag", "Product loaded: $product")
Log.e("ProductTag", "Error loading product", exception)
```

### Debugging
```bash
# Enable debug logs
adb shell setprop log.tag.NoghreSod DEBUG

# View logs
adb logcat | grep NoghreSod

# Profile app
./gradlew :app:profileDebug
```

### Remote Configuration
- API endpoints configurable
- Feature flags supported
- A/B testing ready

---

## 🔘 تماس و پشتیبانی

- **Issues**: https://github.com/Ya3er02/NoghreSod-Android/issues
- **Discussions**: https://github.com/Ya3er02/NoghreSod-Android/discussions
- **Wiki**: https://github.com/Ya3er02/NoghreSod-Android/wiki

---

**آخرین بروزرسانی**: 2025-12-25
