package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.CartDao
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CartRepositoryImplTest {
    
    private lateinit var repository: CartRepositoryImpl
    private val cartDao = mockk<CartDao>(relaxed = true)
    private val productDao = mockk<ProductDao>(relaxed = true)
    
    @Before
    fun setUp() {
        repository = CartRepositoryImpl(cartDao, productDao)
    }
    
    @Test
    fun getCartItems_success() = runTest {
        // Arrange
        val items = listOf(
            CartItem(
                id = "1",
                product = Product("p1", "Product 1", 100.0, "", "", false),
                quantity = 2
            )
        )
        coEvery { cartDao.getAll() } returns items
        
        // Act
        val result = repository.getCartItems()
        
        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data.size == 1)
    }
    
    @Test
    fun addToCart_success() = runTest {
        // Arrange
        val product = Product("p1", "Product 1", 100.0, "", "", false)
        coEvery { productDao.getProductById("p1") } returns product
        coEvery { cartDao.getByProductId("p1") } returns null
        
        // Act
        val result = repository.addToCart("p1", 2)
        
        // Assert
        assert(result is Result.Success)
        coVerify { cartDao.insert(any()) }
    }
    
    @Test
    fun addToCart_productNotFound() = runTest {
        // Arrange
        coEvery { productDao.getProductById("invalid") } returns null
        
        // Act
        val result = repository.addToCart("invalid", 1)
        
        // Assert
        assert(result is Result.Error)
    }
    
    @Test
    fun updateQuantity_success() = runTest {
        // Arrange
        val item = CartItem(
            id = "1",
            product = Product("p1", "Product 1", 100.0, "", "", false),
            quantity = 2
        )
        coEvery { cartDao.getById("1") } returns item
        
        // Act
        val result = repository.updateQuantity("1", 5)
        
        // Assert
        assert(result is Result.Success)
        coVerify { cartDao.update(any()) }
    }
    
    @Test
    fun clearCart_success() = runTest {
        // Act
        val result = repository.clearCart()
        
        // Assert
        assert(result is Result.Success)
        coVerify { cartDao.deleteAll() }
    }
    
    @Test
    fun getCartTotal_success() = runTest {
        // Arrange
        val items = listOf(
            CartItem(
                id = "1",
                product = Product("p1", "Product 1", 100.0, "", "", false),
                quantity = 2
            ),
            CartItem(
                id = "2",
                product = Product("p2", "Product 2", 50.0, "", "", false),
                quantity = 1
            )
        )
        coEvery { cartDao.getAll() } returns items
        
        // Act
        val result = repository.getCartTotal()
        
        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == 250.0)
    }
}
