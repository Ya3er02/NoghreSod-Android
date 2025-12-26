package com.noghre.sod.data.offline

import com.noghre.sod.data.local.entity.OfflineOperationEntity
import com.noghre.sod.data.local.dao.OfflineOperationDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import org.junit.After

/**
 * Unit tests for Offline Operations
 * Tests offline operation queuing including:
 * - Queueing operations when offline
 * - Retrieving pending operations
 * - Removing completed operations
 * - Checking operation status
 */
class OfflineOperationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val offlineDao = mockk<OfflineOperationDao>()

    private lateinit var offlineManager: OfflineOperationManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        offlineManager = OfflineOperationManager(offlineDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `queue operation - operation added to database` = runTest {
        // Arrange
        val operation = OfflineOperationEntity(
            id = 1,
            type = "ADD_TO_CART",
            resourceId = "product123",
            payload = "{}" ,
            status = "PENDING"
        )
        coEvery { offlineDao.insert(operation) } returns Unit

        // Act
        offlineManager.queueOperation(operation)

        // Assert
        coVerify { offlineDao.insert(operation) }
    }

    @Test
    fun `get queued operations - returns all pending operations` = runTest {
        // Arrange
        val operations = listOf(
            OfflineOperationEntity(1, "ADD_TO_CART", "prod1", "{}", "PENDING"),
            OfflineOperationEntity(2, "REMOVE_FROM_CART", "prod2", "{}", "PENDING")
        )
        coEvery { offlineDao.getPending() } returns operations

        // Act
        val result = offlineManager.getQueuedOperations()

        // Assert
        assert(result.size == 2)
        assert(result[0].type == "ADD_TO_CART")
    }

    @Test
    fun `remove from queue - operation deleted from database` = runTest {
        // Arrange
        val operationId = 1
        coEvery { offlineDao.deleteById(operationId) } returns Unit

        // Act
        offlineManager.removeFromQueue(operationId)

        // Assert
        coVerify { offlineDao.deleteById(operationId) }
    }

    @Test
    fun `clear queue - all operations deleted` = runTest {
        // Arrange
        coEvery { offlineDao.deleteAll() } returns Unit

        // Act
        offlineManager.clearQueue()

        // Assert
        coVerify { offlineDao.deleteAll() }
    }

    @Test
    fun `is pending - returns true if operation exists` = runTest {
        // Arrange
        val operationId = 1
        coEvery { offlineDao.getById(operationId) } returns OfflineOperationEntity(
            id = 1, type = "ADD_TO_CART", resourceId = "prod1", payload = "{}", status = "PENDING"
        )

        // Act
        val result = offlineManager.isPending(operationId)

        // Assert
        assert(result)
    }

    @Test
    fun `get operation status - returns current status` = runTest {
        // Arrange
        val operationId = 1
        coEvery { offlineDao.getById(operationId) } returns OfflineOperationEntity(
            id = 1, type = "ADD_TO_CART", resourceId = "prod1", payload = "{}", status = "SYNCED"
        )

        // Act
        val result = offlineManager.getOperationStatus(operationId)

        // Assert
        assert(result == "SYNCED")
    }
}
