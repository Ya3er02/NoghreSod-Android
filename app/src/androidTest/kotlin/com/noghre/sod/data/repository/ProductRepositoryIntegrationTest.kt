package com.noghre.sod.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.data.dto.ApiResponse
import com.noghre.sod.data.dto.PaginatedResponse
import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.model.Product
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.repository.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProductRepositoryIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var productDao: ProductDao
    private lateinit var database: AppDatabase
    private lateinit var repository: ProductRepository
    private lateinit var mockApiService: ApiService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        productDao = database.productDao()
        mockApiService = mockk()
        repository = ProductRepository(mockApiService, productDao)
    }

    @Test
    fun testInsertAndRetrieveProduct() = runTest {
        // Arrange
        val testProduct = Product(
            id = "1",
            name = "Test Product",
            description = "Test Description",
            price = 99.99,
            originalPrice = null,
            category = "Electronics",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            reviewCount = 10,
            inStock = true,
            sellerId = "seller-1"
        )

        // Act
        productDao.insertProduct(testProduct)
        val retrievedProduct = productDao.getProductById("1")

        // Assert
        assertThat(retrievedProduct, notNullValue())
        assertThat(retrievedProduct?.name, equalTo("Test Product"))
        assertThat(retrievedProduct?.price, equalTo(99.99))
        assertThat(retrievedProduct?.inStock, equalTo(true))
    }

    @Test
    fun testInsertMultipleProductsAndQuery() = runTest {
        // Arrange
        val products = listOf(
            Product("1", "Product 1", "Desc 1", 50.0, null, "Cat", "url", 4.5f, 10, true, "seller-1"),
            Product("2", "Product 2", "Desc 2", 100.0, null, "Cat", "url", 4.0f, 5, true, "seller-2"),
            Product("3", "Product 3", "Desc 3", 75.0, null, "Cat", "url", 3.5f, 2, true, "seller-3")
        )

        // Act
        for (product in products) {
            productDao.insertProduct(product)
        }
        val allProducts = productDao.getAllProducts().first()

        // Assert
        assertThat(allProducts, hasSize(3))
        assertThat(allProducts[0].name, equalTo("Product 1"))
        assertThat(allProducts[1].name, equalTo("Product 2"))
    }

    @Test
    fun testFetchProductsFromApiAndCache() = runTest {
        // Arrange
        val mockProducts = listOf(
            ProductDto("1", "Product 1", "Desc 1", 50.0, null, "Cat", "url", 4.5f, 10, true, "seller-1"),
            ProductDto("2", "Product 2", "Desc 2", 100.0, null, "Cat", "url", 4.0f, 5, true, "seller-2")
        )

        coEvery { mockApiService.getProducts(any(), any(), any()) } returns
            ApiResponse(
                success = true,
                data = PaginatedResponse(
                    items = mockProducts,
                    page = 1,
                    pageSize = 20,
                    totalItems = 2,
                    totalPages = 1,
                    hasNextPage = false
                )
            )

        // Act
        repository.fetchProducts(1).first { result ->
            // Assert
            assert(result is Result.Success)
            val data = (result as Result.Success).data
            assertThat(data, hasSize(2))
            assertThat(data[0].name, equalTo("Product 1"))
            true
        }

        // Verify cached
        val cachedProducts = productDao.getAllProducts().first()
        assertThat(cachedProducts, hasSize(2))
    }

    @Test
    fun testSearchProducts() = runTest {
        // Arrange
        val products = listOf(
            Product("1", "Samsung Phone", "Desc", 500.0, null, "Electronics", "url", 4.5f, 10, true, "seller-1"),
            Product("2", "Sony Phone", "Desc", 400.0, null, "Electronics", "url", 4.0f, 5, true, "seller-2"),
            Product("3", "Samsung Tablet", "Desc", 300.0, null, "Electronics", "url", 3.5f, 2, true, "seller-3")
        )
        products.forEach { productDao.insertProduct(it) }

        // Act
        val samsungProducts = productDao.searchProducts("Samsung").first()

        // Assert
        assertThat(samsungProducts, hasSize(2))
        assertThat(samsungProducts[0].name, equalTo("Samsung Phone"))
    }

    @After
    fun tearDown() {
        database.close()
    }
}
