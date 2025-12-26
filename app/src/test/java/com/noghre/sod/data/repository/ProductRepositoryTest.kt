package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.data.remote.SafeApiCall
import com.noghre.sod.data.remote.api.ProductService
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

/**
 * Unit tests for ProductRepository.
 *
 * Tests the following scenarios:
 * - Fetching products from API
 * - Getting products from cache (local database)
 * - Updating local cache when API succeeds
 * - Falling back to cache on network error
 * - Handling network timeouts
 * - Data transformation (DTO to Domain Model)
 *
 * @author Test Suite
 */
@ExperimentalCoroutinesApi
class ProductRepositoryTest {
    
    private lateinit var repository: ProductRepository
    private lateinit var productService: ProductService
    private lateinit var productDao: ProductDao
    
    // Sample test data
    private val sampleProductDto = ProductDto(
        id = "1",
        name = "Silver Ring",
        price = 150000f,
        image = "https://example.com/ring.jpg",
        category = "Rings",
        description = "Beautiful silver ring",
        stock = 10
    )
    
    private val sampleProductEntity = ProductEntity(
        id = "1",
        name = "Silver Ring",
        price = 150000f,
        image = "https://example.com/ring.jpg",
        category = "Rings",
        description = "Beautiful silver ring",
        stock = 10
    )
    
    private val sampleProduct = Product(
        id = "1",
        name = "Silver Ring",
        price = 150000f,
        image = "https://example.com/ring.jpg",
        category = "Rings",
        description = "Beautiful silver ring"
    )
    
    @Before
    fun setUp() {
        productService = mockk()
        productDao = mockk()
        
        repository = ProductRepositoryImpl(
            productService = productService,
            productDao = productDao
        )
    }
    
    @Test
    fun `getProducts should fetch from API and update cache`() = runTest {
        // Arrange
        val productDtos = listOf(sampleProductDto)
        coEvery { productService.getProducts() } returns productDtos
        coEvery { productDao.insertProducts(any()) } returns Unit
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        val resultList = when (result) {
            is NetworkResult.Success -> result.data
            else -> emptyList()
        }
        assertEquals(1, resultList.size)
        assertEquals("Silver Ring", resultList[0].name)
        coVerify { productService.getProducts() }
        coVerify { productDao.insertProducts(any()) }
    }
    
    @Test
    fun `getProducts should return cached data on network error`() = runTest {
        // Arrange
        coEvery { productService.getProducts() } throws Exception("Network error")
        coEvery { productDao.getAllProducts() } returns listOf(sampleProductEntity)
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        val resultList = when (result) {
            is NetworkResult.Success -> result.data
            else -> emptyList()
        }
        assertEquals(1, resultList.size)
        assertEquals("Silver Ring", resultList[0].name)
    }
    
    @Test
    fun `getProductById should fetch specific product`() = runTest {
        // Arrange
        coEvery { productService.getProductById("1") } returns sampleProductDto
        coEvery { productDao.getProductById("1") } returns sampleProductEntity
        
        // Act
        val result = repository.getProductById("1")
        
        // Assert
        assertIs<NetworkResult.Success<Product>>(result)
        val product = (result as NetworkResult.Success).data
        assertEquals("Silver Ring", product.name)
        coVerify { productService.getProductById("1") }
    }
    
    @Test
    fun `searchProducts should filter by query`() = runTest {
        // Arrange
        val searchResults = listOf(sampleProductDto)
        coEvery { productService.searchProducts("Ring") } returns searchResults
        
        // Act
        val result = repository.searchProducts("Ring")
        
        // Assert
        val resultList = when (result) {
            is NetworkResult.Success -> result.data
            else -> emptyList()
        }
        assertEquals(1, resultList.size)
        assertTrue(resultList[0].name.contains("Ring"))
    }
    
    @Test
    fun `getProductsByCategory should filter by category`() = runTest {
        // Arrange
        val categoryResults = listOf(sampleProductDto)
        coEvery { productService.getProductsByCategory("Rings") } returns categoryResults
        
        // Act
        val result = repository.getProductsByCategory("Rings")
        
        // Assert
        val resultList = when (result) {
            is NetworkResult.Success -> result.data
            else -> emptyList()
        }
        assertEquals(1, resultList.size)
        assertEquals("Rings", resultList[0].category)
    }
    
    @Test
    fun `getProducts should handle timeout error gracefully`() = runTest {
        // Arrange
        coEvery { productService.getProducts() } throws java.net.SocketTimeoutException("Timeout")
        coEvery { productDao.getAllProducts() } returns emptyList()
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        when (result) {
            is NetworkResult.Error -> {
                assertEquals(true, result.message.contains("زمان"))
            }
            else -> throw AssertionError("Should be Error state")
        }
    }
    
    @Test
    fun `getProducts should return error with specific code`() = runTest {
        // Arrange
        val httpException = retrofit2.HttpException(
            retrofit2.Response.error<List<ProductDto>>(
                404,
                okhttp3.ResponseBody.create(null, "")
            )
        )
        coEvery { productService.getProducts() } throws httpException
        coEvery { productDao.getAllProducts() } returns emptyList()
        
        // Act
        val result = repository.getProducts()
        
        // Assert
        when (result) {
            is NetworkResult.Error -> {
                assertEquals(404, result.code)
            }
            else -> throw AssertionError("Should be Error state")
        }
    }
    
    @Test
    fun `clearCache should delete all local products`() = runTest {
        // Arrange
        coEvery { productDao.deleteAllProducts() } returns Unit
        
        // Act
        repository.clearCache()
        
        // Assert
        coVerify { productDao.deleteAllProducts() }
    }
}
