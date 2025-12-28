# 🌍 **Phase 7-13: Advanced Localization & Enhancement**

**Status:** Ready to implement  
**Date:** December 28, 2025 - 16:22 UTC+3:30  
**Target:** Production-Ready with Full Persian UI + Iranian Payments

---

## 📋 **Phases Overview**

| Phase | Title | Priority | Hours | Status |
|-------|-------|----------|-------|--------|
| **7** | Persian/RTL UI | CRITICAL | 3 | ⏳ Ready |
| **8** | Iranian Payments | CRITICAL | 4 | ⏳ Ready |
| **9** | Firebase Crashlytics | HIGH | 2 | ⏳ Ready |
| **10** | Dark Mode Support | HIGH | 2 | ⏳ Ready |
| **11** | Push Notifications (FCM) | MEDIUM | 1.5 | ⏳ Ready |
| **12** | Advanced Testing | MEDIUM | 2 | ⏳ Ready |
| **13** | ProGuard & Release | HIGH | 1.5 | ⏳ Ready |
| **TOTAL** | **All Advanced Features** | - | **15.5** | 🎯 |

---

## ✅ **Phase 7: Persian/RTL Localization**

### Critical Tasks (MUST DO)

#### 7.1 - String Resources (فارسی)
- ✅ Create `values-fa/strings.xml` with ALL UI text in Persian
- ✅ Support for:
  - Navigation items
  - Product screens
  - Cart & checkout
  - Payments
  - Orders
  - Auth screens
  - Errors & messages
  - Settings

#### 7.2 - RTL Layout Configuration
- ✅ Add `android:supportsRtl="true"` to AndroidManifest.xml
- ✅ Update theme with RTL support
- ✅ All layouts use start/end instead of left/right

#### 7.3 - PersianUtils.kt
- ✅ Convert English digits to Persian (0-9 → ۰-۹)
- ✅ Format prices in Toman with Persian digits
- ✅ Iranian phone validation & formatting
- ✅ Postal code validation & formatting
- ✅ Weight formatting for jewelry items

#### 7.4 - Update All Composables
- ✅ Replace hardcoded strings with `stringResource(R.string.*)`
- ✅ Use Persian formatters for numbers/prices
- ✅ Test RTL rendering on all screens

**Estimated Time:** 3 hours  
**Files to Create:** 2 major files + updates to 20+ existing files

---

## 💳 **Phase 8: Iranian Payment Gateway Integration**

### Supported Gateways

1. **Zarinpal** (زرین‌پال) - Most Popular ⭐⭐⭐
2. **IDPay** (آیدی‌پی)
3. **NextPay** (نکست‌پی)
4. **Zibal** (زیبال)
5. **PayPing** (پی‌پینگ)

### 8.1 - Payment Domain Models
- ✅ PaymentStatus enum (PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED)
- ✅ PaymentGateway enum (ZARINPAL, IDPAY, NEXTPAY, ZIBAL, PAYPINGENUM, CASH_ON_DELIVERY)
- ✅ PaymentRequest, PaymentResponse, PaymentVerification data classes
- ✅ Payment model with complete transaction info

### 8.2 - Zarinpal Integration
- ✅ ZarinpalApi interface (request & verify endpoints)
- ✅ ZarinpalPaymentRequestDto/ResponseDto
- ✅ ZarinpalVerifyRequestDto/ResponseDto
- ✅ ZarinpalPaymentService (request & verify logic)

### 8.3 - Payment Repository
- ✅ PaymentRepository interface
- ✅ PaymentRepositoryImpl with:
  - requestPayment() - initiate payment
  - verifyPayment() - verify after callback
  - getPayment() - retrieve payment details
  - getOrderPayments() - payment history

### 8.4 - CheckoutViewModel Integration
- ✅ initiatePayment(gateway) - start payment process
- ✅ verifyPayment(authority) - handle callback
- ✅ Payment state management
- ✅ Error handling for payment failures

### 8.5 - Payment Callback Handling
- ✅ Deep link setup: `noghresod://payment/callback`
- ✅ Parse callback parameters (authority, status)
- ✅ Verify payment with gateway
- ✅ Update order status on success

**Estimated Time:** 4 hours  
**Files to Create:** 8-10 files  
**Dependencies:** Retrofit2, Gson

---

## 🔥 **Phase 9: Firebase Crashlytics & Analytics**

### 9.1 - Firebase Setup
- ✅ Add Firebase BOM to gradle
- ✅ Add Crashlytics & Analytics dependencies
- ✅ Add Google Services plugin
- ✅ Download google-services.json from Firebase Console

### 9.2 - Application Class Configuration
- ✅ Initialize Firebase
- ✅ Setup Crashlytics (disable in DEBUG)
- ✅ Setup Analytics
- ✅ Integrate Timber logging with Crashlytics
- ✅ Create CrashlyticsTree for Timber

### 9.3 - Analytics Manager
- ✅ Log screen views
- ✅ Log user events:
  - Product viewed
  - Product added to cart
  - Checkout started
  - Payment initiated
  - Order placed
  - Search performed
  - Favorite toggled

### 9.4 - Crashlytics Integration
- ✅ Set user ID (anonymous)
- ✅ Log custom keys (order total, payment gateway)
- ✅ Exception logging
- ✅ Non-fatal error reporting

**Estimated Time:** 2 hours  
**Files to Create:** 2 files + configuration updates

---

## 🌙 **Phase 10: Dark Mode Support**

### 10.1 - Material 3 Colors (Light & Dark)
- ✅ Define light theme colors
- ✅ Define dark theme colors
- ✅ Use Material Design 3 color system
- ✅ Proper contrast ratios for accessibility

### 10.2 - Theme Implementation
- ✅ LightColorScheme and DarkColorScheme
- ✅ NoghreSodTheme composable
- ✅ Support for system theme preference
- ✅ Dynamic color support (Android 12+)
- ✅ Status bar styling

### 10.3 - Theme Preference Manager
- ✅ ThemeMode enum (LIGHT, DARK, SYSTEM)
- ✅ DataStore for persistence
- ✅ Settings screen for theme selection
- ✅ Real-time theme switching

**Estimated Time:** 2 hours  
**Files to Create:** 3 files

---

## 📬 **Phase 11: Firebase Cloud Messaging (FCM)**

### 11.1 - FCM Setup
- ✅ Add FCM dependency
- ✅ Request notification permissions (Android 13+)
- ✅ Setup notification channel
- ✅ Handle token refresh

### 11.2 - Notification Service
- ✅ MyFirebaseMessagingService
- ✅ Handle incoming messages
- ✅ Display notifications in Persian
- ✅ Handle notification clicks

### 11.3 - Notification Events
- ✅ Order confirmed
- ✅ Order shipped
- ✅ Order delivered
- ✅ New product available
- ✅ Special discounts

**Estimated Time:** 1.5 hours  
**Files to Create:** 2-3 files

---

## 🧪 **Phase 12: Advanced Testing**

### 12.1 - Unit Tests
- ✅ PersianUtils tests
- ✅ PaymentRepository tests (with payment gateways)
- ✅ Zarinpal service tests (mocked)
- ✅ Payment verification tests

### 12.2 - Integration Tests
- ✅ Payment flow end-to-end
- ✅ Order to payment to verification
- ✅ Error handling in payment

### 12.3 - UI Tests (Espresso)
- ✅ Checkout screen tests
- ✅ Payment gateway selection
- ✅ Order confirmation

**Estimated Time:** 2 hours  
**Files to Create:** 5-7 test files

---

## 📦 **Phase 13: ProGuard & Release Build**

### 13.1 - ProGuard Configuration
- ✅ Keep Jetpack Compose classes
- ✅ Keep Retrofit/OkHttp
- ✅ Keep Room database
- ✅ Keep Hilt generated code
- ✅ Keep Firebase classes
- ✅ Keep our package classes

### 13.2 - Release Build Setup
- ✅ Signing configuration
- ✅ Version numbering
- ✅ Build types (debug/release)
- ✅ Firebase setup for production

### 13.3 - Release Preparation
- ✅ Create release notes
- ✅ Generate APK/AAB
- ✅ Internal testing
- ✅ Beta testing setup
- ✅ Play Store listing

**Estimated Time:** 1.5 hours  
**Deliverables:** Signed APK/AAB + documentation

---

## 🎯 **Success Criteria**

### Persian UI ✅
- [ ] All text in Persian (strings.xml)
- [ ] RTL layouts everywhere
- [ ] Persian numbers in prices/quantities
- [ ] Iranian phone validation working
- [ ] Postal code formatting correct

### Payment Systems ✅
- [ ] Zarinpal integration complete
- [ ] Payment request working
- [ ] Callback handling working
- [ ] Payment verification accurate
- [ ] Transaction history tracking
- [ ] Cash on delivery option

### Firebase ✅
- [ ] Crashlytics catching errors
- [ ] Analytics tracking events
- [ ] Custom events logged
- [ ] User identification working

### Dark Mode ✅
- [ ] Light theme complete
- [ ] Dark theme complete
- [ ] Theme switching working
- [ ] System preference respected

### Notifications ✅
- [ ] FCM token handling
- [ ] Notification display
- [ ] Persian notification text
- [ ] Click handling

---

## 🚀 **Execution Priority**

**Must Do First:**
1. ✅ Phase 7 - Persian UI (without this, nothing works for Iran users)
2. ✅ Phase 8 - Payment Systems (essential for e-commerce)

**Then Do:**
3. ✅ Phase 9 - Firebase (needed for production monitoring)
4. ✅ Phase 13 - Release Build (needed for Play Store)

**Can Do After Release:**
5. ✅ Phase 10 - Dark Mode (nice to have)
6. ✅ Phase 11 - Notifications (can add later)
7. ✅ Phase 12 - Advanced Tests (continuous)

---

## 📊 **Total Implementation Stats**

```
Total Phases:         7 phases (Phase 7-13)
Total Files:          30+ new files
Total Lines:          8,000+ lines of code
Total Time:           ~15.5 hours
Priority Phases:      Phase 7, 8, 9, 13
Optional Phases:      Phase 10, 11, 12
```

---

## ✨ **Expected Outcome**

After completing these phases, the app will have:

✅ **Complete Persian Localization**
- All UI in Persian
- RTL layouts
- Persian number formatting
- Iranian phone validation

✅ **Iranian Payment Integration**
- Zarinpal gateway
- Multiple payment methods
- Secure transactions
- Payment history

✅ **Production Ready**
- Firebase monitoring
- Error tracking
- Analytics
- ProGuard optimization

✅ **Enhanced UX**
- Dark mode support
- Push notifications
- Comprehensive testing

---

## 🎓 **Developer Notes**

### Important Configurations
```
# Zarinpal
- Merchant ID: Get from Zarinpal console
- Sandbox URL: https://sandbox.zarinpal.com/ (for testing)
- Production URL: https://www.zarinpal.com/ (for live)

# Firebase
- google-services.json: Download from Firebase Console
- Enable Crashlytics in console
- Enable Analytics in console

# Signing
- Create signing key for Play Store
- Configure in build.gradle
- Keep backup copy
```

### Testing Strategies
```
# Payment Testing (Zarinpal Sandbox)
- Use sandbox API credentials
- Test cards: Zarinpal provides test cards
- Test both success and failure flows

# Firebase Testing
- Use Analytics Debugger to test events
- Force crash to test Crashlytics
- Check Firebase Console for events

# Dark Mode Testing
- Test all screens in light/dark/system
- Check text contrast (AA minimum)
- Test on various devices
```

---

**Status:** Ready to implement phases 7-13  
**Next Step:** Start with Phase 7 (Persian Localization)

**Command:** مستعد برای شروع فازهای ۷ تا ۱۳ 🚀
