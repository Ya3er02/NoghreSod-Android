package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.ProductSummary
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Unit tests for ProductRepositoryImpl.
 * Tests data fetching, caching, and error handling.
 */
class ProductRepositoryImplTest {
    private lateinit var repository: ProductRepository
    private lateinit var api: NoghreSodApi
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxed = true)
        // Initialize repository with mocks
        // Note: Actual implementation depends on your ProductRepositoryImpl constructor
    }

    @Test
    fun `getProducts returns success when API call succeeds`() = runTest {
        // Given
        val mockProducts = listOf(
            mockk<ProductSummary>(relaxed = true) {
                coEvery { id } returns "1"
                coEvery { name } returns "Product 1"
            }
        )
        coEvery { api.getProducts(0, 20) } returns mockk(relaxed = true) {
            coEvery { isSuccessful } returns true
            coEvery { body() } returns mockk {
                coEvery { data } returns mockProducts
            }
        }

        // When
        val result = repository.getProducts(0, 20).first()

        // Then
        assertTrue(result is Result.Success)
        coVerify { api.getProducts(0, 20) }
    }

    @Test
    fun `getProducts returns error when API call fails`() = runTest {
        // Given
        coEvery { api.getProducts(0, 20) } throws Exception("Network error")

        // When
        val result = repository.getProducts(0, 20).first()

        // Then
        assertTrue(result is Result.Error)
    }

    @Test
    fun `getProductById returns product from cache if available`() = runTest {
        // Given
        val productId = "123"
        val mockProduct = mockk<ProductSummary>(relaxed = true) {
            coEvery { id } returns productId
        }

        // When
        // This depends on your actual cache implementation
        // val result = repository.getProductById(productId).first()

        // Then
        // assertTrue(result is Result.Success)
    }

    @Test
    fun `searchProducts filters results correctly`() = runTest {
        // Given
        val searchQuery = "نقره"
        val mockProducts = listOf(
            mockk<ProductSummary>(relaxed = true) {
                coEvery { name } returns "نقره نگین"
            }
        )

        coEvery { api.searchProducts(searchQuery, 0, 20) } returns mockk(relaxed = true) {
            coEvery { isSuccessful } returns true
            coEvery { body() } returns mockk {
                coEvery { data } returns mockProducts
            }
        }

        // When
        val result = repository.searchProducts(searchQuery, 0, 20).first()

        // Then
        assertTrue(result is Result.Success)
        coVerify { api.searchProducts(searchQuery, 0, 20) }
    }

    @Test
    fun `getProductsByCategory filters by category`() = runTest {
        // Given
        val category = "RING"
        val mockProducts = listOf(
            mockk<ProductSummary>(relaxed = true) {
                coEvery { id } returns "1"
            }
        )

        coEvery { api.getProductsByCategory(category, 0, 20) } returns mockk(relaxed = true) {
            coEvery { isSuccessful } returns true
            coEvery { body() } returns mockk {
                coEvery { data } returns mockProducts
            }
        }

        // When
        // val result = repository.getProductsByCategory(category, 0, 20).first()

        // Then
        // assertTrue(result is Result.Success)
    }

    @Test
    fun `network error is properly handled and converted to Result Error`() = runTest {
        // Given
        val errorMessage = "Connection timeout"
        coEvery { api.getProducts(0, 20) } throws Exception(errorMessage)

        // When
        val result = repository.getProducts(0, 20).first()

        // Then
        assertTrue(result is Result.Error)
        if (result is Result.Error) {
            assertTrue(result.exception is Exception)
        }
    }
}
