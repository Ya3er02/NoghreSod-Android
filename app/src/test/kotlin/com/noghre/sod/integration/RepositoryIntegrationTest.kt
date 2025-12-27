package com.noghre.sod.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.data.local.database.ProductDatabase
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.remote.ProductApi
import com.noghre.sod.data.repository.ProductRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for repository layer
 * Tests interaction between database and API
 */
@RunWith(AndroidJUnit4::class)
class RepositoryIntegrationTest {

    private lateinit var mockDb: ProductDatabase
    private lateinit var mockApi: ProductApi
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        mockDb = mockk(relaxed = true)
        mockApi = mockk(relaxed = true)
        repository = ProductRepositoryImpl(mockDb, mockApi)
    }

    @Test
    fun repository_fetches_from_api_on_network_available() = runTest {
        // Arrange
        val mockProducts = listOf(
            ProductEntity(id = "1", name = "Ring", price = 500000, imageUrl = "url1"),
            ProductEntity(id = "2", name = "Necklace", price = 750000, imageUrl = "url2")
        )

        coEvery { mockApi.getProducts(any(), any(), any()) }
            .returns(mockk() {
                coEvery { products } returns mockProducts
            })

        // Act
        val result = repository.getProducts()

        // Assert
        verify { mockApi.getProducts(any(), any(), any()) }
        assert(result.isNotEmpty())
    }

    @Test
    fun repository_falls_back_to_cache_on_network_error() = runTest {
        // Arrange
        val cachedProducts = listOf(
            ProductEntity(id = "1", name = "Ring", price = 500000, imageUrl = "url1")
        )

        coEvery { mockApi.getProducts(any(), any(), any()) }
            .throws(Exception("Network error"))

        coEvery { mockDb.productDao().getAll() }
            .returns(cachedProducts)

        // Act
        val result = repository.getProducts()

        // Assert
        assert(result.isNotEmpty())
        assert(result.first().name == "Ring")
    }

    @Test
    fun repository_syncs_data_on_network_restore() = runTest {
        // Arrange
        val apiProducts = listOf(
            ProductEntity(id = "1", name = "Ring", price = 500000, imageUrl = "url1")
        )

        coEvery { mockApi.getProducts(any(), any(), any()) }
            .returns(mockk() { coEvery { products } returns apiProducts })

        // Act
        repository.syncProducts()

        // Assert
        verify { mockDb.productDao().insertAll(any()) }
    }

    @Test
    fun repository_queues_offline_operations() = runTest {
        // Arrange
        coEvery { mockApi.getProducts(any(), any(), any()) }
            .throws(Exception("Network error"))

        // Act
        repository.getProducts()

        // Assert - Operation should be queued
        verify { mockDb.offlineOperationDao().insert(any()) }
    }
}
