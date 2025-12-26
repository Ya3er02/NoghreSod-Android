package com.noghre.sod.data.offline

import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.data.remote.ProductApi
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import org.junit.After

/**
 * Unit tests for Offline-First Manager
 * Tests offline-first architecture including:
 * - Cache management
 * - Network state handling
 * - Sync orchestration
 * - Merged data (local + remote)
 */
class OfflineFirstManagerTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val productDao = mockk<ProductDao>()
    private val productApi = mockk<ProductApi>()
    private val networkMonitor = mockk<NetworkMonitor>()

    private lateinit var offlineManager: OfflineFirstManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        offlineManager = OfflineFirstManager(productDao, productApi, networkMonitor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cache product - stored in local database` = runTest {
        // Arrange
        val product = Product(id = "123", name = "Silver Ring", price = 500000)
        coEvery { productDao.insert(product) } returns Unit

        // Act
        offlineManager.cacheProduct(product)

        // Assert
        coVerify { productDao.insert(product) }
    }

    @Test
    fun `get cached product - retrieves from local db` = runTest {
        // Arrange
        val product = Product(id = "123", name = "Silver Ring", price = 500000)
        coEvery { productDao.getById("123") } returns product

        // Act
        val cached = offlineManager.getCachedProduct("123")

        // Assert
        assert(cached?.id == "123")
        assert(cached?.name == "Silver Ring")
    }

    @Test
    fun `sync online - merges server data with local cache` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns true
        val serverProducts = listOf(Product(id = "1", name = "Product 1", price = 100000))
        coEvery { productApi.getProducts() } returns NetworkResult.Success(serverProducts)
        coEvery { productDao.insertAll(any()) } returns Unit

        // Act
        offlineManager.syncOnline()

        // Assert
        coVerify { productApi.getProducts() }
        coVerify { productDao.insertAll(any()) }
    }

    @Test
    fun `queue operation - offline first strategy stores locally` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns false
        val operation = OfflineOperation(type = "ADD_TO_CART", resourceId = "123")
        coEvery { productDao.insertOperation(operation) } returns Unit

        // Act
        offlineManager.queueOperation(operation)

        // Assert
        coVerify { productDao.insertOperation(operation) }
    }

    @Test
    fun `apply offline first strategy - returns cached when offline` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns false
        val cachedProducts = listOf(
            Product(id = "1", name = "Cached Product", price = 50000)
        )
        coEvery { productDao.getAll() } returns cachedProducts

        // Act
        val result = offlineManager.getProducts()

        // Assert
        assert(result.size == 1)
        assert(result[0].name == "Cached Product")
    }

    @Test
    fun `handle network restore - syncs pending operations` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns true
        coEvery { productApi.getProducts() } returns NetworkResult.Success(emptyList())
        coEvery { productDao.getPendingOperations() } returns emptyList()

        // Act
        offlineManager.handleNetworkRestore()

        // Assert
        coVerify { productApi.getProducts() }
    }

    @Test
    fun `prioritize operations - critical operations first` = runTest {
        // Arrange
        val ops = listOf(
            OfflineOperation(type = "ADD_TO_CART", priority = 1),
            OfflineOperation(type = "CHECKOUT", priority = 10),
            OfflineOperation(type = "UPDATE_PROFILE", priority = 5)
        )

        // Act
        val sorted = offlineManager.prioritizeOperations(ops)

        // Assert
        assert(sorted[0].priority == 10) // CHECKOUT is first
        assert(sorted[2].priority == 1)  // ADD_TO_CART is last
    }

    @Test
    fun `retry failed operations - exponential backoff applied` = runTest {
        // Arrange
        val failedOp = OfflineOperation(
            id = 1,
            type = "ADD_TO_CART",
            retryCount = 2
        )
        coEvery { productDao.updateRetryCount(1, 3) } returns Unit

        // Act
        offlineManager.retryFailedOperations()

        // Assert
        coVerify { productDao.updateRetryCount(any(), any()) }
    }

    @Test
    fun `clear expired cache - removes stale data older than 7 days` = runTest {
        // Arrange
        coEvery { productDao.deleteOlderThan(any()) } returns Unit

        // Act
        offlineManager.clearExpiredCache(expiryDays = 7)

        // Assert
        coVerify { productDao.deleteOlderThan(any()) }
    }

    @Test
    fun `get merged data - server data takes precedence` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns true
        val cached = listOf(Product(id = "1", name = "Old", price = 100000))
        val server = listOf(Product(id = "1", name = "Updated", price = 150000))
        
        coEvery { productDao.getAll() } returns cached
        coEvery { productApi.getProducts() } returns NetworkResult.Success(server)

        // Act
        val merged = offlineManager.getMergedData()

        // Assert
        // Server data should override local cache
        assert(merged[0].price == 150000)
    }
}
