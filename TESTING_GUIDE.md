# NoghreSod Testing Guide

## Overview

This guide provides comprehensive testing strategies and examples for the NoghreSod-Android project following Clean Architecture principles.

---

## 1. Unit Testing Framework Setup

### Dependencies (build.gradle.kts)

```kotlin
// Testing Framework
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.5")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

// Assertions
testImplementation("io.kotest:kotest-assertions-core:5.6.2")

// Architecture Testing
testImplementation("com.google.truth:truth:1.1.3")
```

---

## 2. Use Case Testing Pattern

### Example: RequestPaymentUseCaseTest

```kotlin
@RunWith(MockKRunner::class)
class RequestPaymentUseCaseTest {
    
    private lateinit var useCase: RequestPaymentUseCase
    private val mockRepository: PaymentRepository = mockk()
    
    @Before
    fun setUp() {
        useCase = RequestPaymentUseCase(mockRepository)
    }
    
    @Test
    fun `invoke with valid params calls repository`() = runTest {
        // Arrange
        val orderId = "order123"
        val amount = 100000L
        val gateway = PaymentGateway.ZARINPAL
        val expectedResponse = PaymentResponse(
            authority = "auth123",
            paymentUrl = "https://payment.url",
            status = PaymentStatus.PENDING,
            message = "Success"
        )
        
        coEvery { mockRepository.requestPayment(any(), any(), any(), any()) } returns
            Result.Success(expectedResponse)
        
        // Act
        val result = useCase(
            orderId = orderId,
            amount = amount,
            gateway = gateway
        )
        
        // Assert
        assert(result is Result.Success)
        assertEquals(expectedResponse, (result as Result.Success).data)
        coVerify(exactly = 1) { 
            mockRepository.requestPayment(orderId, amount, gateway, null)
        }
    }
    
    @Test
    fun `invoke with zero amount returns validation error`() = runTest {
        // Act
        val result = useCase(
            orderId = "order123",
            amount = 0,
            gateway = PaymentGateway.ZARINPAL
        )
        
        // Assert
        assert(result is Result.Error)
        assert((result as Result.Error).error is AppError.Validation)
        coVerify(exactly = 0) { mockRepository.requestPayment(any(), any(), any(), any()) }
    }
    
    @Test
    fun `invoke with blank orderId returns validation error`() = runTest {
        // Act
        val result = useCase(
            orderId = "",
            amount = 100000,
            gateway = PaymentGateway.ZARINPAL
        )
        
        // Assert
        assert(result is Result.Error)
    }
}
```

---

## 3. Repository Testing Pattern

### Example: PaymentRepositoryImplTest

```kotlin
@RunWith(MockKRunner::class)
class PaymentRepositoryImplTest {
    
    private lateinit var repository: PaymentRepositoryImpl
    private val mockZarinpal: ZarinpalPaymentService = mockk()
    private val mockCache: PaymentVerificationCache = mockk()
    private val mockConfig: PaymentConfiguration = mockk()
    private val mockDao: PaymentDao = mockk()
    
    @Before
    fun setUp() {
        repository = PaymentRepositoryImpl(
            zarinpalService = mockZarinpal,
            verificationCache = mockCache,
            paymentConfig = mockConfig,
            paymentDao = mockDao
        )
    }
    
    @Test
    fun `requestPayment stores payment in database`() = runTest {
        // Arrange
        val zarinpalResponse = PaymentResponse(
            authority = "auth123",
            paymentUrl = "https://zarinpal.url",
            status = PaymentStatus.PENDING,
            message = "Success"
        )
        coEvery { mockZarinpal.requestPayment(any()) } returns Result.Success(zarinpalResponse)
        coEvery { mockDao.insertPayment(any()) } returns 1
        
        // Act
        val result = repository.requestPayment(
            orderId = "order123",
            amount = 100000,
            gateway = PaymentGateway.ZARINPAL,
            mobile = "09121234567"
        )
        
        // Assert
        assert(result is Result.Success)
        coVerify { mockDao.insertPayment(any()) }
    }
    
    @Test
    fun `verifyPayment returns cached result for known authority`() = runTest {
        // Arrange
        val authority = "auth123"
        val cachedVerification = PaymentVerification(
            orderId = "order123",
            authority = authority,
            refId = "ref123",
            cardPan = null,
            cardHash = null,
            feeType = null,
            fee = null,
            status = PaymentStatus.SUCCESS,
            verifiedAt = System.currentTimeMillis()
        )
        coEvery { mockCache.getVerification(authority) } returns cachedVerification
        
        // Act
        val result = repository.verifyPayment(
            authority = authority,
            amount = 100000,
            gateway = PaymentGateway.ZARINPAL
        )
        
        // Assert
        assert(result is Result.Success)
        assertEquals(cachedVerification, (result as Result.Success).data)
        coVerify(exactly = 0) { mockZarinpal.verifyPayment(any(), any()) }
    }
}
```

---

## 4. DAO Testing Pattern

### Example: PaymentDaoTest

```kotlin
@RunWith(AndroidJUnit4::class)
class PaymentDaoTest {
    
    private lateinit var database: NoghreSodDatabase
    private lateinit var paymentDao: PaymentDao
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoghreSodDatabase::class.java
        ).build()
        paymentDao = database.paymentDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertPayment_insertsSuccessfully() = runBlocking {
        // Arrange
        val payment = PaymentEntity(
            id = "payment123",
            orderId = "order123",
            amount = 100000L,
            gateway = "ZARINPAL",
            authority = "auth123",
            refId = null,
            status = "PENDING",
            createdAt = System.currentTimeMillis(),
            paidAt = null,
            description = "Test payment"
        )
        
        // Act
        val rowId = paymentDao.insertPayment(payment)
        
        // Assert
        assert(rowId > 0)
    }
    
    @Test
    fun getPaymentsByOrderId_returnsAllPaymentsForOrder() = runBlocking {
        // Arrange
        val orderId = "order123"
        val payment1 = PaymentEntity(
            id = "pay1", orderId = orderId, amount = 100000L, gateway = "ZARINPAL",
            authority = "auth1", refId = null, status = "PENDING",
            createdAt = System.currentTimeMillis() - 1000, paidAt = null, description = null
        )
        val payment2 = PaymentEntity(
            id = "pay2", orderId = orderId, amount = 50000L, gateway = "IDPAY",
            authority = "auth2", refId = null, status = "FAILED",
            createdAt = System.currentTimeMillis(), paidAt = null, description = null
        )
        
        paymentDao.insertPayments(listOf(payment1, payment2))
        
        // Act
        val payments = paymentDao.getPaymentsByOrderId(orderId)
        
        // Assert
        assertEquals(2, payments.size)
        // Should be ordered by createdAt DESC (most recent first)
        assertEquals(payment2.id, payments[0].id)
        assertEquals(payment1.id, payments[1].id)
    }
}
```

---

## 5. Compose UI Testing Pattern

### Example: CartScreenTest

```kotlin
@RunWith(AndroidJUnit4::class)
class CartScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun cartScreen_showsLoadingState() {
        // Arrange
        val state = CartUiState(
            isLoading = true,
            cartItems = emptyList(),
            error = null
        )
        
        // Act
        composeTestRule.setContent {
            CartScreen(state = state)
        }
        
        // Assert
        composeTestRule.onNodeWithTag("LoadingIndicator").assertExists()
        composeTestRule.onNodeWithTag("CartList").assertDoesNotExist()
    }
    
    @Test
    fun cartScreen_showsErrorDialog() {
        // Arrange
        val state = CartUiState(
            isLoading = false,
            cartItems = emptyList(),
            error = "Failed to load cart"
        )
        val onDismissError = mockk<() -> Unit>()
        
        // Act
        composeTestRule.setContent {
            CartScreen(
                state = state,
                onDismissError = onDismissError
            )
        }
        
        // Assert
        composeTestRule.onNodeWithText("Failed to load cart").assertIsDisplayed()
        composeTestRule.onNodeWithText("بستن").performClick()
        verify { onDismissError() }
    }
    
    @Test
    fun cartScreen_removesItemWhenDeleteClicked() {
        // Arrange
        val cartItems = listOf(
            CartItem(id = "item1", productId = "prod1", quantity = 2, price = 100000L)
        )
        val state = CartUiState(
            isLoading = false,
            cartItems = cartItems,
            error = null
        )
        val onRemoveItem = mockk<(String) -> Unit>()
        
        // Act
        composeTestRule.setContent {
            CartScreen(
                state = state,
                onRemoveItem = onRemoveItem
            )
        }
        
        composeTestRule.onNodeWithTag("DeleteButton_item1").performClick()
        
        // Assert
        verify { onRemoveItem("item1") }
    }
}
```

---

## 6. Test Data Builders

### Example: PaymentTestBuilder

```kotlin
class PaymentTestBuilder {
    private var id: String = UUID.randomUUID().toString()
    private var orderId: String = "order123"
    private var amount: Long = 100000L
    private var gateway: PaymentGateway = PaymentGateway.ZARINPAL
    private var authority: String? = "auth123"
    private var refId: String? = null
    private var status: PaymentStatus = PaymentStatus.PENDING
    private var createdAt: Long = System.currentTimeMillis()
    private var paidAt: Long? = null
    private var description: String? = "Test payment"
    
    fun withId(id: String) = apply { this.id = id }
    fun withOrderId(orderId: String) = apply { this.orderId = orderId }
    fun withAmount(amount: Long) = apply { this.amount = amount }
    fun withGateway(gateway: PaymentGateway) = apply { this.gateway = gateway }
    fun withStatus(status: PaymentStatus) = apply { this.status = status }
    fun withAuthority(authority: String?) = apply { this.authority = authority }
    fun withRefId(refId: String?) = apply { this.refId = refId }
    fun paid() = apply {
        this.status = PaymentStatus.SUCCESS
        this.paidAt = System.currentTimeMillis()
    }
    
    fun build(): Payment = Payment(
        id = id,
        orderId = orderId,
        amount = amount,
        gateway = gateway,
        authority = authority,
        refId = refId,
        status = status,
        createdAt = createdAt,
        paidAt = paidAt,
        description = description
    )
    
    fun buildEntity(): PaymentEntity = PaymentEntity(
        id = id,
        orderId = orderId,
        amount = amount,
        gateway = gateway.name,
        authority = authority,
        refId = refId,
        status = status.name,
        createdAt = createdAt,
        paidAt = paidAt,
        description = description
    )
}

// Usage
@Test
fun test() {
    val payment = PaymentTestBuilder()
        .withOrderId("order456")
        .withAmount(50000L)
        .paid()
        .build()
}
```

---

## 7. Mock Strategies

### Mocking Database (Room)

```kotlin
val mockDao = mockk<PaymentDao> {
    coEvery { getPaymentById(any()) } returns PaymentEntity(...)
    coEvery { insertPayment(any()) } returns 1L
}
```

### Mocking Network Calls

```kotlin
val mockService = mockk<PaymentService> {
    coEvery { requestPayment(any()) } returns Result.Success(
        PaymentResponse(...)
    )
}
```

### Mocking Flows

```kotlin
val mockFlow = flow<Result<Cart>> {
    emit(Result.Success(testCart))
}.asLiveData()
```

---

## 8. Running Tests

### Unit Tests
```bash
./gradlew test
./gradlew testDebug
./gradlew testDebugUnitTest --info
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
./gradlew connectedDebugAndroidTest
```

### Specific Test Class
```bash
./gradlew test --tests "*PaymentRepositoryImplTest"
```

### Generate Coverage Report
```bash
./gradlew testDebug jacocoTestReport
# Report: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## 9. CI/CD Integration (GitHub Actions)

### .github/workflows/test.yml

```yaml
name: Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./gradlew test
  
  coverage:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./gradlew testDebug jacocoTestReport
      - uses: codecov/codecov-action@v3
        with:
          files: ./app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
```

---

## 10. Test Coverage Goals

| Module | Target | Current |
|--------|--------|----------|
| Payment Use Cases | 90% | 0% |
| Payment Repository | 85% | 0% |
| Cart ViewModel | 80% | 0% |
| Payment DAO | 95% | 0% |
| **Overall** | **>80%** | **0%** |

---

## Next Steps (Sprint +1-2)

1. ✅ **Use Cases** - Implement RequestPaymentUseCaseTest, VerifyPaymentUseCaseTest
2. ✅ **Repository** - Implement PaymentRepositoryImplTest
3. ✅ **DAO** - Implement PaymentDaoTest
4. ✅ **ViewModels** - Implement CartViewModelTest, PaymentViewModelTest
5. ✅ **UI Tests** - Implement Compose UI tests
6. ✅ **CI/CD** - Setup GitHub Actions workflows
7. ✅ **Coverage** - Achieve 80%+ coverage

---

## References

- [Mockk Documentation](https://mockk.io/)
- [Kotest Assertions](https://kotest.io/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)

