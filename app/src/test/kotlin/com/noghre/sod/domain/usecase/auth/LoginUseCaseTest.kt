package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.model.AuthResult
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.AuthRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `login with valid credentials returns AuthResult`() = runTest {
        val mockUser = User(id = "1", phone = "09123456789", fullName = "محمد")
        val mockResult = AuthResult(user = mockUser, accessToken = "token123", refreshToken = "refresh123")

        coEvery { authRepository.login("09123456789", "password123") } returns mockResult

        val result = loginUseCase(LoginUseCase.Params("09123456789", "password123"))

        assert(result.user.phone == "09123456789")
        assert(result.accessToken == "token123")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `login with invalid phone format throws exception`() = runTest {
        loginUseCase(LoginUseCase.Params("1234567890", "password123"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `login with short password throws exception`() = runTest {
        loginUseCase(LoginUseCase.Params("09123456789", "pass"))
    }
}
