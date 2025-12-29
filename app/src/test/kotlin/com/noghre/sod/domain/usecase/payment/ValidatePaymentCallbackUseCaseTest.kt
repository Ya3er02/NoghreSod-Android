package com.noghre.sod.domain.usecase.payment

import io.mockk.*
import org.junit.Test
import org.junit.Before
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import com.noghre.sod.data.payment.PaymentRepository

/**
 * Unit tests برای ValidatePaymentCallbackUseCase
 * 
 * اهداف:
 * - تست callback validation
 * - تست replay attack prevention
 * - تست server verification
 */
class ValidatePaymentCallbackUseCaseTest {
    
    private lateinit var useCase: ValidatePaymentCallbackUseCase
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var verificationCache: PaymentVerificationCache
    
    @Before
    fun setup() {
        paymentRepository = mockk()
        verificationCache = InMemoryPaymentVerificationCache()
        
        useCase = ValidatePaymentCallbackUseCase(
            paymentRepository = paymentRepository,
            verificationCache = verificationCache
        )
    }
    
    // ==================== Valid Callback Tests ====================
    
    @Test
    fun `should validate callback with correct parameters`() = runTest {
        val authority = "valid_authority_123"
        val refId = "ref_123"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        val result = useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Valid::class.java)
    }
    
    @Test
    fun `should mark callback as verified after successful validation`() = runTest {
        val authority = "test_authority"
        val refId = "ref_test"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        
        // Should be marked as verified in cache
        val isVerified = verificationCache.isAlreadyVerified(authority)
        assertThat(isVerified).isTrue()
    }
    
    // ==================== Replay Attack Prevention ====================
    
    @Test
    fun `should reject callback if already verified`() = runTest {
        val authority = "replay_attack_123"
        val refId = "ref_123"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        // First call - should succeed
        val result1 = useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        assertThat(result1).isInstanceOf(ValidationResult.Valid::class.java)
        
        // Second call with same authority - should fail
        val result2 = useCase.invoke(
            authority = authority,
            status = "OK",
            refId = "ref_different"
        )
        assertThat(result2).isInstanceOf(ValidationResult.AlreadyProcessed::class.java)
    }
    
    @Test
    fun `should prevent duplicate processing within time window`() = runTest {
        val authority = "dup_test_123"
        val refId = "ref_123"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        // First verification
        useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        
        // Immediate retry should be rejected
        val result = useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        
        assertThat(result).isInstanceOf(ValidationResult.AlreadyProcessed::class.java)
    }
    
    // ==================== Invalid Status Tests ====================
    
    @Test
    fun `should reject callback with non-OK status`() = runTest {
        val result = useCase.invoke(
            authority = "auth_123",
            status = "FAILED",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `should reject callback with empty status`() = runTest {
        val result = useCase.invoke(
            authority = "auth_123",
            status = "",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    // ==================== Empty Parameter Tests ====================
    
    @Test
    fun `should reject callback with empty authority`() = runTest {
        val result = useCase.invoke(
            authority = "",
            status = "OK",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `should reject callback with empty refId`() = runTest {
        val result = useCase.invoke(
            authority = "auth_123",
            status = "OK",
            refId = ""
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    // ==================== Server Verification Tests ====================
    
    @Test
    fun `should call server to verify payment`() = runTest {
        val authority = "server_verify_123"
        val refId = "ref_123"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        useCase.invoke(
            authority = authority,
            status = "OK",
            refId = refId
        )
        
        coVerify {
            paymentRepository.verifyPayment(any())
        }
    }
    
    @Test
    fun `should reject if server verification fails`() = runTest {
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Failed
        
        val result = useCase.invoke(
            authority = "auth_123",
            status = "OK",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.ServerVerificationFailed::class.java)
    }
    
    @Test
    fun `should handle server verification error gracefully`() = runTest {
        coEvery {
            paymentRepository.verifyPayment(any())
        } throws Exception("Server connection error")
        
        val result = useCase.invoke(
            authority = "auth_123",
            status = "OK",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Error::class.java)
    }
    
    // ==================== Different Authority Tests ====================
    
    @Test
    fun `different authorities should be tracked independently`() = runTest {
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        val auth1 = "authority_1"
        val auth2 = "authority_2"
        
        // Verify first authority
        val result1 = useCase.invoke(
            authority = auth1,
            status = "OK",
            refId = "ref_1"
        )
        assertThat(result1).isInstanceOf(ValidationResult.Valid::class.java)
        
        // Verify second authority - should succeed
        val result2 = useCase.invoke(
            authority = auth2,
            status = "OK",
            refId = "ref_2"
        )
        assertThat(result2).isInstanceOf(ValidationResult.Valid::class.java)
        
        // Retry first authority - should fail (replay)
        val result3 = useCase.invoke(
            authority = auth1,
            status = "OK",
            refId = "ref_1"
        )
        assertThat(result3).isInstanceOf(ValidationResult.AlreadyProcessed::class.java)
    }
    
    // ==================== Case Sensitivity Tests ====================
    
    @Test
    fun `status should be case-sensitive (OK not ok)`() = runTest {
        val result = useCase.invoke(
            authority = "auth_123",
            status = "ok",  // lowercase
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    // ==================== Authorization Validation ====================
    
    @Test
    fun `authority should follow expected format`() = runTest {
        val validAuthority = "auth_validated_123"
        
        coEvery {
            paymentRepository.verifyPayment(any())
        } returns VerificationResult.Success
        
        val result = useCase.invoke(
            authority = validAuthority,
            status = "OK",
            refId = "ref_123"
        )
        
        assertThat(result).isInstanceOf(ValidationResult.Valid::class.java)
    }
}

// Mock result classes
sealed class ValidationResult {
    object Valid : ValidationResult()
    object Invalid : ValidationResult()
    object AlreadyProcessed : ValidationResult()
    object ServerVerificationFailed : ValidationResult()
    data class Error(val message: String = "") : ValidationResult()
}

sealed class VerificationResult {
    object Success : VerificationResult()
    object Failed : VerificationResult()
}
