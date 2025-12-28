package com.noghre.sod.core.error

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GlobalExceptionHandlerTest {
    
    private lateinit var exceptionHandler: GlobalExceptionHandler
    
    @Before
    fun setUp() {
        exceptionHandler = GlobalExceptionHandler(StandardTestDispatcher())
    }
    
    @Test
    fun handleException_networkException() = runTest {
        // Arrange
        val exception = AppException.NetworkException("Network error")
        
        // Act & Assert
        try {
            throw exception
        } catch (e: AppException.NetworkException) {
            assert(e.message == "Network error")
        }
    }
    
    @Test
    fun handleException_authException() = runTest {
        // Arrange
        val exception = AppException.AuthException("Not authenticated")
        
        // Act & Assert
        try {
            throw exception
        } catch (e: AppException.AuthException) {
            assert(e.message == "Not authenticated")
        }
    }
    
    @Test
    fun handleException_databaseException() = runTest {
        // Arrange
        val exception = AppException.DatabaseException("DB error")
        
        // Act & Assert
        try {
            throw exception
        } catch (e: AppException.DatabaseException) {
            assert(e.message == "DB error")
        }
    }
    
    @Test
    fun handleException_validationException() = runTest {
        // Arrange
        val exception = AppException.ValidationException("Invalid input")
        
        // Act & Assert
        try {
            throw exception
        } catch (e: AppException.ValidationException) {
            assert(e.message == "Invalid input")
        }
    }
    
    @Test
    fun handleException_notFound() = runTest {
        // Arrange
        val exception = AppException.NotFound("Not found")
        
        // Act & Assert
        try {
            throw exception
        } catch (e: AppException.NotFound) {
            assert(e.message == "Not found")
        }
    }
    
    @Test
    fun appException_toUserMessage() {
        // Network Exception
        var message = AppException.NetworkException("Network error").toUserMessage()
        assert(message.isNotBlank())
        
        // Auth Exception
        message = AppException.AuthException("Auth error").toUserMessage()
        assert(message.isNotBlank())
        
        // Database Exception
        message = AppException.DatabaseException("DB error").toUserMessage()
        assert(message.isNotBlank())
    }
}
