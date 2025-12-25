package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.domain.model.Product
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositoryTests {

    private lateinit var mockApi: NoghreSodApi
    private lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        mockApi = mockk(relaxed = true)
        productRepository = ProductRepository(mockApi)
    }

    @Test
    fun testGetProductsSuccess() = runBlocking {
        // Arrange
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Silver Ring",
                price = 50.0,
                description = "Beautiful silver ring",
                category = "Rings",
                imageUrl = "https://example.com/ring.jpg",
                rating = 4.5f,
                reviewCount = 10,
                material = "Silver 925",
                weight = 5.0
            )
        )

        // Act
        val result = productRepository.getProducts(1, 0).first()

        // Assert
        result.fold(
            onSuccess = { products ->
                assertNotNull(products)
                assertEquals(1, products.size)
            },
            onFailure = {
                assertTrue(false, "Should not fail")
            }
        )
    }

    @Test
    fun testSearchProductsSuccess() = runBlocking {
        // Arrange
        val query = "ring"
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Silver Ring",
                price = 50.0,
                description = "Beautiful silver ring",
                category = "Rings",
                imageUrl = "https://example.com/ring.jpg",
                rating = 4.5f,
                reviewCount = 10,
                material = "Silver 925",
                weight = 5.0
            )
        )

        // Act
        val result = productRepository.searchProducts(query).first()

        // Assert
        result.fold(
            onSuccess = { products ->
                assertNotNull(products)
                assertTrue(products.isNotEmpty())
            },
            onFailure = {
                assertTrue(false, "Should not fail")
            }
        )
    }

    @Test
    fun testGetProductByIdSuccess() = runBlocking {
        // Arrange
        val productId = "1"
        val mockProduct = Product(
            id = "1",
            name = "Silver Ring",
            price = 50.0,
            description = "Beautiful silver ring",
            category = "Rings",
            imageUrl = "https://example.com/ring.jpg",
            rating = 4.5f,
            reviewCount = 10,
            material = "Silver 925",
            weight = 5.0
        )

        // Act
        val result = productRepository.getProductById(productId).first()

        // Assert
        result.fold(
            onSuccess = { product ->
                assertNotNull(product)
                assertEquals("1", product.id)
                assertEquals("Silver Ring", product.name)
            },
            onFailure = {
                assertTrue(false, "Should not fail")
            }
        )
    }

    @Test
    fun testGetProductsByCategory() = runBlocking {
        // Arrange
        val category = "Rings"
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Silver Ring",
                price = 50.0,
                description = "Beautiful silver ring",
                category = "Rings",
                imageUrl = "https://example.com/ring.jpg",
                rating = 4.5f,
                reviewCount = 10,
                material = "Silver 925",
                weight = 5.0
            )
        )

        // Act
        val result = productRepository.getProductsByCategory(category).first()

        // Assert
        result.fold(
            onSuccess = { products ->
                assertNotNull(products)
                assertTrue(products.all { it.category == category })
            },
            onFailure = {
                assertTrue(false, "Should not fail")
            }
        )
    }

    @Test
    fun testGetFeaturedProducts() = runBlocking {
        // Act
        val result = productRepository.getFeaturedProducts().first()

        // Assert
        result.fold(
            onSuccess = { products ->
                assertNotNull(products)
            },
            onFailure = {
                assertTrue(false, "Should not fail")
            }
        )
    }
}
