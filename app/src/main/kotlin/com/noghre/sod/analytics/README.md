# Analytics Module - NoghreSod

## Overview

Comprehensive analytics and crash reporting system for NoghreSod using Firebase Analytics and Crashlytics.

Now fully DI-friendly, thread-safe, and centrally configured via `AppConfig`.

## Features

✅ **Firebase Analytics** - User interaction tracking (thread-safe, IO-dispatched)
✅ **Crashlytics** - Exception and error reporting
✅ **Event Queuing** - Offline-first event handling with retry logic
✅ **Screen Tracking** - Automatic navigation analytics for Compose
✅ **User Tracking** - User journey and segmentation
✅ **E-commerce Events** - Product, cart, and purchase tracking
✅ **Error Tracking** - Comprehensive error monitoring
✅ **Custom Events** - Jewelry-specific feature tracking
✅ **Central Config** - Currency, queue sizes, and retries in `AppConfig`

## Components

### `AnalyticsManager.kt` (Main)
Core analytics engine integrating Firebase Analytics and Crashlytics.

**Key Characteristics:**
- All dependencies injected via Hilt (`FirebaseAnalytics`, `FirebaseCrashlytics`, `CoroutineDispatcher`)
- All public methods are safe to call from any thread (internally dispatched to IO)
- Currency code read from `AppConfig.Pricing.CURRENCY_CODE`
- Consent toggles exposed via `setCollectionEnabled` و `setCrashlyticsCollectionEnabled`

**Key Methods:**
- `logProductView()` - Track product views
- `logAddToCart()` - Track cart additions
- `logPurchase()` - Track completed purchases
- `logSearch()` - Track searches
- `logScreenView()` - Track screen navigation
- `recordException()` - Log exceptions to Crashlytics + Analytics
- `setUserId()` - Set user identifier (Analytics + Crashlytics)
- `setUserProperty()` - Set user segmentation properties
- `setCrashlyticsKey()` - Add custom crash report data

### `AnalyticsEvents.kt` (Constants)
Centralized event and parameter name constants for type-safety.

**Contains:**
- Screen view event names
- User action event names
- Jewelry-specific events (ring sizer, deep zoom, etc.)
- Parameter name constants
- Event category constants

### `AnalyticsModule.kt` (DI)
Hilt dependency injection configuration.

**Provides (all `@Singleton`):**
- `FirebaseAnalytics`
- `FirebaseCrashlytics`
- `CoroutineDispatcher` (IO)
- `AnalyticsManager`
- `AnalyticsRepository`
- `AnalyticsTracker`

Everything is wired in a DI-friendly, testable way – no direct static access to Firebase inside consumers.

### `AnalyticsInterceptor.kt` (UI Integration)
Compose composables and helpers for automatic screen and interaction tracking.

**Components:**
- `AnalyticsScreenTracker` - Automatic screen view logging + optional extra params
- `AnalyticsLifecycleTracker` - Lifecycle-based tracking (resume/pause)
- `AnalyticsTracker` - Common interaction tracking (buttons, forms, scroll, share, errors)
- `AnalyticsManager.logUserAction` - Extension for structured user actions

All methods have proper error handling (try/catch + Timber) and are safe to call from UI.

### `AnalyticsRepository.kt` (Data Layer)
Event queue management and persistence orchestration.

**Features:**
- Thread-safe queue using `Mutex`
- Injected `ioDispatcher` instead of hard-coded `Dispatchers.IO`
- Event queuing for offline support
- Batch processing with `BATCH_SIZE`
- Per-event retry with `MAX_RETRY_ATTEMPTS`
- `SharedFlow` for observing events (with replay)
- Dashboard summary API for debugging

Configuration comes from `AppConfig.Analytics` where appropriate.

## Usage Examples

### Log Product View
```kotlin
@Inject
lateinit var analyticsManager: AnalyticsManager

fun onProductClicked(product: Product) {
    analyticsManager.logProductView(
        productId = product.id,
        productName = product.name,
        category = product.category,
        price = product.price,
        material = product.material,
        gemType = product.gemType
    )
}
```

### Log Purchase
```kotlin
analyticsManager.logPurchase(
    orderId = "ORD-12345",
    totalPrice = 2_500_000.0,  // Uses AppConfig.Pricing.CURRENCY_CODE
    itemCount = 3,
    paymentGateway = "ZarinPal",
    shippingCost = 50_000.0,
    discountAmount = 100_000.0
)
```

### Track Screen View (Compose)
```kotlin
@Composable
fun ProductListScreen(
    analyticsManager: AnalyticsManager,
) {
    // Automatic screen tracking
    AnalyticsScreenTracker(
        screenName = "ProductList",
        screenClass = "ProductListScreen",
        analyticsManager = analyticsManager,
        additionalParams = mapOf(
            "section" to "home_recommendation"
        )
    )
    
    // Screen content...
}
```

### Track Errors
```kotlin
try {
    // Some operation
} catch (e: Exception) {
    analyticsManager.recordException(
        throwable = e,
        message = "Payment processing failed",
        fatal = false
    )
}
```

### Set User Properties
```kotlin
analyticsManager.setUserId(userId)
analyticsManager.setUserProperty("user_tier", "premium")
analyticsManager.setUserProperty("region", "Tehran")
analyticsManager.setUserProperty("language", "fa")
```

### Queue Events (Offline Support)
```kotlin
@Inject
lateinit var analyticsRepository: AnalyticsRepository

fun logOfflineEvent(productId: String) {
    analyticsRepository.queueEvent(
        AnalyticsEvent(
            eventName = AnalyticsEvents.PRODUCT_VIEW,
            bundle = Bundle().apply {
                putString("product_id", productId)
            }
        )
    )
}

// When online, process queued events:
viewModelScope.launch {
    analyticsRepository.processQueue()
}
```

### Inspect Queue State
```kotlin
viewModelScope.launch {
    val summary = analyticsRepository.getDashboardSummary()
    Timber.d("Analytics queue: $summary")
}
```

## Configuration

### Firebase Setup
Ensure Firebase is configured in `google-services.json`.

### Gradle Dependencies
```gradle
dependencies {
    // Firebase
    implementation "com.google.firebase:firebase-analytics-ktx:${firebaseVersion}"
    implementation "com.google.firebase:firebase-crashlytics-ktx:${crashlyticsVersion}"
    
    // Hilt
    implementation "com.google.dagger:hilt-android:${hiltVersion}"
    kapt "com.google.dagger:hilt-compiler:${hiltVersion}"
    
    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}"
    
    // Timber
    implementation "com.jakewharton.timber:timber:${timberVersion}"
}
```

### ProGuard Configuration
```proguard
# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase Analytics
-keep class com.google.firebase.** { *; }
```

## Best Practices

1. **Use Constants** - Always use `AnalyticsEvents` constants
2. **Include Context** - Add relevant parameters to events
3. **Track Errors** - Use `recordException()` for all errors
4. **Set User ID** - Set user identifier after login
5. **Respect Privacy** - Wire `setCollectionEnabled` به consent کاربر (DataStore)
6. **Batch Events** - Use repository for offline queuing
7. **Monitor Queue** - Use `getDashboardSummary()` در دیباگ
8. **Test Events** - Verify events in Firebase Analytics Dashboard

## Version History

### v2.0.0 (Current)
- ✅ DI-friendly `AnalyticsManager` with injected Firebase & Dispatcher
- ✅ Thread-safe `AnalyticsRepository` with Mutex & retry logic
- ✅ Centralized currency & analytics config in `AppConfig`
- ✅ Safer `AnalyticsInterceptor` with error handling & extra params support

### v1.x
- Basic Firebase Analytics & Crashlytics integration

---
**Maintained by:** NoghreSod Team  
**Last Updated:** 2026-01-29  
**Status:** ✅ Production Ready
