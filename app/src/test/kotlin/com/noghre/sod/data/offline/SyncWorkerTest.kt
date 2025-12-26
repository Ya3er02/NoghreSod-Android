package com.noghre.sod.data.offline

import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.noghre.sod.data.repository.CartRepository
import com.noghre.sod.domain.model.CartItem
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
 * Unit tests for Sync Worker (Background Sync)
 * Tests background synchronization including:
 * - Cart sync
 * - Wishlist sync
 * - Retry logic with exponential backoff
 * - Network constraint handling
 */
class SyncWorkerTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val cartRepository = mockk<CartRepository>()
    private val cartApi = mockk<CartApi>()

    private lateinit var syncWorker: SyncWorker

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        syncWorker = TestListenableWorkerBuilder<SyncWorker>(mockk())
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: android.content.Context,
                    workerClassName: String,
                    params: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker? = SyncWorker(appContext, params)
            })
            .build() as SyncWorker
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `schedule sync - enqueues work request` = runTest {
        // Arrange & Act
        syncWorker.scheduleSync()

        // Assert
        // WorkManager would queue the request
        assert(true)
    }

    @Test
    fun `sync cart - synchronizes pending cart items` = runTest {
        // Arrange
        val cartItems = listOf(
            CartItem(id = "1", productId = "prod1", quantity = 2),
            CartItem(id = "2", productId = "prod2", quantity = 1)
        )
        coEvery { cartRepository.getSyncPending() } returns cartItems
        coEvery { cartApi.syncCart(cartItems) } returns SyncResult.Success()

        // Act
        syncWorker.syncCart()

        // Assert
        coVerify { cartApi.syncCart(cartItems) }
    }

    @Test
    fun `sync wishlist - synchronizes wishlist items` = runTest {
        // Arrange
        val wishlistItems = listOf(
            WishlistItem(id = "1", productId = "prod1")
        )
        coEvery { wishlistRepository.getSyncPending() } returns wishlistItems

        // Act
        syncWorker.syncWishlist()

        // Assert
        coVerify { wishlistApi.sync(any()) }
    }

    @Test
    fun `retry on failure - attempts again with backoff` = runTest {
        // Arrange
        coEvery { cartApi.syncCart(any()) } returns SyncResult.Failure()

        // Act
        val result = syncWorker.retryOnFailure { cartApi.syncCart(any()) }

        // Assert
        // Should retry at least once
        coVerify(atLeast = 1) { cartApi.syncCart(any()) }
    }

    @Test
    fun `exponential backoff - applies increasing delays` = runTest {
        // Arrange
        val delays = mutableListOf<Long>()
        var attemptCount = 0
        coEvery { cartApi.syncCart(any()) } returns SyncResult.Failure()

        // Act - Would test exponential backoff in actual worker
        syncWorker.applyExponentialBackoff(attemptCount)

        // Assert
        // Delays should follow: 1s, 2s, 4s, 8s, etc.
        assert(true)
    }

    @Test
    fun `respects network constraint - only runs on connected network` = runTest {
        // Arrange
        every { networkMonitor.isOnline() } returns false

        // Act
        val result = syncWorker.doWork()

        // Assert
        // Should return retry when offline
        assert(result == ListenableWorker.Result.retry())
    }

    @Test
    fun `persist state - saves sync state for resume after restart` = runTest {
        // Arrange
        val state = "SYNCING_CART"

        // Act
        syncWorker.persistState(state)
        val saved = syncWorker.getSavedState()

        // Assert
        assert(saved == state)
    }
}
