# Analytics Module - NoghreSod

## Overview

Comprehensive analytics and crash reporting system for NoghreSod using Firebase Analytics and Crashlytics.

## Features

✅ **Firebase Analytics** - User interaction tracking
✅ **Crashlytics** - Exception and error reporting
✅ **Event Queuing** - Offline-first event handling
✅ **Screen Tracking** - Automatic navigation analytics
✅ **User Tracking** - User journey and segmentation
✅ **E-commerce Events** - Product, cart, and purchase tracking
✅ **Error Tracking** - Comprehensive error monitoring
✅ **Custom Events** - Jewelry-specific feature tracking

## Components

### `AnalyticsManager.kt` (Main)
Core analytics engine integrating Firebase Analytics and Crashlytics.

**Key Methods:**
- `logProductView()` - Track product views
- `logAddToCart()` - Track cart additions
- `logPurchase()` - Track completed purchases
- `logSearch()` - Track searches
- `logScreenView()` - Track screen navigation
- `recordException()` - Log exceptions to Crashlytics
- `setUserId()` - Set user identifier
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

**Provides:**
- `AnalyticsManager` singleton
- `AnalyticsRepository` singleton

### `AnalyticsInterceptor.kt` (UI Integration)
Compose composables for automatic screen tracking.

**Components:**
- `AnalyticsScreenTracker` - Automatic screen view logging
- `AnalyticsLifecycleTracker` - Lifecycle-based tracking
- `AnalyticsTracker` - Common interaction tracking

### `AnalyticsRepository.kt` (Data Layer)
Event queue management and persistence.

**Features:**
- Event queuing for offline support
- Batch processing
- Event history tracking
- Dashboard summary

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
    totalPrice = 2500000.0,  // In IRR
    itemCount = 3,
    paymentGateway = "ZarinPal",
    shippingCost = 50000.0,
    discountAmount = 100000.0
)
```

### Track Screen View
```kotlin
@Composable
fun ProductListScreen(
    analyticsManager: AnalyticsManager = hiltViewModel()
) {
    // Automatic screen tracking
    AnalyticsScreenTracker(
        screenName = "ProductList",
        screenClass = "ProductListScreen",
        analyticsManager = analyticsManager
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
analyticsRepository.processQueue()
```

## Event Tracking Guide

### Product Events
- `PRODUCT_VIEW` - User views product details
- `PRODUCT_ADD_TO_CART` - User adds product to cart
- `PRODUCT_REMOVE_FROM_CART` - User removes from cart
- `PRODUCT_SEARCH` - User performs search
- `PRODUCT_FILTER` - User applies filters
- `PRODUCT_WISHLIST_ADD` - User adds to wishlist

### Jewelry-Specific Events
- `RING_SIZER_OPENED` - User opens virtual ring sizer
- `RING_SIZER_MEASURED` - User completes ring measurement
- `PRODUCT_ZOOM_USED` - User uses deep zoom on product
- `HALLMARK_VIEWED` - User views silver hallmark (925)
- `GEM_INSPECTION` - User inspects gem details

### E-commerce Events
- `CHECKOUT_START` - User initiates checkout
- `CHECKOUT_COMPLETE` - Checkout completed
- `DISCOUNT_APPLIED` - Discount/coupon applied
- `PAYMENT_METHOD_SELECT` - User selects payment method

### Authentication Events
- `LOGIN` - User logs in
- `SIGNUP` - New user registration
- `LOGOUT` - User logs out
- `OTP_VERIFIED` - OTP verification successful

### Error Events
- `ERROR_NETWORK` - Network error occurred
- `ERROR_PAYMENT` - Payment processing error
- `ERROR_API` - API call error
- `APP_CRASH` - Application crash

## Configuration

### Firebase Setup
Ensure Firebase is configured in `google-services.json`:
```json
{
  "type": "service_account",
  "project_id": "noghresod-analytics",
  // ... rest of Firebase config
}
```

### Gradle Dependencies
```gradle
dependencies {
    // Firebase
    implementation "com.google.firebase:firebase-analytics-ktx:${firebaseVersion}"
    implementation "com.google.firebase:firebase-crashlytics-ktx:${crashlyticsVersion}"
    
    // Hilt
    implementation "com.google.dagger:hilt-android:${hiltVersion}"
    kapt "com.google.dagger:hilt-compiler:${hiltVersion}"
    
    // Timber
    implementation "com.jakewharton.timber:timber:${timberVersion}"
}
```

### ProGuard Configuration
Add to `proguard-rules.pro`:
```proguard
# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase Analytics
-keep class com.google.firebase.** { *; }
```

## Testing

### Unit Tests
```kotlin
class AnalyticsManagerTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var analyticsManager: AnalyticsManager
    
    @Before
    fun setup() {
        analyticsManager = AnalyticsManager()
    }
    
    @Test
    fun testProductView() {
        analyticsManager.logProductView(
            productId = "123",
            productName = "Silver Ring",
            category = "Rings",
            price = 500000.0
        )
        // Verify event was logged
    }
}
```

### Firebase Test Lab
Run analytics tests on Firebase Test Lab:
```bash
gcloud firebase test android run \
  --app app-debug.apk \
  --test app-debug-androidTest.apk
```

## Best Practices

1. **Use Constants** - Always use `AnalyticsEvents` constants
2. **Include Context** - Add relevant parameters to events
3. **Track Errors** - Use `recordException()` for all errors
4. **Set User ID** - Set user identifier after login
5. **Respect Privacy** - Allow users to opt-out of analytics
6. **Batch Events** - Use repository for offline queuing
7. **Monitor Queue** - Check queue size in development
8. **Test Events** - Verify events in Firebase Analytics Dashboard

## Debugging

### Enable Debug Logging
```kotlin
// In MainActivity or Application class
if (BuildConfig.DEBUG) {
    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
}
```

### Monitor Event Queue
```kotlin
@Inject
lateinit var analyticsRepository: AnalyticsRepository

fun checkQueueStatus() {
    val summary = analyticsRepository.getDashboardSummary()
    Timber.d("Analytics queue: $summary")
}
```

### Real-time Events in Firebase Console
1. Open [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to Analytics > Real-time
4. Watch events as they happen

## Troubleshooting

### Events Not Appearing
- ✅ Verify Firebase initialization
- ✅ Check internet connectivity
- ✅ Verify event names match Firebase schema
- ✅ Allow 24-48 hours for historical data processing

### Crashes Not Reported
- ✅ Verify Crashlytics is initialized
- ✅ Check ProGuard rules
- ✅ Ensure internet access
- ✅ Verify Firebase project configuration

### High Queue Size
- ✅ Process queue when network available
- ✅ Reduce event frequency in dev builds
- ✅ Check for network issues

## Future Enhancements

- [ ] Custom dimensions support
- [ ] A/B testing integration
- [ ] Remote Config analytics
- [ ] Real-time dashboard
- [ ] Advanced segmentation
- [ ] Privacy-compliant tracking
- [ ] Revenue tracking
- [ ] Attribution modeling

## Version History

### v2.0.0 (Current)
- ✅ Crashlytics integration
- ✅ Event queuing system
- ✅ Automatic screen tracking
- ✅ User property tracking
- ✅ Jewelry-specific events

### v1.0.0
- ✅ Basic Firebase Analytics
- ✅ Screen view tracking
- ✅ E-commerce events

## Support

For issues or questions:
1. Check this README
2. Review Firebase Analytics documentation
3. Check Firebase Console logs
4. Contact NoghreSod team

---
**Maintained by:** NoghreSod Team  
**Last Updated:** 2025-12-31  
**Status:** ✅ Production Ready
