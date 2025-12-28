# 🔐 Secrets & Configuration Setup Guide

## Table of Contents
1. [Overview](#overview)
2. [Setting Up Payment Gateway Credentials](#setting-up-payment-gateway-credentials)
3. [Local Properties Configuration](#local-properties-configuration)
4. [Build Variants & Environment Management](#build-variants--environment-management)
5. [Security Best Practices](#security-best-practices)
6. [Troubleshooting](#troubleshooting)

---

## Overview

NoghreSod uses **gradle-based BuildConfig** to inject secrets at compile time. This approach:
- ✅ Keeps secrets out of version control
- ✅ Supports environment-specific configurations (dev, staging, production)
- ✅ Prevents accidental credential exposure
- ✅ Enables local development without sharing sensitive data

### Current Integrations
- **Zarinpal** - Primary payment gateway
- **Firebase** - Analytics, messaging, storage (future)
- **NextPay** - Secondary payment option (future)
- **Bazaar Pay** - In-app payment (future)

---

## Setting Up Payment Gateway Credentials

### 1️⃣ Zarinpal Payment Gateway Setup

#### Prerequisite: Zarinpal Account
1. Register at [Zarinpal.com](https://zarinpal.com)
2. Complete KYC (Know Your Customer) verification
3. Access panel at [https://panel.zarinpal.com](https://panel.zarinpal.com)

#### Get Your Merchant IDs

**For Production:**
```
Panel → Settings → Integration
Copy: Merchant ID (for live transactions)
```

**For Sandbox Testing:**
```
Panel → Sandbox Environment
Copy: Sandbox Merchant ID (for testing)
Example: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
```

⚠️ **Important Notes:**
- Zarinpal provides **both UUID and numeric IDs** - use the UUID format
- Sandbox ID differs from production ID
- Keep these credentials absolutely secure

---

## Local Properties Configuration

### Step 1: Create `local.properties`

In your project root directory, create `local.properties` (same level as `settings.gradle.kts`):

```bash
cd /path/to/NoghreSod-Android
cp local.properties.example local.properties
```

### Step 2: Configure Zarinpal Credentials

Edit `local.properties` and add your credentials:

```properties
# ============================================
# Zarinpal Payment Gateway Credentials
# ============================================
# Get your credentials from: https://panel.zarinpal.com

# Production Merchant ID (for live transactions)
zarinpal.merchant.id=YOUR_PRODUCTION_MERCHANT_ID

# Sandbox Merchant ID (for testing)
zarinpal.sandbox.merchant.id=YOUR_SANDBOX_MERCHANT_ID

# Optional: API Key for additional operations
zarinpal.api.key=YOUR_API_KEY_IF_NEEDED

# ============================================
# Firebase Configuration (Optional)
# ============================================
firebase.project.id=your-project-id

# ============================================
# Keystore Configuration (for signed APK)
# ============================================
keystore.file=/path/to/your/keystore.jks
keystore.password=YOUR_KEYSTORE_PASSWORD
keystore.alias=YOUR_KEY_ALIAS
keystore.key.password=YOUR_KEY_PASSWORD
```

### Step 3: Add to `.gitignore`

**CRITICAL**: Ensure `local.properties` is in `.gitignore`:

```bash
# Verify it's ignored
git check-ignore local.properties
# Should output: local.properties

# If not, add it:
echo "local.properties" >> .gitignore
git add .gitignore
git commit -m "chore: Ensure local.properties is git-ignored"
```

---

## Build Variants & Environment Management

### Understanding Product Flavors

NoghreSod has **3 product flavors** for environment management:

| Flavor | Purpose | Merchant ID Used | API URL |
|--------|---------|------------------|----------|
| `dev` | Local development | Sandbox ID | Dev server |
| `staging` | Pre-production testing | Sandbox ID | Staging server |
| `production` | Live deployment | Production ID | Production server |

### Building for Each Environment

**Development (Default):**
```bash
# Uses sandbox credentials from local.properties
gradle assembleDebug
```

**Staging:**
```bash
# Uses sandbox credentials, staging API
gradle assembleStagingRelease
```

**Production:**
```bash
# Uses production credentials, live API
gradle assembleProductionRelease
```

### Automatic Merchant ID Selection

The build system automatically selects the correct merchant ID:

```kotlin
// From build.gradle.variants.kts

productFlavors {
    create("dev") {
        // Uses SANDBOX merchant ID
        buildConfigField("String", "ZARINPAL_MERCHANT_ID", 
            "\"${zarinpalSandboxId}\"")
    }
    
    create("production") {
        // Uses PRODUCTION merchant ID
        buildConfigField("String", "ZARINPAL_MERCHANT_ID", 
            "\"${zarinpalProdId}\"")
    }
}
```

---

## Security Best Practices

### ✅ Do's

1. **Use Different Credentials per Environment**
   ```properties
   # Always keep separate IDs
   zarinpal.merchant.id=PRODUCTION_ID        # Live transactions
   zarinpal.sandbox.merchant.id=SANDBOX_ID   # Testing only
   ```

2. **Store `local.properties` Securely**
   - Don't share via email or chat
   - If compromised, immediately regenerate credentials
   - Use `.gitignore` to prevent accidental commits

3. **Rotate Credentials Regularly**
   - Change API keys quarterly
   - Immediately update if leaked or exposed

4. **Use Environment Variables in CI/CD**
   ```bash
   # GitHub Actions example
   export ZARINPAL_MERCHANT_ID=${{ secrets.ZARINPAL_PROD_ID }}
   ```

5. **Validate Credentials Before Building**
   ```bash
   # Add to build.gradle.kts
   task validateSecrets {
       doFirst {
           if (MERCHANT_ID.isEmpty()) {
               throw GradleException("Zarinpal merchant ID not configured!")
           }
       }
   }
   ```

### ❌ Don'ts

1. **Never commit `local.properties`** to git
2. **Never hardcode credentials** in code
3. **Never share production IDs** in public repositories
4. **Never use same ID** for dev and production
5. **Never push `.apk` files** with sensitive data

---

## Runtime Access

### Accessing Merchant ID at Runtime

```kotlin
import com.noghre.sod.BuildConfig

// In ZarinpalPaymentService.kt
private val MERCHANT_ID = BuildConfig.ZARINPAL_MERCHANT_ID

// Automatic environment detection
private val USE_SANDBOX = BuildConfig.DEBUG  // Debug builds use sandbox

// Usage
suspend fun requestPayment(request: PaymentRequest) {
    // MERCHANT_ID is automatically set based on build variant
    val dto = ZarinpalPaymentRequestDto(
        merchantId = MERCHANT_ID,  // ✓ Injected from BuildConfig
        amount = request.amount * 10,
        callbackUrl = request.callbackUrl
    )
    // ... rest of implementation
}
```

---

## Troubleshooting

### Issue 1: "YOUR_PRODUCTION_MERCHANT_ID" in BuildConfig

**Problem:** You see placeholder values in error logs

**Solution:**
```bash
# Check if local.properties exists and is readable
ls -la local.properties
cat local.properties | grep zarinpal

# Rebuild with fresh Gradle cache
./gradlew clean
./gradlew assembleDebug
```

### Issue 2: Different Merchant IDs Across Builds

**Problem:** Dev build uses production ID unexpectedly

**Solution:**
```bash
# Verify which flavor is being built
./gradlew assemble --dry-run

# Explicitly specify flavor
./gradlew assembleDevDebug  # Forces dev flavor
```

### Issue 3: Payment Fails with "Invalid Merchant"

**Checklist:**
- [ ] Correct merchant ID in `local.properties`
- [ ] Sandbox ID used for testing, Production ID for live
- [ ] Zarinpal account has proper permissions
- [ ] Callback URL is whitelisted in Zarinpal panel
- [ ] Amount is in Toman (will be converted to Rial)

### Issue 4: BuildConfig Not Recognizing New Credentials

**Solution:**
```bash
# Gradle cache issue - clear it
./gradlew clean

# Invalidate and restart Android Studio
# File → Invalidate Caches → Invalidate and Restart

# Then rebuild
./gradlew assembleDebug --no-build-cache
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build Signed APK

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Create local.properties
        run: |
          echo "zarinpal.merchant.id=${{ secrets.ZARINPAL_PROD_ID }}" > local.properties
          echo "zarinpal.sandbox.merchant.id=${{ secrets.ZARINPAL_SANDBOX_ID }}" >> local.properties
          echo "keystore.file=/tmp/keystore.jks" >> local.properties
          echo "keystore.password=${{ secrets.KEYSTORE_PASSWORD }}" >> local.properties
      
      - name: Build Release APK
        run: ./gradlew assembleProductionRelease
      
      - name: Upload to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJson: ${{ secrets.PLAY_SERVICE_ACCOUNT }}
          packageName: com.noghre.sod
          releaseFiles: app/build/outputs/apk/production/release/app-production-release.apk
```

### Required GitHub Secrets

Configure these in your GitHub repository settings:

```
ZARINPAL_PROD_ID          → Your production merchant ID
ZARINPAL_SANDBOX_ID       → Your sandbox merchant ID
KEYSTORE_PASSWORD         → Your keystore password
KEYSTORE_KEY_PASSWORD     → Your key password
PLAY_SERVICE_ACCOUNT      → Google Play service account JSON
```

---

## Verification Checklist

Before deploying to production:

- [ ] `local.properties` is in `.gitignore`
- [ ] Production merchant ID is correct and active
- [ ] Sandbox ID works for testing
- [ ] Credentials are rotated recently
- [ ] Callback URL is configured in Zarinpal panel
- [ ] Error handling includes invalid merchant ID scenario
- [ ] Logging doesn't expose merchant IDs
- [ ] CI/CD secrets are configured
- [ ] Team members have setup guide
- [ ] Backup credentials are stored securely

---

## Support

For issues with:
- **Zarinpal Integration:** See [Zarinpal API Docs](https://zarinpal.com/doc/api)
- **Gradle BuildConfig:** Check [Android Gradle Plugin docs](https://developer.android.com/build)
- **NoghreSod Setup:** Create an issue on GitHub

---

**Last Updated:** 2025-12-28 | **Version:** 1.0
