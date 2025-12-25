package com.noghre.sod.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.data.local.database.AppDatabase
import com.noghre.sod.data.local.database.ALL_MIGRATIONS
import com.noghre.sod.data.local.database.validateDatabaseIntegrity
import com.noghre.sod.data.local.entity.ProductEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for Room database.
 * Tests migrations, queries, and data integrity.
 */
@RunWith(AndroidJUnit4::class)
class ProductDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setupDatabase() {
        context = ApplicationProvider.getApplicationContext()
        
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveProduct() = runTest {
        // Given
        val product = ProductEntity(
            id = "1",
            name = "Test Product",
            description = "Test Description",
            price = 100.0,
            discountPercentage = 10.0,
            image = "https://example.com/image.jpg",
            category = "Electronics",
            rating = 4.5,
            inStock = true,
            createdAt = "2025-01-01",
            lastUpdated = System.currentTimeMillis()
        )

        // When
        database.productDao().insertProduct(product)
        val retrievedProduct = database.productDao().getProductById("1")

        // Then
        assertNotNull(retrievedProduct)
        assertEquals(product.name, retrievedProduct?.name)
        assertEquals(product.price, retrievedProduct?.price)
    }

    @Test
    fun testInsertMultipleProductsAndList() = runTest {
        // Given
        val products = listOf(
            ProductEntity(
                id = "1",
                name = "Product 1",
                description = "Description 1",
                price = 100.0,
                discountPercentage = 0.0,
                image = "url1",
                category = "Electronics",
                rating = 4.0,
                inStock = true,
                createdAt = "2025-01-01",
                lastUpdated = System.currentTimeMillis()
            ),
            ProductEntity(
                id = "2",
                name = "Product 2",
                description = "Description 2",
                price = 200.0,
                discountPercentage = 10.0,
                image = "url2",
                category = "Jewelry",
                rating = 4.5,
                inStock = true,
                createdAt = "2025-01-02",
                lastUpdated = System.currentTimeMillis()
            )
        )

        // When
        products.forEach { database.productDao().insertProduct(it) }
        val allProducts = database.productDao().getAllProducts()

        // Then
        assertEquals(2, allProducts.size)
        assertEquals("Product 1", allProducts[0].name)
        assertEquals("Product 2", allProducts[1].name)
    }

    @Test
    fun testUpdateProduct() = runTest {
        // Given
        val product = ProductEntity(
            id = "1",
            name = "Original Name",
            description = "Description",
            price = 100.0,
            discountPercentage = 0.0,
            image = "url",
            category = "Electronics",
            rating = 4.0,
            inStock = true,
            createdAt = "2025-01-01",
            lastUpdated = System.currentTimeMillis()
        )

        // When
        database.productDao().insertProduct(product)
        val updatedProduct = product.copy(
            name = "Updated Name",
            price = 150.0
        )
        database.productDao().updateProduct(updatedProduct)
        val retrieved = database.productDao().getProductById("1")

        // Then
        assertNotNull(retrieved)
        assertEquals("Updated Name", retrieved?.name)
        assertEquals(150.0, retrieved?.price)
    }

    @Test
    fun testDeleteProduct() = runTest {
        // Given
        val product = ProductEntity(
            id = "1",
            name = "Product to Delete",
            description = "Description",
            price = 100.0,
            discountPercentage = 0.0,
            image = "url",
            category = "Electronics",
            rating = 4.0,
            inStock = true,
            createdAt = "2025-01-01",
            lastUpdated = System.currentTimeMillis()
        )

        // When
        database.productDao().insertProduct(product)
        database.productDao().deleteProduct(product)
        val retrieved = database.productDao().getProductById("1")

        // Then
        assertEquals(null, retrieved)
    }

    @Test
    fun testGetProductsByCategory() = runTest {
        // Given
        val products = listOf(
            ProductEntity(
                id = "1",
                name = "Electronics 1",
                description = "Desc",
                price = 100.0,
                discountPercentage = 0.0,
                image = "url",
                category = "Electronics",
                rating = 4.0,
                inStock = true,
                createdAt = "2025-01-01",
                lastUpdated = System.currentTimeMillis()
            ),
            ProductEntity(
                id = "2",
                name = "Jewelry 1",
                description = "Desc",
                price = 200.0,
                discountPercentage = 0.0,
                image = "url",
                category = "Jewelry",
                rating = 4.5,
                inStock = true,
                createdAt = "2025-01-02",
                lastUpdated = System.currentTimeMillis()
            )
        )

        // When
        products.forEach { database.productDao().insertProduct(it) }
        val electronicsProducts = database.productDao().getProductsByCategory("Electronics")

        // Then
        assertEquals(1, electronicsProducts.size)
        assertEquals("Electronics 1", electronicsProducts[0].name)
    }

    @Test
    fun testDatabaseMigrations() = runTest {
        // When
        database.openHelper.readableDatabase.apply {
            query("SELECT * FROM sqlite_master WHERE type='table'").use { cursor ->
                // Then
                assertTrue(cursor.count > 0)
            }
        }
        validateDatabaseIntegrity(database.openHelper.readableDatabase)
    }

    @Test
    fun testDatabaseIntegrity() = runTest {
        // Given
        val products = listOf(
            ProductEntity(
                id = "1",
                name = "Product 1",
                description = "Desc",
                price = 100.0,
                discountPercentage = 0.0,
                image = "url",
                category = "Electronics",
                rating = 4.0,
                inStock = true,
                createdAt = "2025-01-01",
                lastUpdated = System.currentTimeMillis()
            )
        )

        // When
        products.forEach { database.productDao().insertProduct(it) }
        validateDatabaseIntegrity(database.openHelper.readableDatabase)

        // Then - no exception thrown means integrity is good
        val allProducts = database.productDao().getAllProducts()
        assertEquals(1, allProducts.size)
    }
}
