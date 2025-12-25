package com.noghre.sod.domain.usecase

import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.domain.model.AppException
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GetProductsUseCase using MockK.
 */
class GetProductsUseCaseTest {

    private lateinit var useCase: GetProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = GetProductsUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository returns products`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Product 1",
                price = 100.0,
                discountPercentage = 10.0,
                description = "Test product",
                image = "https://example.com/1.jpg",
                category = "Electronics",
                rating = 4.5,
                inStock = true,
                createdAt = "2025-01-01"
            )
        )

        coEvery { repository.getProducts(any(), any()) } returns mockProducts

        // When
        val result = useCase.invoke()

        // Then
        assertEquals(mockProducts, result)
        coVerify { repository.getProducts(1, 20) }
    }

    @Test
    fun `invoke with pagination parameters`() = runTest {
        // Given
        val mockProducts = listOf(
            Product.mock(id = "2", name = "Product 2")
        )
        val page = 2
        val pageSize = 10

        coEvery { repository.getProducts(page, pageSize) } returns mockProducts

        // When
        val result = useCase.invoke(page = page, pageSize = pageSize)

        // Then
        assertEquals(mockProducts, result)
        coVerify { repository.getProducts(page, pageSize) }
    }

    @Test
    fun `invoke returns empty list when no products found`() = runTest {
        // Given
        coEvery { repository.getProducts(any(), any()) } returns emptyList()

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result.isEmpty())
        coVerify { repository.getProducts(1, 20) }
    }

    @Test(expected = AppException::class)
    fun `invoke throws NetworkError when repository fails with network issue`() = runTest {
        // Given
        val networkError = AppException.NetworkError("Network failed", -1)
        coEvery { repository.getProducts(any(), any()) } throws networkError

        // When
        useCase.invoke()

        // Then - exception is thrown
    }

    @Test(expected = AppException::class)
    fun `invoke throws ServerError when repository returns server error`() = runTest {
        // Given
        val serverError = AppException.ServerError(500, "Internal Server Error")
        coEvery { repository.getProducts(any(), any()) } throws serverError

        // When
        useCase.invoke()

        // Then - exception is thrown
    }
}
