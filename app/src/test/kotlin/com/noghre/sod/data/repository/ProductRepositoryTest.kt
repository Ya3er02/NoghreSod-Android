package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.data.remote.dto.ApiResponse
import com.noghre.sod.domain.model.Product
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * 🪨 Product Repository Unit Tests
 * 
 * Tests error handling, data transformation, and business logic.
 */
class ProductRepositoryTest {
    
    private lateinit var apiService: ApiService
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var repository: ProductRepositoryImpl
    
    @Before
    fun setup() {
        apiService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        repository = ProductRepositoryImpl(apiService, exceptionHandler)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    // ============================================
    // SUCCESS CASES
    // ============================================
    
    @Test
    fun `getProducts returns Success when API call succeeds`() = runTest {
        // Arrange
        val mockProducts = listOf(
            ProductDto(
                id = "1",
                name = "نقره خالص",
                price = 100000.0,
                description = "نقره خالص 925",
                images = listOf("image1.jpg"),
                rating = 4.5,
                categoryId = "cat1"
            )
        )
        
        val mockResponse = ApiResponse(
            success = true,
            data = mockProducts,
            message = "Success"
        )
        
        coEvery { apiService.getProducts() } returns mockResponse
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        assertIs<Result.Success<List<Product>>>(result)
        assertEquals(1, result.data.size)
        assertEquals("نقره خالص", result.data[0].name)
        
        coVerify(exactly = 1) { apiService.getProducts() }
    }
    
    @Test
    fun `searchProducts filters results correctly`() = runTest {
        // Arrange
        val query = "قلادی"
        val mockResponse = ApiResponse(
            success = true,
            data = listOf(
                ProductDto(
                    id = "1",
                    name = "براسلت قلادی",
                    price = 50000.0,
                    description = "براسلت",
                    images = emptyList(),
                    rating = 4.0,
                    categoryId = "cat2"
                )
            ),
            message = "Success"
        )
        
        coEvery { apiService.searchProducts(query) } returns mockResponse
        
        // Act
        val result = repository.searchProducts(query)
        
        // Assert
        assertIs<Result.Success<List<Product>>>(result)
        assertEquals(1, result.data.size)
        assertTrue(result.data[0].name.contains("قلادی"))
    }
    
    @Test
    fun `getProductById returns single product`() = runTest {
        // Arrange
        val productId = "123"
        val mockProduct = ProductDto(
            id = productId,
            name = "نقره",
            price = 150000.0,
            description = "نقره ւ کارات",
            images = listOf("img.jpg"),
            rating = 5.0,
            categoryId = "cat1"
        )
        
        val mockResponse = ApiResponse(
            success = true,
            data = mockProduct,
            message = "Success"
        )
        
        coEvery { apiService.getProductById(productId) } returns mockResponse
        
        // Act
        val result = repository.getProductById(productId)
        
        // Assert
        assertIs<Result.Success<Product>>(result)
        assertEquals(productId, result.data.id)
        assertEquals("نقره", result.data.name)
    }
    
    // ============================================
    // ERROR CASES
    // ============================================
    
    @Test
    fun `searchProducts returns Error for empty query`() = runTest {
        // Arrange
        val emptyQuery = ""
        
        // Act
        val result = repository.searchProducts(emptyQuery)
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Validation>(result.error)
        assertEquals("query", (result.error as AppError.Validation).field)
        
        coVerify(exactly = 0) { apiService.searchProducts(any()) }
    }
    
    @Test
    fun `getProductById returns Error for invalid ID`() = runTest {
        // Arrange
        val invalidId = ""
        
        // Act
        val result = repository.getProductById(invalidId)
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Validation>(result.error)
    }
    
    @Test
    fun `getProducts returns Error on network failure`() = runTest {
        // Arrange
        coEvery { apiService.getProducts() } throws java.net.UnknownHostException()
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Network>(result.error)
    }
    
    @Test
    fun `getProducts returns Error on timeout`() = runTest {
        // Arrange
        coEvery { apiService.getProducts() } throws java.net.SocketTimeoutException()
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Network>(result.error)
    }
    
    @Test
    fun `getProducts returns Error when API fails`() = runTest {
        // Arrange
        val mockResponse = ApiResponse<List<ProductDto>>(
            success = false,
            data = null,
            message = "Server error",
            code = 500
        )
        
        coEvery { apiService.getProducts() } returns mockResponse
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Network>(result.error)
        assertEquals(500, (result.error as AppError.Network).statusCode)
    }
    
    @Test
    fun `getProductById returns 404 error when not found`() = runTest {
        // Arrange
        val productId = "nonexistent"
        val mockResponse = ApiResponse<ProductDto>(
            success = false,
            data = null,
            message = "Not found",
            code = 404
        )
        
        coEvery { apiService.getProductById(productId) } returns mockResponse
        
        // Act
        val result = repository.getProductById(productId)
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Network>(result.error)
        assertEquals(404, (result.error as AppError.Network).statusCode)
    }
    
    // ============================================
    // CATEGORY FILTER TESTS
    // ============================================
    
    @Test
    fun `getProductsByCategory returns filtered products`() = runTest {
        // Arrange
        val categoryId = "jewelry"
        val mockResponse = ApiResponse(
            success = true,
            data = listOf(
                ProductDto(
                    id = "1",
                    name = "کوبالت",
                    price = 200000.0,
                    description = "کوبالت",
                    images = emptyList(),
                    rating = 4.2,
                    categoryId = categoryId
                )
            ),
            message = "Success"
        )
        
        coEvery { apiService.getProductsByCategory(categoryId) } returns mockResponse
        
        // Act
        val result = repository.getProductsByCategory(categoryId)
        
        // Assert
        assertIs<Result.Success<List<Product>>>(result)
        assertEquals(1, result.data.size)
        assertEquals(categoryId, result.data[0].categoryId)
    }
    
    @Test
    fun `getProductsByCategory returns Error for invalid category`() = runTest {
        // Arrange
        val invalidCategory = ""
        
        // Act
        val result = repository.getProductsByCategory(invalidCategory)
        
        // Assert
        assertIs<Result.Error>(result)
        assertIs<AppError.Validation>(result.error)
    }
    
    // ============================================
    // INTEGRATION TESTS
    // ============================================
    
    @Test
    fun `getAllOperations work correctly in sequence`() = runTest {
        // Arrange
        val mockResponse = ApiResponse(
            success = true,
            data = listOf(
                ProductDto(
                    id = "1",
                    name = "نقره",
                    price = 100000.0,
                    description = "نقره خالص",
                    images = emptyList(),
                    rating = 4.5,
                    categoryId = "cat1"
                )
            ),
            message = "Success"
        )
        
        coEvery { apiService.getProducts() } returns mockResponse
        coEvery { apiService.searchProducts("test") } returns mockResponse
        
        // Act - Execute multiple operations
        val result1 = repository.getProducts()
        val result2 = repository.searchProducts("test")
        
        // Assert - Both succeed
        assertIs<Result.Success<List<Product>>>(result1)
        assertIs<Result.Success<List<Product>>>(result2)
        assertEquals(1, result1.data.size)
        assertEquals(1, result2.data.size)
        
        // Verify calls
        coVerify(exactly = 1) { apiService.getProducts() }
        coVerify(exactly = 1) { apiService.searchProducts("test") }
    }
}
