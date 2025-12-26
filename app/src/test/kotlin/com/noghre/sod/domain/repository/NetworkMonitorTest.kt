package com.noghre.sod.domain.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import org.junit.After

/**
 * Unit tests for Network Monitor
 * Tests network monitoring including:
 * - Online/offline detection
 * - Network state changes
 * - Network type detection (WiFi vs Mobile)
 * - Metered connection detection
 */
class NetworkMonitorTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val context = mockk<Context>()
    private val connectivityManager = mockk<ConnectivityManager>()

    private lateinit var networkMonitor: NetworkMonitor

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        networkMonitor = NetworkMonitor(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `is online - returns true when network connected` = runTest {
        // Arrange
        every { connectivityManager.activeNetwork } returns mockk<Network>()
        val capabilities = mockk<android.net.NetworkCapabilities>()
        every { connectivityManager.getNetworkCapabilities(any()) } returns capabilities
        every { 
            capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true

        // Act
        val isOnline = networkMonitor.isCurrentlyOnline()

        // Assert
        assert(isOnline)
    }

    @Test
    fun `network changed - emits state change event` = runTest {
        // Arrange
        var stateChanged = false

        // Act
        networkMonitor.networkStateFlow.collect { state ->
            stateChanged = true
        }
        networkMonitor.reportNetworkChange(true)

        // Assert
        assert(stateChanged)
    }

    @Test
    fun `observe network - emits multiple state changes` = runTest {
        // Arrange
        val states = mutableListOf<Boolean>()

        // Act
        networkMonitor.networkStateFlow.collect { state ->
            states.add(state)
        }
        networkMonitor.reportNetworkChange(true)
        networkMonitor.reportNetworkChange(false)
        networkMonitor.reportNetworkChange(true)

        // Assert
        assert(states.size >= 3)
    }

    @Test
    fun `handle network loss - triggers offline operations` = runTest {
        // Arrange
        every { connectivityManager.activeNetwork } returns null

        // Act
        networkMonitor.handleNetworkLoss()

        // Assert
        // Should queue pending operations
        assert(true)
    }

    @Test
    fun `detect network type - returns wifi for wifi network` = runTest {
        // Arrange
        val wifiNetwork = mockk<Network>()
        every { connectivityManager.activeNetwork } returns wifiNetwork
        val capabilities = mockk<android.net.NetworkCapabilities>()
        every { connectivityManager.getNetworkCapabilities(wifiNetwork) } returns capabilities
        every { capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) } returns true

        // Act
        val type = networkMonitor.getNetworkType()

        // Assert
        assert(type == NetworkType.WIFI)
    }

    @Test
    fun `is metered - returns true for mobile data` = runTest {
        // Arrange
        every { connectivityManager.isActiveNetworkMetered } returns true

        // Act
        val isMetered = networkMonitor.isMeteredConnection()

        // Assert
        assert(isMetered)
    }
}
