# Outstanding TODOs - Implementation Progress

**Last Updated:** December 28, 2025  
**Status:** 2 of 8 High/Medium Priority TODOs Completed

---

## ✅ COMPLETED TODOs

### 1. Use Case Layer Introduction ✅

**Status:** COMPLETE  
**Priority:** HIGH  
**Date Completed:** December 28, 2025

**What Was Done:**
- Created `domain/usecase/payment/RequestPaymentUseCase.kt`
  - Encapsulates payment request logic
  - Validates orderId, amount, mobile
  - Delegates to PaymentRepository
  - Testable with mock repository
  
- Created `domain/usecase/payment/VerifyPaymentUseCase.kt`
  - Encapsulates payment verification logic
  - Validates authority and amount
  - Ensures idempotent operations (via repository caching)
  - Handles payment completion workflow

**Code Example:**
```kotlin
// Usage in ViewModel
class PaymentViewModel @Inject constructor(
    private val requestPaymentUseCase: RequestPaymentUseCase,
    private val verifyPaymentUseCase: VerifyPaymentUseCase
) : ViewModel() {
    fun requestPayment(orderId: String, amount: Long) {
        viewModelScope.launch {
            val result = requestPaymentUseCase(
                orderId = orderId,
                amount = amount,
                gateway = PaymentGateway.ZARINPAL
            )
            // Handle result
        }
    }
}
```

**Benefits:**
- ✅ Testable: Mock PaymentRepository easily
- ✅ Reusable: Used by different features
- ✅ Encapsulated: Payment business logic centralized
- ✅ Type-safe: Returns `Result<PaymentResponse>`

**Commits:**
- [c652df68b61f08270be9ccb3cf82c730cd955371](https://github.com/Ya3er02/NoghreSod-Android/commit/c652df68b61f08270be9ccb3cf82c730cd955371) - RequestPaymentUseCase
- [89f1e2f55626f3be6b6cdaefd25b91eb2574020e](https://github.com/Ya3er02/NoghreSod-Android/commit/89f1e2f55626f3be6b6cdaefd25b91eb2574020e) - VerifyPaymentUseCase

---

### 2. Room DAO Implementation ✅

**Status:** COMPLETE  
**Priority:** HIGH  
**Date Completed:** December 28, 2025

**What Was Done:**

**Created PaymentEntity** (`data/database/entity/PaymentEntity.kt`)
```kotlin
@Entity(
    tableName = "payments",
    indices = [
        Index(name = "idx_payment_id", value = ["id"], unique = true),
        Index(name = "idx_payment_orderId", value = ["orderId"]),
        Index(name = "idx_payment_authority", value = ["authority"], unique = true),
        Index(name = "idx_payment_status", value = ["status"]),
        Index(name = "idx_payment_createdAt", value = ["createdAt"])
    ]
)
data class PaymentEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val amount: Long,
    val gateway: String,
    val authority: String?,
    val refId: String?,
    val status: String,
    val createdAt: Long,
    val paidAt: Long?,
    val description: String?
)
```

**Created PaymentDao** (`data/database/dao/PaymentDao.kt`)
- `insertPayment(payment)` - Store new payments
- `getPaymentById(paymentId)` - Query by ID
- `getPaymentByAuthority(authority)` - Query by gateway authority (idempotency check)
- `getPaymentsByOrderId(orderId)` - Get payment history
- `observePaymentsByOrderId(orderId)` - Reactive payment updates
- `updatePaymentStatus(...)` - Update after verification
- `deletePayment(paymentId)` - Cleanup
- `clearAllPayments()` - Reset cache

**Updated PaymentRepositoryImpl** - Now integrates DAO:
```kotlin
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val zarinpalService: ZarinpalPaymentService,
    private val verificationCache: PaymentVerificationCache,
    private val paymentConfig: PaymentConfiguration,
    private val paymentDao: PaymentDao  // ← NEW
) : PaymentRepository {
    
    override suspend fun requestPayment(...): Result<PaymentResponse> {
        // ... gateway request ...
        if (result is Result.Success) {
            // PERSIST to database
            val entity = PaymentEntity.fromDomainModel(...)
            paymentDao.insertPayment(entity)
        }
    }
    
    override suspend fun getPayment(paymentId: String): Result<Payment> {
        val entity = paymentDao.getPaymentById(paymentId)
        return if (entity != null) {
            Result.Success(entity.toDomainModel())
        } else {
            Result.Error(AppError.NotFound(...))
        }
    }
    
    override suspend fun getOrderPayments(orderId: String): Result<List<Payment>> {
        val entities = paymentDao.getPaymentsByOrderId(orderId)
        return Result.Success(entities.map { it.toDomainModel() })
    }
}
```

**Database Schema:**
```sql
CREATE TABLE payments (
    id TEXT PRIMARY KEY,
    orderId TEXT NOT NULL,
    amount INTEGER NOT NULL,
    gateway TEXT NOT NULL,
    authority TEXT UNIQUE,
    refId TEXT,
    status TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    paidAt INTEGER,
    description TEXT
);

CREATE INDEX idx_payment_orderId ON payments(orderId);
CREATE INDEX idx_payment_authority ON payments(authority);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_createdAt ON payments(createdAt);
```

**Features:**
- ✅ Store payment records after gateway requests
- ✅ Query payment history by order ID
- ✅ Support payment retry logic
- ✅ Enable auditing and compliance
- ✅ Indexed for fast queries
- ✅ Reactive queries with Flow
- ✅ Type-safe entity mapping

**Commits:**
- [df001f577325fb88aa88a0b440fe5e424021dbd7](https://github.com/Ya3er02/NoghreSod-Android/commit/df001f577325fb88aa88a0b440fe5e424021dbd7) - PaymentDao
- [c9a3f36800e903f854b46783552b337dacc1a3a0](https://github.com/Ya3er02/NoghreSod-Android/commit/c9a3f36800e903f854b46783552b337dacc1a3a0) - PaymentEntity
- [90e7da5e6e746dba15582a908638401480050b3f](https://github.com/Ya3er02/NoghreSod-Android/commit/90e7da5e6e746dba15582a908638401480050b3f) - PaymentRepositoryImpl integration

---

## ⏳ REMAINING TODOs (6 items)

### 3. Additional Payment Gateway Integration ⏳

**Priority:** MEDIUM  
**Effort:** Medium (1-2 sprints)  
**Dependencies:** None (can proceed independently)

**What Needs to Be Done:**
1. Implement `IDPayPaymentService`
   - Research IDPay API documentation
   - Create payment request/verification logic
   - Handle IDPay-specific error codes

2. Implement `NextPayPaymentService`
   - Research NextPay API
   - Implement request/verification
   - Add to PaymentRepositoryImpl

3. Implement `ZibalPaymentService` (optional)
   - Research Zibal API
   - Implement gateway logic

4. Implement `PayPingsumPaymentService` (optional)
   - Research Paypingsum API

**Location:** `data/payment/services/`

**Code Structure Example:**
```kotlin
// data/payment/services/IDPayPaymentService.kt
@Singleton
class IDPayPaymentService @Inject constructor(
    private val httpClient: Retrofit,
    private val config: PaymentConfiguration
) {
    suspend fun requestPayment(request: PaymentRequest): Result<PaymentResponse> {
        // IDPay-specific API call
    }
    
    suspend fun verifyPayment(authority: String, amount: Long): Result<PaymentVerification> {
        // IDPay verification
    }
}
```

**Acceptance Criteria:**
- [ ] All 4 gateway services implemented and tested
- [ ] PaymentRepositoryImpl routes correctly to each service
- [ ] Error codes mapped to AppError types
- [ ] Integration tests pass
- [ ] Documentation updated with gateway-specific notes

---

### 4. String Localization ⏳

**Priority:** MEDIUM  
**Effort:** Low (0.5 sprint)  
**Dependencies:** None

**What Needs to Be Done:**
1. Create `res/values/strings.xml` with all hardcoded Persian strings
2. Create `res/values-en/strings.xml` for English translations
3. Create `StringProvider` utility in core module
4. Replace hardcoded strings in repositories with StringProvider

**Examples:**
```xml
<!-- res/values/strings.xml -->
<string name="payment_validation_amount_error">مبلغ باید بیشتر از صفر باشد</string>
<string name="payment_request_success">درخواست پرداخت با موفقیت ارسال شد</string>
<string name="cart_item_invalid_product">شناسه محصول معتبر نیست</string>
<string name="error_generic">خطای غیرمنتظره رخ داد. لطفاً دوباره تلاش کنید.</string>
```

**Acceptance Criteria:**
- [ ] All hardcoded Persian strings moved to resources
- [ ] English translations available
- [ ] StringProvider tested
- [ ] No hardcoded strings in data/domain layers
- [ ] RTL layout support verified

---

### 5. Unit Testing (Payment & Cart Modules) ⏳

**Priority:** MEDIUM  
**Effort:** Medium (1-2 sprints)  
**Dependencies:** Use case layer (completed ✅)

**What Needs to Be Done:**
1. Create unit tests for use cases
   - `RequestPaymentUseCaseTest` - Validation, repository calls
   - `VerifyPaymentUseCaseTest` - Idempotency, caching

2. Create unit tests for repositories
   - `PaymentRepositoryImplTest` - All flows, error handling
   - Mock PaymentDao
   - Mock verification cache
   - Mock gateway services

3. Create unit tests for ViewModels
   - `CartViewModelTest` - Flow.combine, state updates
   - Test loading states
   - Test error handling

4. Create integration tests
   - DAO operations
   - End-to-end payment flow

**Test Framework:** JUnit5 + Mockk + Espresso (for UI tests)

**Example Test:**
```kotlin
@Test
fun requestPayment_validAmount_callsRepository() = runTest {
    // Arrange
    val useCase = RequestPaymentUseCase(mockRepository)
    
    // Act
    val result = useCase(
        orderId = "order123",
        amount = 100000,
        gateway = PaymentGateway.ZARINPAL
    )
    
    // Assert
    verify { mockRepository.requestPayment(any(), eq(100000), any(), any()) }
    assert(result is Result.Success)
}

@Test
fun requestPayment_zeroAmount_returnsValidationError() = runTest {
    // Arrange
    val useCase = RequestPaymentUseCase(mockRepository)
    
    // Act
    val result = useCase(
        orderId = "order123",
        amount = 0,
        gateway = PaymentGateway.ZARINPAL
    )
    
    // Assert
    assert(result is Result.Error)
    assert((result as Result.Error).error is AppError.Validation)
    verify(exactly = 0) { mockRepository.requestPayment(any(), any(), any(), any()) }
}
```

**Acceptance Criteria:**
- [ ] >80% code coverage for payment module
- [ ] >80% code coverage for cart module
- [ ] All tests passing
- [ ] CI/CD integrated (GitHub Actions)
- [ ] Test report generated

---

### 6. Compose UI Tests ⏳

**Priority:** LOW  
**Effort:** Medium (1 sprint)  
**Dependencies:** CartViewModel fixes (completed ✅)

**What Needs to Be Done:**
1. Test CartUiState rendering
   - Loading state animation
   - Error state display
   - Empty cart state
   - Populated cart state

2. Test error dismissal
   - Error dialog appearance
   - Dismiss button functionality
   - clearError() triggers

3. Test optimistic updates
   - Add product - immediate update
   - Remove product - immediate removal
   - Error revert

**Test Framework:** Compose Testing API + Espresso

**Example:**
```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun cartScreen_showsLoadingState_whenLoading() {
    composeTestRule.setContent {
        val state = CartUiState(isLoading = true)
        CartScreen(state = state)
    }
    
    composeTestRule.onNodeWithTag("LoadingIndicator").assertExists()
}
```

**Acceptance Criteria:**
- [ ] All major UI states tested
- [ ] Loading transitions verified
- [ ] Error handling tested
- [ ] Test code maintainable

---

### 7. Firebase Crashlytics Integration ⏳

**Priority:** LOW  
**Effort:** Low (0.5 sprint)  
**Dependencies:** None

**What Needs to Be Done:**
1. Add Firebase dependency to `build.gradle`
2. Implement Crashlytics in `GlobalExceptionHandler`
3. Send non-PII errors to Crashlytics
4. Set up custom keys (order ID, gateway type, etc.)
5. Test crash reporting

**Code Example:**
```kotlin
@Singleton
class GlobalExceptionHandler @Inject constructor() {
    val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "[COROUTINE_ERROR]")
        
        // Send to Crashlytics (non-PII only)
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.recordException(throwable)
    }
}
```

**Acceptance Criteria:**
- [ ] Crashlytics configured
- [ ] Non-sensitive errors reported
- [ ] Custom breadcrumbs added
- [ ] Production errors monitored

---

### 8. Performance Optimization ⏳

**Priority:** LOW  
**Effort:** Low (0.5 sprint)  
**Dependencies:** Room DAO (completed ✅)

**What Needs to Be Done:**
1. Profile `PaymentVerificationCache` under load
   - Test with 1000+ cached verifications
   - Measure memory usage
   - Check TTL cleanup performance

2. Consider optimizations if needed
   - LRU (Least Recently Used) cache replacement
   - Reduce TTL if cache grows too large
   - Periodic cleanup of expired entries

3. Optimize Room DAO queries
   - Verify index effectiveness
   - Profile query times
   - Add query optimization if needed

**Acceptance Criteria:**
- [ ] Cache performs well under load
- [ ] Memory usage acceptable
- [ ] No query performance issues
- [ ] Optimization recommendations documented

---

## 📊 Summary

| Priority | Item | Status | Sprint |
|----------|------|--------|--------|
| HIGH | Use Case Layer | ✅ DONE | Current |
| HIGH | Room DAO | ✅ DONE | Current |
| MEDIUM | Payment Gateways | ⏳ TODO | Sprint +1 |
| MEDIUM | String Localization | ⏳ TODO | Sprint +1 |
| MEDIUM | Unit Testing | ⏳ TODO | Sprint +1-2 |
| LOW | Compose UI Tests | ⏳ TODO | Sprint +2 |
| LOW | Crashlytics | ⏳ TODO | Sprint +2 |
| LOW | Performance Tune | ⏳ TODO | Sprint +3 |

**Total Completed:** 2/8 (25%)  
**Total Remaining:** 6/8 (75%)

---

## 🚀 Next Steps

**For Current Sprint:**
1. ✅ Implement use cases (DONE)
2. ✅ Implement Room DAO (DONE)
3. Run full test suite
4. Code review and merge

**For Next Sprint:**
1. Implement additional payment gateways
2. Add string localization
3. Begin unit testing framework setup

**For Future Sprints:**
1. Complete comprehensive unit tests
2. Add Compose UI tests
3. Integrate Firebase Crashlytics
4. Performance profiling and optimization

---

**Last Updated:** December 28, 2025  
**Status:** 2 High Priority TODOs Complete | 6 Medium/Low Priority Pending
