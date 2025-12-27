# 📦 Dependency Management Guide

## Overview

This document tracks all critical dependencies and their update status for NoghreSod Android project.

**Current Status:** ⚠️ **NEEDS UPDATES**

---

## 🔴 Critical Updates Required (Priority 1)

### Android Core Libraries

| Library | Current | Latest | Status | Security |
|---------|---------|--------|--------|----------|
| `androidx.core:core-ktx` | 1.12.0 | 1.15.0 | ⚠️ Outdated | Check |
| `androidx.navigation:navigation-compose` | 2.7.7 | 2.8.5 | ⚠️ Outdated | Check |
| `androidx.lifecycle:lifecycle-runtime-ktx` | TBD | Latest | TBD | TBD |
| `androidx.activity:activity-compose` | TBD | Latest | TBD | TBD |

### Firebase & Google Services

| Library | Current | Latest | Status | Notes |
|---------|---------|--------|--------|-------|
| Firebase BOM | 32.7.0 | 33.7.0 | ⚠️ Outdated | Includes all Firebase libs |
| `com.google.android.gms:play-services-auth` | TBD | Latest | TBD | Authentication |

### Networking

| Library | Current | Latest | Status |
|---------|---------|--------|--------|
| `com.squareup.retrofit2:retrofit` | 2.10.0 | 2.11.0 | ⚠️ Outdated |
| `com.squareup.okhttp3:okhttp` | TBD | Latest | TBD |
| `com.squareup.okhttp3:logging-interceptor` | TBD | Latest | TBD |

### Database

| Library | Current | Latest | Status |
|---------|---------|--------|--------|
| `androidx.room:room-runtime` | 2.6.1 | 2.6.1 | ✅ Latest |
| `androidx.room:room-ktx` | 2.6.1 | 2.6.1 | ✅ Latest |

### Security

| Library | Current | Latest | Status | Purpose |
|---------|---------|--------|--------|----------|
| `androidx.security:security-crypto` | TBD | Latest | TBD | Encrypted SharedPreferences |
| `com.jrummyapps:rootbeer-lib` | TBD | Latest | TBD | Root detection |

---

## 🟡 High Priority Updates (Priority 2)

- [ ] Kotlin version (check compatibility)
- [ ] Gradle version
- [ ] Jetpack Compose version
- [ ] DataStore versions

---

## 🟢 Medium Priority (Priority 3)

- [ ] Testing libraries (JUnit, MockK, Turbine)
- [ ] Image loading libraries (Coil)
- [ ] Analytics libraries

---

## 📋 Update Instructions

### Step 1: Check for Updates

```bash
# View all updates available
./gradlew dependencyUpdates
```

### Step 2: Update build.gradle.kts

Edit `app/build.gradle.kts` and update versions:

```kotlin
// OLD
implementation("androidx.core:core-ktx:1.12.0")

// NEW
implementation("androidx.core:core-ktx:1.15.0")
```

### Step 3: Test Build

```bash
./gradlew clean
./gradlew build
```

### Step 4: Run Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 🔒 Security Considerations

### Known Vulnerabilities

Before updating, check:

```bash
# Check for known vulnerabilities
./gradlew dependencyCheckAnalyze
```

### Recommended Security Updates

✅ **Always update:**
- Firebase BOM (security patches)
- OkHttp (TLS updates)
- Retrofit (security improvements)
- AndroidX libraries

⚠️ **Test thoroughly after updating:**
- Room migrations
- Hilt configuration
- Compose version compatibility

---

## 📅 Update Schedule

- **Monthly:** Check for critical security updates
- **Quarterly:** Full dependency audit
- **Before Release:** Update all dependencies to latest

---

## 🐛 Troubleshooting Updates

### Build Fails After Update

1. Check Kotlin version compatibility
2. Verify Gradle compatibility
3. Run `./gradlew clean`
4. Check for deprecation warnings
5. Review changelog of updated library

### Conflicts Between Libraries

```kotlin
// Force specific version if conflict
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0") {
    because("Compatibility with Compose")
}
```

---

## 📚 Resources

- [Android Developers - Libraries](https://developer.android.com/jetpack/androidx/versions)
- [Firebase Release Notes](https://firebase.google.com/docs/android/release-notes)
- [Gradle Plugin Releases](https://developer.android.com/build/releases)
- [Kotlin Releases](https://kotlinlang.org/docs/releases.html)

---

**Last Updated:** December 27, 2025
**Next Review:** January 27, 2026