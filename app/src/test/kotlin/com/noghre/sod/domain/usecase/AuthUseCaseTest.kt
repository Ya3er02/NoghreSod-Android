package com.noghre.sod.domain.usecase

import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.data.model.ErrorType
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.model.AuthToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import org.junit.After

/**
 * Unit tests for Authentication Use Case
 * Tests user authentication flow including:
 * - Login with valid/invalid credentials
 * - User registration
 * - Token refresh
 * - Logout functionality
 */
class AuthUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authService = mockk<AuthService>()
    private val tokenStorage = mockk<TokenStorage>()

    private lateinit var authUseCase: AuthUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authUseCase = AuthUseCase(authService, tokenStorage)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login user - successful with valid credentials` = runTest {
        // Arrange
        val email = "user@example.com"
        val password = "password123"
        val mockUser = User(id = "1", email = email, name = "Test User")
        coEvery { authService.login(email, password) } returns NetworkResult.Success(mockUser)

        // Act
        val result = authUseCase.login(email, password)

        // Assert
        assert(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assert(successResult.data.email == email)
    }

    @Test
    fun `login user - incorrect password returns unauthorized error` = runTest {
        // Arrange
        val email = "user@example.com"
        val wrongPassword = "wrongpassword"
        coEvery { authService.login(email, wrongPassword) } returns NetworkResult.Error(
            exception = Exception("Incorrect password"),
            errorType = ErrorType.UNAUTHORIZED
        )

        // Act
        val result = authUseCase.login(email, wrongPassword)

        // Assert
        assert(result is NetworkResult.Error)
        val errorResult = result as NetworkResult.Error
        assert(errorResult.errorType == ErrorType.UNAUTHORIZED)
    }

    @Test
    fun `register user - new user created successfully` = runTest {
        // Arrange
        val userData = User(id = "2", email = "newuser@example.com", name = "New User")
        coEvery { authService.register(userData) } returns NetworkResult.Success(userData)

        // Act
        val result = authUseCase.register(userData)

        // Assert
        assert(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assert(successResult.data.email == userData.email)
    }

    @Test
    fun `register user - email already registered returns validation error` = runTest {
        // Arrange
        val userData = User(id = "3", email = "existing@example.com", name = "User")
        coEvery { authService.register(userData) } returns NetworkResult.Error(
            exception = Exception("Email already exists"),
            errorType = ErrorType.VALIDATION_ERROR
        )

        // Act
        val result = authUseCase.register(userData)

        // Assert
        assert(result is NetworkResult.Error)
        val errorResult = result as NetworkResult.Error
        assert(errorResult.errorType == ErrorType.VALIDATION_ERROR)
    }

    @Test
    fun `refresh token - token successfully refreshed` = runTest {
        // Arrange
        val newToken = AuthToken(accessToken = "new_access", refreshToken = "new_refresh")
        coEvery { authService.refreshToken() } returns NetworkResult.Success(newToken)

        // Act
        val result = authUseCase.refreshToken()

        // Assert
        assert(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assert(successResult.data.accessToken == "new_access")
    }

    @Test
    fun `logout - session terminated and token cleared` = runTest {
        // Arrange
        coEvery { authService.logout() } returns NetworkResult.Success(Unit)
        coEvery { tokenStorage.clearTokens() } returns Unit

        // Act
        authUseCase.logout()

        // Assert
        coVerify { authService.logout() }
        coVerify { tokenStorage.clearTokens() }
    }
}
