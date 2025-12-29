# NoghreSod Test Structure - Phase 2

## 📁 Correct Test Directory Structure

### Unit Tests (`app/src/test/kotlin/com/noghre/sod/`)

Unit tests برای domain logic, utilities و data layer بدون Android dependencies

```
app/src/test/kotlin/com/noghre/sod/
├── core/
│   ├── security/
│   │   ├── PaymentRateLimiterTest.kt          # Rate limiting logic
│   │   └── NativeKeyManagerTest.kt            # Native key retrieval
│   └── util/
│       ├── PersianNumberFormatterTest.kt      # Persian number formatting
│       └── PersianDateConverterTest.kt        # Persian date conversion
│
├── domain/
│   ├── model/
│   │   ├── MoneyTest.kt                       # Toman/Rial type safety
│   │   └── PaymentStatusTest.kt               # Payment state machine
│   │
│   └── usecase/payment/
│       ├── ValidatePaymentCallbackUseCaseTest.kt      # Callback validation
│       ├── RequestPaymentUseCaseTest.kt               # Payment request logic
│       └── VerifyPaymentUseCaseTest.kt                # Payment verification
│
└── data/
    └── repository/
        ├── PaymentRepositoryTest.kt           # Repository logic
        └── TransactionRepositoryTest.kt       # Transaction persistence
```

**Test Framework**: JUnit4 + Mockk + Coroutines Test

### Integration Tests (`app/src/androidTest/kotlin/com/noghre/sod/`)

Integration tests برای سناریوهای end-to-end with Android framework

```
app/src/androidTest/kotlin/com/noghre/sod/
├── integration/
│   ├── PaymentFlowIntegrationTest.kt         # Full payment flow
│   ├── DatabaseIntegrationTest.kt            # Room database operations
│   └── NetworkIntegrationTest.kt             # Retrofit API interactions
│
├── ui/
│   ├── PaymentScreenUiTest.kt                # Compose UI tests
│   ├── CartScreenUiTest.kt                   # Cart UI interactions
│   └── ProductDetailUiTest.kt                # Product detail screen
│
└── HiltTestActivity.kt                       # Test activity with Hilt
HiltTestRunner.kt                             # Custom test runner
```

**Test Framework**: JUnit4 + Espresso + Compose Testing + Hilt Testing

---

## 🔧 Test Configuration

### `build.gradle.kts` Requirements

```kotlin
// Test runner
android {
    testInstrumentationRunner = "com.noghre.sod.HiltTestRunner"
}

// Dependencies
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

### `HiltTestActivity.kt` Template

```kotlin
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()
```

### `HiltTestRunner.kt` Template

```kotlin
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application {
        return super.newApplication(
            cl,
            HiltTestApplication::class.java.name,
            context
        )
    }
}
```

---

## ✅ Unit Test Template

```kotlin
import io.mockk.*
import org.junit.Test
import org.junit.Before
import kotlinx.coroutines.test.runTest

class MyUseCaseTest {
    
    @Before
    fun setup() {
        // Initialize mocks
    }
    
    @Test
    fun `should return result when condition is met`() = runTest {
        // Given
        val input = "test"
        
        // When
        val result = useCase.invoke(input)
        
        // Then
        assertThat(result).isNotNull()
    }
}
```

---

## ✅ Integration Test Template

```kotlin
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun `should complete payment flow successfully`() {
        // Test actual flow with real dependencies from Hilt
    }
}
```

---

## ✅ Compose UI Test Template

```kotlin
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule

class PaymentScreenUiTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `payment button should be enabled when amount is valid`() {
        composeTestRule.setContent {
            PaymentScreen()
        }
        
        composeTestRule
            .onNodeWithTag("payButton")
            .assertIsEnabled()
    }
}
```

---

## 📊 Test Coverage Goals

| Category | Target | Current |
|----------|--------|----------|
| Unit Tests | 70%+ | To Implement |
| Integration Tests | 50%+ | To Implement |
| Overall Coverage | 65%+ | To Implement |

---

## 🚀 Running Tests

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
```

### Run Specific Test
```bash
./gradlew test --tests "com.noghre.sod.domain.usecase.PaymentTest"
```

---

## 📝 Test Naming Convention

```kotlin
// Format: shouldReturnExpectedBehavior_WhenCondition
@Test
fun `should return PaymentResult_Success_when_request_is_valid`()

@Test
fun `should throw InvalidAmountException_when_amount_is_zero`()

@Test
fun `should prevent replay attacks_when_authority_is_already_verified`()
```

---

## 🔍 Debugging Tests

### Enable Test Logging
```kotlin
Timber.plant(Timber.DebugTree())
```

### Run with Verbose Output
```bash
./gradlew test --info
```

### Debug a Specific Test
```bash
./gradlew test --debug-jvm
```

---

## ⚡ Performance Tips

1. **Disable Animations** in instrumentation tests
2. **Use Mock Objects** for external dependencies
3. **Parallel Test Execution** for unit tests
4. **Avoid Thread Sleep** - use Espresso idling resources
5. **Use Emulator API 30+** for faster instrumentation tests

---

**آخرین بروزرسانی**: 2025-12-29  
**نسخه**: 1.0  
**وضعیت**: ✅ Ready for Phase 2 Implementation
