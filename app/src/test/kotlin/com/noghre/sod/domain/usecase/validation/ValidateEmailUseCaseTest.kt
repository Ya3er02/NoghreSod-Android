package com.noghre.sod.domain.usecase.validation

import com.noghre.sod.core.util.AppError
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ValidateEmailUseCaseTest {
    
    private lateinit var useCase: ValidateEmailUseCase
    
    @Before
    fun setup() {
        useCase = ValidateEmailUseCase()
    }
    
    @Test
    fun `given empty email when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("")
        }
    }
    
    @Test
    fun `given blank email when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("   ")
        }
    }
    
    @Test
    fun `given valid email when validate then succeeds`() = runTest {
        // Should not throw
        useCase("user@example.com")
    }
    
    @Test
    fun `given email without at symbol when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("userexample.com")
        }
    }
    
    @Test
    fun `given email without domain when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("user@")
        }
    }
    
    @Test
    fun `given email with multiple at symbols when validate then throws exception`() = runTest {
        assertFailsWith<AppError.Validation> {
            useCase("user@@example.com")
        }
    }
    
    @Test
    fun `given very long email when validate then throws exception`() = runTest {
        val longEmail = "a".repeat(250) + "@example.com"
        assertFailsWith<AppError.Validation> {
            useCase(longEmail)
        }
    }
    
    @Test
    fun `given valid persian email when validate then succeeds`() = runTest {
        useCase("user@example.ir")
    }
}
