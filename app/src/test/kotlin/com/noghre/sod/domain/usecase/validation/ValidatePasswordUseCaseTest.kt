package com.noghre.sod.domain.usecase.validation

import com.noghre.sod.core.util.AppError
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class ValidatePasswordUseCaseTest {
    
    private lateinit var useCase: ValidatePasswordUseCase
    private lateinit var confirmationUseCase: ValidatePasswordConfirmationUseCase
    
    @Before
    fun setup() {
        useCase = ValidatePasswordUseCase()
        confirmationUseCase = ValidatePasswordConfirmationUseCase()
    }
    
    @Test
    fun `given empty password when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("")
        }
    }
    
    @Test
    fun `given blank password when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("   ")
        }
    }
    
    @Test
    fun `given password with less than 6 chars when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("12345")
        }
    }
    
    @Test
    fun `given valid password when validate then succeeds`() = runTest {
        // Should not throw
        useCase("ValidPass123")
    }
    
    @Test
    fun `given 6 character password when validate then succeeds`() = runTest {
        // Should not throw (minimum length)
        useCase("123456")
    }
    
    @Test
    fun `given very long password when validate then throws exception`() = runTest {
        val longPassword = "a".repeat(200)
        assertFailsWith<AppError.Validation> {
            useCase(longPassword)
        }
    }
    
    @Test
    fun `given matching passwords when validate confirmation then succeeds`() = runTest {
        val params = ValidatePasswordConfirmationUseCase.Params(
            password = "Password123",
            confirmPassword = "Password123"
        )
        // Should not throw
        confirmationUseCase(params)
    }
    
    @Test
    fun `given non matching passwords when validate confirmation then throws exception`() = runTest {
        val params = ValidatePasswordConfirmationUseCase.Params(
            password = "Password123",
            confirmPassword = "Password124"
        )
        assertFailsWith<AppError.Validation> {
            confirmationUseCase(params)
        }
    }
}
