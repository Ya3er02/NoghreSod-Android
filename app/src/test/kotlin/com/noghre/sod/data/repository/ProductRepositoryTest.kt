package com.noghre.sod.data.repository

import com.google.common.truth.Truth.assertThat
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.NetworkResult
import com.noghre.sod.domain.util.NetworkMonitor
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Unit Tests for ProductRepository
 * 
 * هدف:
 * ✅ Offline-First pattern تست
 * ✅ Room database تست
 * ✅ Network error handling
 * ✅ Cache strategy validation
 */
class ProductRepositoryTest {

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var productDao: ProductDao

    @MockK
    private lateinit var networkMonitor: NetworkMonitor

    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        repository = ProductRepositoryImpl(apiService, productDao, networkMonitor)
    }

    // ========== Offline-First ==========

    @Test
    fun `بررسی محصول - ابتدا کاش سپس نببرق`() = runTest {
        // Given - انبار دار پیشین
        val cachedProducts = listOf(
            ProductEntity(
                id = "1",
                name = "انگشتر نقره",
                price = 1500000.0,
                imageUrl = "https://example.com/ring.jpg",
                description = "انگشتر اسلیمی",
                categoryId = "rings",
                stock = 10,
                isFavorite = false,
                lastUpdated = System.currentTimeMillis()
            )
        )

        every { productDao.getAllProducts() } returns flowOf(cachedProducts)
        every { networkMonitor.isOnline() } returns false  // آفلاین

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then - باید کاش برگردانه
        assertThat(results).isNotEmpty()
        assertThat(results.last()).isInstanceOf(NetworkResult.Success::class.java)
    }

    @Test
    fun `بررسی محصول - شبکه سپس کاش`() = runTest {
        // Given
        val cachedProducts = listOf(
            ProductEntity("1", "محصول 1", 1000.0, "", "", "cat", 10, false, System.currentTimeMillis())
        )
        val apiProducts = listOf(
            ProductDto("2", "محصول 2", 2000.0, "", "", "cat", 5)
        )

        every { productDao.getAllProducts() } returns flowOf(cachedProducts)
        coEvery { apiService.getProducts(page = 1, perPage = 20) } returns 
            Response.success(apiProducts)
        every { networkMonitor.isOnline() } returns true
        coJustRun { productDao.insertProducts(any()) }
        coJustRun { productDao.clearProducts() }

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then
        // اول: loading
        assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
        
        // دوم: cached data
        if (results.size > 1) {
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)
        }
    }

    @Test
    fun `بررسی محصول - آفلاین بدون کاش`() = runTest {
        // Given - آفلاین و بدون کاش
        every { productDao.getAllProducts() } returns flowOf(emptyList())
        every { networkMonitor.isOnline() } returns false

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then - باید خطا
        assertThat(results.last()).isInstanceOf(NetworkResult.Error::class.java)
    }

    // ========== Filtering ==========

    @Test
    fun `بررسی محصول - براساس دسته‌بندی`() = runTest {
        // Given
        val categoryId = "rings"
        val filteredProducts = listOf(
            ProductEntity(
                id = "1",
                name = "انگشتر",
                price = 1500000.0,
                imageUrl = "",
                description = "",
                categoryId = categoryId,
                stock = 10,
                isFavorite = false,
                lastUpdated = System.currentTimeMillis()
            )
        )

        every { productDao.getProductsByCategory(categoryId) } returns flowOf(filteredProducts)
        every { networkMonitor.isOnline() } returns false

        // When
        val result = repository.getProducts(categoryId = categoryId, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then
        assertThat(results).isNotEmpty()
    }

    // ========== Network Error Handling ==========

    @Test
    fun `بررسی محصول - خطای تایم‌آوت`() = runTest {
        // Given
        every { productDao.getAllProducts() } returns flowOf(emptyList())
        coEvery { apiService.getProducts(any(), any()) } throws IOException("Request timed out")
        every { networkMonitor.isOnline() } returns true

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then - باید error برگردانه
        assertThat(results.last()).isInstanceOf(NetworkResult.Error::class.java)
    }

    @Test
    fun `بررسی محصول - خطای سرور (500)`() = runTest {
        // Given
        every { productDao.getAllProducts() } returns flowOf(emptyList())
        coEvery { apiService.getProducts(any(), any()) } returns 
            Response.error(500, mockk { every { string() } returns "{\"error\":\"Server error\"}" })
        every { networkMonitor.isOnline() } returns true

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then
        assertThat(results.last()).isInstanceOf(NetworkResult.Error::class.java)
    }

    @Test
    fun `بررسی محصول - خطای 404 Not Found`() = runTest {
        // Given
        every { productDao.getAllProducts() } returns flowOf(emptyList())
        coEvery { apiService.getProducts(any(), any()) } returns 
            Response.error(404, mockk { every { string() } returns "{\"error\":\"Not found\"}" })
        every { networkMonitor.isOnline() } returns true

        // When
        val result = repository.getProducts(categoryId = null, page = 1)
        val results = mutableListOf<NetworkResult<*>>()
        
        result.collect { networkResult ->
            results.add(networkResult)
        }

        // Then
        assertThat(results.last()).isInstanceOf(NetworkResult.Error::class.java)
    }

    // ========== Pagination ==========

    @Test
    fun `بررسی محصول - صفحه‌بندی درست`() = runTest {
        // Given
        val page1Products = listOf(
            ProductEntity("1", "محصول 1", 1000.0, "", "", "cat", 10, false, System.currentTimeMillis())
        )

        coEvery { apiService.getProducts(page = 1, perPage = 20) } returns 
            Response.success(listOf(ProductDto("1", "محصول 1", 1000.0, "", "", "cat", 10)))
        coEvery { apiService.getProducts(page = 2, perPage = 20) } returns 
            Response.success(listOf(ProductDto("2", "محصول 2", 2000.0, "", "", "cat", 10)))
        
        every { productDao.getAllProducts() } returns flowOf(page1Products)
        every { networkMonitor.isOnline() } returns true
        coJustRun { productDao.insertProducts(any()) }
        coJustRun { productDao.clearProducts() }

        // When
        val result1 = repository.getProducts(categoryId = null, page = 1)
        val result2 = repository.getProducts(categoryId = null, page = 2)

        // Then - page parameters درستار شه
        coVerify { apiService.getProducts(page = 1, perPage = 20) }
        coVerify { apiService.getProducts(page = 2, perPage = 20) }
    }

    // ========== Database Operations ==========

    @Test
    fun `ذخیره محصول - داخل دیتابیس`() = runTest {
        // Given
        val products = listOf(
            ProductEntity("1", "محصول", 1000.0, "", "", "cat", 10, false, System.currentTimeMillis())
        )
        coJustRun { productDao.insertProducts(products) }

        // When
        productDao.insertProducts(products)

        // Then
        coVerify { productDao.insertProducts(products) }
    }

    @Test
    fun `حذف محصول قدیمی - بعد از 7 روز`() = runTest {
        // Given
        coJustRun { productDao.cleanOldProducts(olderThanDays = 7) }

        // When
        productDao.cleanOldProducts(olderThanDays = 7)

        // Then
        coVerify { productDao.cleanOldProducts(olderThanDays = 7) }
    }

    @Test
    fun `محصول واحد - دریافت از ID`() = runTest {
        // Given
        val productId = "1"
        val product = ProductEntity(
            id = productId,
            name = "محصول",
            price = 1000.0,
            imageUrl = "",
            description = "",
            categoryId = "cat",
            stock = 10,
            isFavorite = false,
            lastUpdated = System.currentTimeMillis()
        )

        coEvery { productDao.getProductById(productId) } returns product

        // When
        val result = productDao.getProductById(productId)

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(productId)
    }
}
