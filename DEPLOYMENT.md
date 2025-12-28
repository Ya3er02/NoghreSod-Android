# 🚀 Deployment Guide - NoghreSod Android

**Complete guide for building, signing, and deploying to Google Play Store and user devices.**

---

## Table of Contents

1. [Overview](#overview)
2. [Build Types & Flavors](#build-types--flavors)
3. [Signing Configuration](#signing-configuration)
4. [Building](#building)
5. [Testing Before Release](#testing-before-release)
6. [Google Play Deployment](#google-play-deployment)
7. [CI/CD Pipeline](#cicd-pipeline)
8. [Version Management](#version-management)
9. [Release Checklist](#release-checklist)

---

## Overview

### Build Pipeline

```
Source Code
    ⬇︎
./gradlew build
    ⬇︎
Compilation (Kotlin → Bytecode)
    ⬇︎
Resource Packaging
    ⬇︎
DEX Compilation (Java → Android)
    ⬇︎
APK Assembly
    ⬇︎
APK Signing
    ⬇︎
APK Optimization (R8/ProGuard)
    ⬇︎
Installation & Testing
```

### Release Strategy

```
Internal Testing (Debug)
         ⬇︎
Staging Release (Sandbox Merchant)
         ⬇︎
Pre-release Testing
         ⬇︎
Production Release (Real Merchant)
         ⬇︎
Google Play Rollout
```

---

## Build Types & Flavors

### Build Types

#### Debug
- Development builds
- Debuggable
- No ProGuard/R8 obfuscation
- Unoptimized
- Faster build time

```bash
./gradlew assembleDebug
```

#### Release
- Production builds
- Not debuggable
- ProGuard/R8 optimization
- Code obfuscation
- Slower build time, smaller APK

```bash
./gradlew assembleRelease
```

### Product Flavors

#### Dev
- Uses Zarinpal Sandbox Merchant ID
- Dev API server
- For internal development

```bash
./gradlew assembleDevDebug
./gradlew assembleDevRelease
```

#### Staging
- Uses Zarinpal Sandbox Merchant ID
- Staging API server
- For pre-release testing
- Real-like environment

```bash
./gradlew assembleStagingDebug
./gradlew assembleStagingRelease
```

#### Production
- Uses Zarinpal Production Merchant ID
- Production API server
- Real transactions

```bash
./gradlew assembleProductionDebug
./gradlew assembleProductionRelease
```

### Variant Combinations

```
Flavor × Build Type = Variant

dev          × debug     = devDebug
dev          × release   = devRelease
staging      × debug     = stagingDebug
staging      × release   = stagingRelease
production   × debug     = productionDebug
production   × release   = productionRelease
```

---

## Signing Configuration

### Generate Signing Key

#### For Production

```bash
# Generate keystore (one-time)
keytool -genkey -v -keystore noghre_sod_production.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias noghre_sod_prod

# Output: noghre_sod_production.keystore
# Store securely!
```

**Keystore Details to Remember:**
- Keystore password (keep safe!)
- Key alias: `noghre_sod_prod`
- Key password (keep safe!)
- Validity: 10000 days (~27 years)

#### For Debug (Auto-generated)

```bash
# Android creates debug keystore automatically at:
# ~/.android/debug.keystore
# Password: android
# Alias: androiddebugkey
```

### Configure Signing in Gradle

**File: `app/build.gradle.kts`**

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("${System.getProperty("user.home")}/.android/noghre_sod_production.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "noghre_sod_prod"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Store Keystore Securely

```bash
# Best practice: Don't commit keystore!
# .gitignore should have:
echo "*.keystore" >> .gitignore

# Store in:
# 1. Secure password manager
# 2. CI/CD secrets (GitHub Actions Secrets)
# 3. Hardware security key (production)
```

---

## Building

### Build Commands

```bash
# Debug builds (unsigned, auto-signed with debug key)
./gradlew assembleDebug
./gradlew installDebug  # Build + install on device

# Release builds (requires signing config)
./gradlew assembleProductionRelease
./gradlew bundleProductionRelease  # For Play Store

# Specific flavor
./gradlew assembleStagingRelease
./gradlew installStagingRelease
```

### Build Options

```bash
# Parallel build (faster)
./gradlew assembleProductionRelease --parallel

# With build cache
./gradlew assembleProductionRelease --build-cache

# Clean first
./gradlew clean assembleProductionRelease

# Verbose output
./gradlew assembleProductionRelease --info
```

### Output Location

```
app/build/outputs/
├── apk/             # APK files
│   ├── devDebug/
│   ├── stagingRelease/
│   └── productionRelease/
├── bundle/          # AAB files (for Play Store)
│   └── productionRelease/
├── mapping/         # R8 obfuscation mapping
└── lint-results/    # Lint reports
```

---

## Testing Before Release

### Pre-Release Testing Checklist

```bash
# 1. Run all tests
./gradlew test --coverage

# 2. Lint check
./gradlew lint

# 3. Build all variants
./gradlew assembleDebug
./gradlew assembleStagingRelease
./gradlew assembleProductionDebug  # For manual testing

# 4. Install on device
./gradlew installStagingRelease

# 5. Test payment flow (sandbox)
# Use Zarinpal sandbox credentials

# 6. Check app signing
jarsigner -verify -verbose app/build/outputs/apk/productionRelease/app-production-release.apk
```

### Manual Testing on Staging

1. **Install staging build:**
   ```bash
   ./gradlew installStagingRelease
   ```

2. **Test features:**
   - Product browsing
   - Filtering and search
   - Add to cart
   - Checkout flow
   - **Payment (uses Zarinpal sandbox)**
   - Offline functionality
   - Network reconnection

3. **Test payment with sandbox:**
   - Use test card numbers
   - Verify transaction logs
   - Check database sync

---

## Google Play Deployment

### Prerequisites

- [Google Play Developer Account](https://play.google.com/console) (€25 one-time)
- App signed with release keystore
- App Store Listing prepared
- Privacy Policy and Terms of Service
- Screenshots and preview images
- Rating content questionnaire completed

### Prepare App Bundle

```bash
# Create AAB (App Bundle) for Play Store
./gradlew bundleProductionRelease

# Output: app/build/outputs/bundle/productionRelease/app-production-release.aab

# This is ~5-10 MB (smaller than APK)
# Play Store optimizes per device
```

### Upload to Play Console

1. **Go to [Google Play Console](https://play.google.com/console)**
2. **Select app (or create if new)**
3. **Left sidebar → Release → Production**
4. **Create new release:**
   - Click "Create new release"
   - Upload AAB file
   - Add release notes
   - Set rollout percentage (e.g., 10% → 25% → 100%)
5. **Review and publish**

### Play Store Rollout Strategy

```
Day 1: 10% rollout
  Monitor crash reports and ratings
  ⬇︎ (24 hours)

Day 2: 25% rollout
  Check metrics and user feedback
  ⬇︎ (24 hours)
  
Day 3: 100% rollout
  Full release to all users
```

---

## CI/CD Pipeline

### GitHub Actions Workflow

**File: `.github/workflows/build-and-deploy.yml`**

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]
    tags: ['v*']

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build
        env:
          ZARINPAL_MERCHANT_ID: ${{ secrets.PROD_MERCHANT_ID }}
          ZARINPAL_SANDBOX_MERCHANT_ID: ${{ secrets.SANDBOX_MERCHANT_ID }}
        run: |
          echo "zarinpal.merchant.id=$ZARINPAL_MERCHANT_ID" >> local.properties
          echo "zarinpal.sandbox.merchant.id=$ZARINPAL_SANDBOX_MERCHANT_ID" >> local.properties
          ./gradlew bundleProductionRelease
      
      - name: Deploy to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_CONSOLE_JSON }}
          packageName: com.noghre.sod
          releaseFile: app/build/outputs/bundle/productionRelease/app-production-release.aab
          track: internal
          status: inProgress
          rolloutPercentage: 10
```

### Setup GitHub Secrets

```
Repository Settings → Secrets and variables → Actions

Add:
- PROD_MERCHANT_ID: Your production Zarinpal ID
- SANDBOX_MERCHANT_ID: Your sandbox Zarinpal ID
- PLAY_CONSOLE_JSON: Google Play Console service account JSON
- KEYSTORE_PASSWORD: Keystore password
- KEY_PASSWORD: Key password
```

---

## Version Management

### Semantic Versioning

**Format:** `MAJOR.MINOR.PATCH`

Examples:
- `1.0.0` - First release
- `1.1.0` - New features (minor bump)
- `1.1.1` - Bug fix (patch bump)
- `2.0.0` - Breaking changes (major bump)

### Update Version

**File: `app/build.gradle.kts`**

```kotlin
android {
    defaultConfig {
        versionCode = 1        // Increment for each release
        versionName = "1.0.0"  // Semantic version
    }
}

// Version code calculation
// versionCode = majorVersion * 10000 + minorVersion * 100 + patchVersion
// Example: v1.2.3 = 10203
```

### Version Timeline

```
v0.1.0 - Initial Alpha
  ⬇︎ 1 month
v0.5.0 - Beta with major features
  ⬇︎ 2 weeks
v1.0.0 - First Production Release
  ⬇︎ 1 month
v1.1.0 - New features, bug fixes
  ⬇︎ 1 week
v1.1.1 - Critical bug fix
  ⬇︎ 2 weeks
v2.0.0 - Major redesign/rewrite
```

---

## Release Checklist

### Before Deployment

- [ ] All tests passing (`./gradlew test`)
- [ ] Code review completed
- [ ] Version updated in `build.gradle.kts`
- [ ] CHANGELOG.md updated
- [ ] Release notes prepared
- [ ] Screenshots/preview updated (if UI changed)
- [ ] Privacy Policy updated (if needed)
- [ ] All branches merged to `main`
- [ ] No TODO comments in code
- [ ] No debug logging
- [ ] ProGuard rules tested
- [ ] Keystore password available

### Build Staging Release

- [ ] Run `./gradlew clean`
- [ ] Run `./gradlew bundleStagingRelease`
- [ ] Install staging APK on device
- [ ] Test all critical flows
- [ ] Test payment with sandbox
- [ ] Check logs for crashes
- [ ] Test offline functionality
- [ ] Verify app signing

### Build Production Release

- [ ] Verify merchant ID in `local.properties`
- [ ] Run `./gradlew clean`
- [ ] Run `./gradlew bundleProductionRelease`
- [ ] Verify signing configuration
- [ ] Check APK size and performance
- [ ] Verify manifest (target SDK, permissions)
- [ ] Test with production payment credentials (if possible)

### Upload to Play Store

- [ ] Create release in Play Console
- [ ] Upload AAB file
- [ ] Add release notes
- [ ] Set rollout percentage (10%)
- [ ] Review all information
- [ ] Submit for review
- [ ] Monitor crash reports (next 24h)
- [ ] Increase rollout to 25% (if stable)
- [ ] Final rollout to 100%

### Post-Release

- [ ] Monitor crash reports
- [ ] Monitor ratings and reviews
- [ ] Check Play Console analytics
- [ ] Monitor payment transactions
- [ ] Tag release in Git
- [ ] Archive release notes
- [ ] Notify team of deployment

---

## Troubleshooting

### Build Failures

**Issue:** `Signing config is missing the required key fields`

```bash
# Solution: Ensure signing config in build.gradle.kts
# Or set environment variables:
export KEYSTORE_PASSWORD="your_password"
export KEY_PASSWORD="your_password"
./gradlew assembleProductionRelease
```

**Issue:** `R8 rule violation`

```bash
# Solution: Update proguard-rules.pro
# Add -dontwarn for problematic classes
```

### Play Store Issues

**Issue:** `App not compatible with any device`

- Check `compileSdk` is 34+
- Check `targetSdk` is 34+
- Verify minSdk is reasonable (API 21+)

**Issue:** `Certificate not valid for signing`

- Verify certificate path in signing config
- Check certificate hasn't expired
- Verify password is correct

---

## Quick Commands Reference

```bash
# Development
./gradlew installDebug              # Build & install dev debug

# Staging
./gradlew bundleStagingRelease      # Create staging AAB
./gradlew installStagingRelease     # Install staging release

# Production
./gradlew bundleProductionRelease   # Create production AAB
./gradlew assembleProductionRelease # Create production APK

# Testing
./gradlew test                      # Run unit tests
./gradlew lint                      # Run lint checks

# Cleanup
./gradlew clean                     # Clean build files
```

---

**Last Updated:** December 28, 2025  
**Status:** ✅ Production-Ready
