package com.noghre.sod.domain.usecase

import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Currency
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GetProductsUseCaseTest {

    private val mockRepository = mockk<ProductRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var useCase: GetProductsUseCase

    @Before
    fun setup() {
        useCase = GetProductsUseCase(
            productRepository = mockRepository,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `getProducts returns success when repository succeeds`() = runTest {
        // Given
        val expected = createMockProducts(5)
        coEvery {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = null,
                sortBy = "popular"
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.success(expected))

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
        assertEquals(5, result.getOrNull()?.size)
    }

    @Test
    fun `getProducts returns failure when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = null,
                sortBy = "popular"
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.failure(exception))

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getProducts with search query calls repository with correct params`() = runTest {
        // Given
        val query = "silver jewelry"
        val expected = createMockProducts(1)
        coEvery {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = query,
                categoryId = null,
                sortBy = "popular"
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.success(expected))

        // When
        useCase(
            page = 1,
            pageSize = 20,
            query = query
        ).first()

        // Then
        coVerify {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = query,
                categoryId = null,
                sortBy = "popular"
            )
        }
    }

    @Test
    fun `getProducts with category filter calls repository with correct params`() = runTest {
        // Given
        val categoryId = "rings"
        val expected = createMockProducts(3)
        coEvery {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = categoryId,
                sortBy = "popular"
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.success(expected))

        // When
        useCase(
            categoryId = categoryId
        ).first()

        // Then
        coVerify {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = categoryId,
                sortBy = "popular"
            )
        }
    }

    @Test
    fun `getProducts with pagination works correctly`() = runTest {
        // Given
        val page = 2
        val pageSize = 50
        val expected = createMockProducts(10)
        coEvery {
            mockRepository.getProducts(
                page = page,
                pageSize = pageSize,
                query = null,
                categoryId = null,
                sortBy = "popular"
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.success(expected))

        // When
        val result = useCase(
            page = page,
            pageSize = pageSize
        ).first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull()?.size)
    }

    @Test
    fun `getProducts with sorting works correctly`() = runTest {
        // Given
        val sortBy = "price_low_to_high"
        val expected = createMockProducts(5)
        coEvery {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = null,
                sortBy = sortBy
            )
        } returns kotlinx.coroutines.flow.flowOf(Result.success(expected))

        // When
        val result = useCase(
            sortBy = sortBy
        ).first()

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            mockRepository.getProducts(
                page = 1,
                pageSize = 20,
                query = null,
                categoryId = null,
                sortBy = sortBy
            )
        }
    }

    private fun createMockProducts(count: Int): List<Product> {
        return (1..count).map { i ->
            Product(
                id = "product_$i",
                name = "Product $i",
                description = "Description for product $i",
                price = Product.Money(BigDecimal(100 * i), Currency.IRR),
                images = listOf(
                    Product.ImageUrl("https://example.com/image$i.jpg", isMain = true)
                ),
                category = Category(
                    id = "category_1",
                    name = "Rings"
                ),
                specifications = Product.ProductSpecifications(),
                availability = Product.StockStatus.Available(10),
                rating = Product.Rating(average = 4.5, count = 10),
                reviews = emptyList(),
                tags = listOf("silver", "jewelry"),
                isNew = true,
                isOnSale = false,
                createdAt = "2025-01-01T00:00:00",
                updatedAt = "2025-01-01T00:00:00"
            )
        }
    }
}