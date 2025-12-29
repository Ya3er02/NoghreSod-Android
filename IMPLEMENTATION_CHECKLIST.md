# 📲 Phase 2 Implementation Checklist

**وضعیت فعلی**: ✅ تمام نقایص برطرف شد  
**تاریخ**: 29 دسامبر 2025  
**شروع اجرا**: 30 دسامبر 2025

---

## ✅ Pre-Implementation Verification

### Infrastructure Setup
- [x] CMakeLists.txt - صحیح شده
- [x] native-keys.cpp - خطاهای C++ اصلاح شدند
- [x] NativeKeyManager.kt - error handling اضافه شده
- [x] build.gradle.kts - NDK configuration complete
- [x] network_security_config.xml - certificate pinning
- [x] proguard-rules.pro - صحیح شده

### Security & Verification
- [x] PaymentVerificationCache - implemented
- [x] PaymentRateLimiter - thread-safe
- [x] Native key storage - secure
- [x] CI/CD security gates - no `|| true`

### Testing Infrastructure
- [x] HiltTestActivity - created
- [x] HiltTestRunner - configured
- [x] build.gradle.kts - test dependencies
- [x] TEST_STRUCTURE.md - documented

### Code Quality
- [x] detekt.yml - صحیح شده
- [x] Detekt rules - configured
- [x] ProGuard rules - optimized
- [x] Timber logging - initialized

---

## 📋 Phase 2 Execution Plan (30 ساعت)

### هفته 1: 30 دسامبر - 3 ژانویه (5 روز)

#### روز 1-2: NDK & Native Code (6 ساعت)
- [ ] NDK compilation test
  ```bash
  ./gradlew assembleDebug
  ```
- [ ] Native library loading verification
- [ ] Payment credentials retrieval test
- [ ] Fix any compilation errors

**Expected Output**: ✅ APK builds successfully with NDK

#### روز 3: Unit Tests - Domain Layer (8 ساعت)
- [ ] Create MoneyTest.kt
  - [ ] Test Toman/Rial type safety
  - [ ] Test conversion functions
  - [ ] Test arithmetic operations

- [ ] Create PersianNumberFormatterTest.kt
  - [ ] Test Persian digit conversion
  - [ ] Test number formatting
  - [ ] Test negative numbers

- [ ] Create PaymentRateLimiterTest.kt
  - [ ] Test rate limiting logic
  - [ ] Test concurrent access
  - [ ] Test cleanup of old attempts

**Expected Output**: ✅ 9+ unit tests passing

#### روز 4-5: Unit Tests - UseCase Layer (8 ساعت)
- [ ] Create ValidatePaymentCallbackUseCaseTest.kt
  - [ ] Test valid callback
  - [ ] Test replay attack prevention
  - [ ] Test signature validation

- [ ] Create RequestPaymentUseCaseTest.kt
  - [ ] Test payment request
  - [ ] Test rate limiting enforcement
  - [ ] Test error handling

- [ ] Create VerifyPaymentUseCaseTest.kt
  - [ ] Test verification logic
  - [ ] Test transaction persistence

**Expected Output**: ✅ 15+ unit tests passing, 70%+ coverage

---

### هفته 2: 4 ژانویه - 10 ژانویه (5 روز)

#### روز 1-2: Integration Tests (8 ساعت)
- [ ] Create PaymentFlowIntegrationTest.kt
  - [ ] End-to-end payment flow
  - [ ] Database operations
  - [ ] Network requests (mocked)

- [ ] Create DatabaseIntegrationTest.kt
  - [ ] Room database operations
  - [ ] Transaction persistence
  - [ ] Query verification

- [ ] Create NetworkIntegrationTest.kt
  - [ ] Retrofit API calls (mocked)
  - [ ] Error handling
  - [ ] Timeout handling

**Expected Output**: ✅ 10+ integration tests passing

#### روز 3: UI Tests (4 ساعت)
- [ ] Create PaymentScreenUiTest.kt
  - [ ] Button states
  - [ ] Input validation
  - [ ] Error messages
  - [ ] RTL layout

- [ ] Create CartScreenUiTest.kt
  - [ ] Item selection
  - [ ] Quantity changes
  - [ ] Total calculation

**Expected Output**: ✅ 5+ UI tests passing

#### روز 4: Coverage & Quality Gates (4 ساعت)
- [ ] Run Jacoco coverage
  ```bash
  ./gradlew jacocoTestDebugUnitTestReport
  ```
- [ ] Verify 70%+ coverage
- [ ] Run Detekt
  ```bash
  ./gradlew detekt
  ```
- [ ] Fix Detekt issues (max 10)
- [ ] Run ProGuard
  ```bash
  ./gradlew assembleRelease
  ```

**Expected Output**: 
- ✅ 70%+ code coverage
- ✅ ≤10 Detekt issues
- ✅ ProGuard compiles successfully

#### روز 5: CI/CD Verification (2 ساعت)
- [ ] Push to develop branch
- [ ] Verify GitHub Actions workflow
  - [ ] Unit tests pass
  - [ ] Coverage gates pass
  - [ ] Lint passes
  - [ ] Security checks pass
- [ ] Fix any CI/CD issues

**Expected Output**: ✅ All GitHub Actions jobs pass

---

## 📊 Quality Gate Metrics

### Code Coverage
```
✅ Unit Test Coverage:      70%+ (Target: 70%)
✅ Integration Coverage:    50%+ (Target: 50%)
✅ Overall Coverage:        65%+ (Target: 65%)
```

### Code Quality
```
✅ Lint Issues:             ≤5   (Current: TBD)
✅ Detekt Issues:           ≤10  (Current: TBD)
✅ ProGuard Rules:          ✅   (Applied)
✅ Security Vulnerabilities: 0 High-Risk
```

### Performance
```
✅ Build Time:              < 2 min
✅ Test Execution:          < 3 min
✅ APK Size:                < 15 MB
```

---

## 🚀 Testing Commands

### Run All Tests
```bash
./gradlew test
```

### Run Unit Tests Only
```bash
./gradlew testDebugUnitTest
```

### Run Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Generate Coverage Report
```bash
./gradlew jacocoTestDebugUnitTestReport
open app/build/reports/jacoco/jacocoTestDebugUnitTestReport/html/index.html
```

### Run Detekt
```bash
./gradlew detekt
open app/build/reports/detekt.html
```

### Run Lint
```bash
./gradlew lint
open app/build/reports/lint-results.html
```

### Build Release APK
```bash
./gradlew assembleRelease
```

---

## 📝 File Structure After Phase 2

```
app/src/
├── main/
│   ├── cpp/
│   │   ├── CMakeLists.txt ✅ (Fixed)
│   │   └── native-keys.cpp ✅ (Fixed)
│   ├── kotlin/com/noghre/sod/
│   │   ├── core/
│   │   │   ├── security/
│   │   │   │   ├── NativeKeyManager.kt ✅ (Fixed)
│   │   │   │   └── PaymentRateLimiter.kt ✅ (Fixed)
│   │   │   └── startup/
│   │   │       └── TimberInitializer.kt ✅ (New)
│   │   └── domain/usecase/payment/
│   │       └── PaymentVerificationCache.kt ✅ (New)
│   ├── res/xml/
│   │   └── network_security_config.xml ✅ (Fixed)
│   └── AndroidManifest.xml ✅ (Updated)
├── test/kotlin/com/noghre/sod/
│   ├── core/
│   │   ├── security/
│   │   │   ├── PaymentRateLimiterTest.kt (To Implement)
│   │   │   └── NativeKeyManagerTest.kt (To Implement)
│   │   └── util/
│   │       ├── PersianNumberFormatterTest.kt (To Implement)
│   │       └── PersianDateConverterTest.kt (To Implement)
│   └── domain/usecase/payment/
│       ├── ValidatePaymentCallbackUseCaseTest.kt (To Implement)
│       ├── RequestPaymentUseCaseTest.kt (To Implement)
│       └── VerifyPaymentUseCaseTest.kt (To Implement)
└── androidTest/kotlin/com/noghre/sod/
    ├── HiltTestActivity.kt ✅ (New)
    ├── HiltTestRunner.kt ✅ (New)
    ├── integration/
    │   ├── PaymentFlowIntegrationTest.kt (To Implement)
    │   ├── DatabaseIntegrationTest.kt (To Implement)
    │   └── NetworkIntegrationTest.kt (To Implement)
    └── ui/
        ├── PaymentScreenUiTest.kt (To Implement)
        └── CartScreenUiTest.kt (To Implement)

Root Level Files:
├── app/build.gradle.kts ✅ (Updated)
├── app/proguard-rules.pro ✅ (Fixed)
├── detekt.yml ✅ (Fixed)
├── .github/workflows/quality-gates.yml ✅ (Fixed)
├── TEST_STRUCTURE.md ✅ (New)
└── PHASE2_FIXES_SUMMARY.md ✅ (New)
```

---

## ⚠️ Known Issues & Workarounds

### Certificate Pinning Placeholders
**Issue**: `network_security_config.xml` has placeholder pins
**Action**: Replace with actual Zarinpal certificate pins before production
```bash
openssl s_client -connect api.zarinpal.com:443 -showcerts
```

### NDK Version
**Issue**: NDK 26.1 may not be installed locally
**Solution**: Install via Android Studio SDK Manager or
```bash
./gradlew -v  # Shows NDK path
```

---

## ✨ Success Criteria

- [x] All 15 critical issues fixed
- [ ] 70%+ unit test coverage
- [ ] 50%+ integration test coverage  
- [ ] All CI/CD gates passing
- [ ] Zero high-risk security vulnerabilities
- [ ] NDK compilation successful
- [ ] ProGuard optimization enabled
- [ ] Detekt ≤10 issues
- [ ] Build time < 2 minutes
- [ ] APK size < 15 MB

---

## 📞 Support Resources

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Hilt Testing](https://dagger.dev/hilt/testing)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [GitHub Actions](https://github.com/actions)
- [Jacoco Documentation](https://www.jacoco.org/)

---

**آخرین بروزرسانی**: 2025-12-29  
**نسخه**: 1.0  
**وضعیت**: ✅ آماده برای اجرا
