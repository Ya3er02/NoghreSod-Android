# 📦 Dependencies - NoghreSod

**Complete list of project dependencies, version strategy, and security advisories.**

---

## Table of Contents

1. [Core Dependencies](#core-dependencies)
2. [Version Catalog](#version-catalog)
3. [Update Strategy](#update-strategy)
4. [Security Advisories](#security-advisories)
5. [Compatibility Matrix](#compatibility-matrix)

---

## Core Dependencies

### Build System

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Gradle** | 8.0+ | Build system | ✅ Current |
| **Kotlin** | 1.9.x | Programming language | ✅ Current |
| **Android Gradle Plugin** | 8.1+ | Gradle integration | ✅ Current |

### Android Framework

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Android SDK** | 34 | Target platform | ✅ Current |
| **Jetpack Compose** | 1.5.x | UI framework | ✅ Current |
| **Jetpack Lifecycle** | 2.6.x | Lifecycle management | ✅ Current |
| **Jetpack Navigation** | 2.7.x | Navigation | ✅ Current |

### Data & Storage

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Room** | 2.5.x | Local database | ✅ Current |
| **DataStore** | 1.0.x | Preferences | ✅ Current |
| **Retrofit** | 2.9.x | HTTP client | ✅ Current |
| **OkHttp** | 4.11.x | HTTP interceptor | ✅ Current |
| **Moshi** | 1.15.x | JSON parser | ✅ Current |

### Dependency Injection

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Hilt** | 2.48 | DI framework | ✅ Current |
| **Hilt Navigation** | 2.48 | Navigation DI | ✅ Current |

### Async & Coroutines

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Coroutines** | 1.7.x | Async/await | ✅ Current |
| **Coroutines Flow** | 1.7.x | Reactive streams | ✅ Current |
| **WorkManager** | 2.8.x | Background tasks | ✅ Current |

### Image Loading

| Dependency | Version | Purpose | Status |
|------------|---------|---------|--------|
| **Coil** | 2.4.x | Image loading | ✅ Current |

### Testing

| Dependency | Version | Purpose | Scope |
|------------|---------|---------|-------|
| **JUnit 4** | 4.13.x | Testing framework | testImplementation |
| **JUnit 5** | 5.9.x | Modern testing | testImplementation |
| **MockK** | 1.13.x | Mocking library | testImplementation |
| **Turbine** | 1.0.x | Flow testing | testImplementation |
| **Coroutines Test** | 1.7.x | Coroutine testing | testImplementation |
| **Google Truth** | 1.1.x | Assertions | testImplementation |
| **Espresso** | 3.5.x | UI testing | androidTestImplementation |
| **Compose UI Test** | 1.5.x | Compose testing | androidTestImplementation |

---

## Version Catalog

**File: `gradle/libs.versions.toml`**

```toml
[versions]
kotlin = "1.9.20"
compileSdk = "34"
targetSdk = "34"
minSdk = "21"

androidGradlePlugin = "8.1.0"
compose = "1.5.4"
room = "2.5.2"
retrofit = "2.9.0"
okhttpLogging = "4.11.0"
moshi = "1.15.0"
coil = "2.4.0"
navigation = "2.7.0"
lifecycle = "2.6.1"
hilt = "2.48"
workmanager = "2.8.1"

coroutines = "1.7.1"
datastore = "1.0.0"

junit = "4.13.2"
junit5 = "5.9.2"
mockk = "1.13.5"
turbine = "1.0.0"
truth = "1.1.3"
espresso = "3.5.1"

[libraries]
# Core
kotlin-stdlib = { group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version.ref = "kotlin" }

# Compose
compose-ui = { group = "androidx.compose.ui", name = "ui", version.ref = "compose" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "compose" }

# Data
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttpLogging" }
moshi = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshi" }
moshi-codegen = { group = "com.squareup.moshi", name = "moshi-kotlin-codegen", version.ref = "moshi" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truth" }
espresso = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }

[bundles]
compose = ["compose-ui", "compose-material3"]
retrofit = ["retrofit", "okhttp-logging", "moshi"]
testing = ["junit", "mockk", "turbine", "truth"]

[plugins]
android-application = { id = "com.android.application", version.ref = "androidGradlePlugin" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

---

## Update Strategy

### Version Matrix

```
Target SDK:     34 (Latest Android)
Min SDK:        21 (Android 5.0 Lollipop)
Compile SDK:    34 (Latest)

Kotlin:         1.9.x (Stable)
Gradle:         8.0+ (Latest)
Compose:        1.5.x (Stable)
```

### Update Frequency

- **Weekly:** Check security advisories
- **Monthly:** Review minor version updates
- **Quarterly:** Evaluate major updates
- **Annually:** Plan Kotlin/Gradle major versions

### Update Process

1. **Check Advisories:** Review security issues
2. **Test Locally:** Build and run tests
3. **Update Version Catalog:** Modify `libs.versions.toml`
4. **Gradle Sync:** Let IDE resolve dependencies
5. **Run Tests:** Ensure 97+ tests still pass
6. **Commit:** Document changes

### Version Update Examples

```bash
# Update Compose
# In libs.versions.toml
compose = "1.6.0"

# Run tests
./gradlew test

# Commit
git commit -m "chore: update Compose to 1.6.0"
```

---

## Security Advisories

### Current Status: ✅ All Clear

### Known Vulnerabilities

None currently known for dependencies at specified versions.

### Monitoring

- GitHub Dependabot enabled
- Weekly security scans
- OWASP dependency check

### Response Plan

1. **Critical:** Update immediately
2. **High:** Update within 1 week
3. **Medium:** Update within 1 month
4. **Low:** Include in regular updates

---

## Compatibility Matrix

### Kotlin Version Compatibility

| Kotlin | Gradle | Compose | Room | Hilt |
|--------|--------|---------|------|------|
| 1.9.x | 8.0+ | 1.5.x | 2.5.x | 2.48 |
| 2.0.x | 8.1+ | 1.6.x | 2.6.x | 2.49+ |

### Android Version Compatibility

| Android SDK | Jetpack | Lifecycle | Navigation |
|-------------|---------|-----------|------------|
| 34 | Latest | 2.6.x | 2.7.x |
| 33 | 1.8.x | 2.5.x | 2.6.x |
| 32 | 1.7.x | 2.4.x | 2.5.x |

---

## Dependency Graph

```
App
├── Jetpack Compose
├── Room Database
├── Retrofit
├── Coroutines
├── WorkManager
├── Hilt DI
├── Coil Image Loading
└── Testing (JUnit, MockK, Turbine)
```

---

## Related Documentation

- [BUILD_AND_REBUILD_GUIDE.md](../BUILD_AND_REBUILD_GUIDE.md) - Build configuration
- [gradle.properties](../../gradle.properties) - Gradle settings
- [gradle/libs.versions.toml](../../gradle/libs.versions.toml) - Version catalog

---

**Last Updated:** December 28, 2025  
**Status:** ✅ All Dependencies Current  
**Total Dependencies:** 30+  
**Security Issues:** 0
