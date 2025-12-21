package com.noghre.sod.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.local.CartDao
import com.noghre.sod.data.model.CartItem
import com.noghre.sod.data.remote.ApiService
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
class CartRepositoryIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var cartDao: CartDao
    private lateinit var database: AppDatabase
    private lateinit var repository: CartRepository
    private lateinit var mockApiService: ApiService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        cartDao = database.cartDao()
        mockApiService = mockk()
        repository = CartRepository(mockApiService, cartDao)
    }

    @Test
    fun testAddToCart() = runTest {
        // Arrange
        val cartItem = CartItem(productId = "1", quantity = 2)
        coEvery { mockApiService.addToCart(any()) } returns
            com.noghre.sod.data.dto.ApiResponse(success = true, data = "Item added")

        // Act
        repository.addToCart("1", 2)
        val cartItems = cartDao.getCartItems().first()

        // Assert
        assertThat(cartItems, hasSize(1))
        assertThat(cartItems[0].productId, equalTo("1"))
        assertThat(cartItems[0].quantity, equalTo(2))
    }

    @Test
    fun testRemoveFromCart() = runTest {
        // Arrange
        cartDao.addToCart(CartItem(productId = "1", quantity = 2))
        coEvery { mockApiService.removeFromCart("1") } returns
            com.noghre.sod.data.dto.ApiResponse(success = true, data = "Item removed")

        // Act
        repository.removeFromCart("1")
        val cartItems = cartDao.getCartItems().first()

        // Assert
        assertThat(cartItems, hasSize(0))
    }

    @Test
    fun testClearCart() = runTest {
        // Arrange
        cartDao.addToCart(CartItem(productId = "1", quantity = 2))
        cartDao.addToCart(CartItem(productId = "2", quantity = 1))
        coEvery { mockApiService.clearCart() } returns
            com.noghre.sod.data.dto.ApiResponse(success = true, data = "Cart cleared")

        // Act
        repository.clearCart()
        val cartItems = cartDao.getCartItems().first()

        // Assert
        assertThat(cartItems, hasSize(0))
    }

    @After
    fun tearDown() {
        database.close()
    }
}
