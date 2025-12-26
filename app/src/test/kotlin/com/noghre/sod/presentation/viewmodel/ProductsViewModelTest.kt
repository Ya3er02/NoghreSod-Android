package com.noghre.sod.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetProductsUseCase
import com.noghre.sod.domain.usecase.GetCategoriesUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Unit Tests for ProductsViewModel
 * 
 * مختبری شامل:
 * ✅ بارگذاری محصولات موفق
 * ✅ مدیریت خطای شبکه
 * ✅ debounce جستجو
 * ✅ انتخاب دسته‌بندی
 * ✅ مدیریت state و loading
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var getProductsUseCase: GetProductsUseCase

    @MockK
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    private lateinit var viewModel: ProductsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)

        viewModel = ProductsViewModel(
            getProductsUseCase = getProductsUseCase,
            getCategoriesUseCase = getCategoriesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== بارگذاری محصولات ==========

    @Test
    fun `بارگذاری محصولات - موفقیت‌آمیز`() = runTest(testDispatcher) {
        // Given - داده‌های mock شده
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "انگشتر نقره",
                price = 1500000.0,
                imageUrl = "https://example.com/ring.jpg",
                description = "انگشتر نقره اسلیمی",
                categoryId = "rings",
                stock = 10,
                isFavorite = false
            ),
            Product(
                id = "2",
                name = "گردنبند نقره",
                price = 2000000.0,
                imageUrl = "https://example.com/necklace.jpg",
                description = "گردنبند نقره ظریف",
                categoryId = "necklaces",
                stock = 5,
                isFavorite = false
            )
        )

        coEvery { getProductsUseCase(page = any(), categoryId = null) } returns mockProducts
        coEvery { getCategoriesUseCase() } returns emptyList()

        // When - فراخوانی loadProducts
        viewModel.loadProducts()
        advanceUntilIdle()

        // Then - بررسی state
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.products).hasSize(2)
        assertThat(state.products[0].name).isEqualTo("انگشتر نقره")
        assertThat(state.error).isNull()

        coVerify(exactly = 1) { getProductsUseCase(page = any(), categoryId = null) }
    }

    @Test
    fun `بارگذاری محصولات - خطای شبکه`() = runTest(testDispatcher) {
        // Given - خطای شبکه mock شده
        val networkError = IOException("اتصال اینترنتی قطع")
        coEvery { getProductsUseCase(page = any(), categoryId = null) } throws networkError

        // When
        viewModel.loadProducts()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNotNull()
        assertThat(state.products).isEmpty()
    }

    @Test
    fun `بارگذاری محصولات - خطای سرور (500)`() = runTest(testDispatcher) {
        // Given
        val serverError = Exception("خطای سرور: 500")
        coEvery { getProductsUseCase(page = any(), categoryId = null) } throws serverError

        // When
        viewModel.loadProducts()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNotNull()
    }

    @Test
    fun `بارگذاری محصولات - لیست خالی`() = runTest(testDispatcher) {
        // Given - بدون محصول
        coEvery { getProductsUseCase(page = any(), categoryId = null) } returns emptyList()

        // When
        viewModel.loadProducts()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.products).isEmpty()
        assertThat(state.isLoading).isFalse()
    }

    // ========== جستجو ==========

    @Test
    fun `جستجو - debounce صحیح`() = runTest(testDispatcher) {
        // Given
        val searchQuery = "انگشتر"
        val mockResults = listOf(
            Product(
                id = "1",
                name = "انگشتر نقره",
                price = 1500000.0,
                imageUrl = "",
                description = "",
                categoryId = "rings",
                stock = 10
            )
        )
        coEvery { getProductsUseCase(search = searchQuery) } returns mockResults

        // When - تایپ شروع
        viewModel.searchProducts(searchQuery)
        
        // 300ms منتظر بمان (debounce delay = 500ms)
        advanceTimeBy(300)

        // Then - جستجو شروع نشه
        coVerify(exactly = 0) { getProductsUseCase(search = any()) }

        // When - تا debounce تمام شه منتظر بمان
        advanceTimeBy(250)
        advanceUntilIdle()

        // Then - الان جستجو اجرا شده
        coVerify(atLeast = 1) { getProductsUseCase(search = searchQuery) }
    }

    @Test
    fun `جستجو - متن خالی`() = runTest(testDispatcher) {
        // When
        viewModel.searchProducts("")
        advanceUntilIdle()

        // Then - جستجو صورت نگیره
        coVerify(exactly = 0) { getProductsUseCase(search = any()) }
    }

    // ========== دسته‌بندی ==========

    @Test
    fun `انتخاب دسته‌بندی - بارگذاری صحیح`() = runTest(testDispatcher) {
        // Given
        val categoryId = "rings"
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "انگشتر 1",
                price = 1500000.0,
                imageUrl = "",
                description = "",
                categoryId = categoryId,
                stock = 10
            )
        )
        coEvery { getProductsUseCase(page = 1, categoryId = categoryId) } returns mockProducts

        // When
        viewModel.selectCategory(categoryId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.products).isNotEmpty()
        assertThat(state.selectedCategoryId).isEqualTo(categoryId)

        coVerify { getProductsUseCase(page = 1, categoryId = categoryId) }
    }

    @Test
    fun `انتخاب دسته‌بندی null - تمام محصولات`() = runTest(testDispatcher) {
        // Given
        val allProducts = listOf(
            Product("1", "انگشتر", 1500000.0, "", "", "rings", 10),
            Product("2", "گردنبند", 2000000.0, "", "", "necklaces", 5)
        )
        coEvery { getProductsUseCase(page = 1, categoryId = null) } returns allProducts

        // When
        viewModel.selectCategory(null)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.selectedCategoryId).isNull()
        assertThat(state.products).hasSize(2)
    }

    // ========== صفحه‌بندی ==========

    @Test
    fun `صفحه‌بندی - بارگذاری صفحه بعدی`() = runTest(testDispatcher) {
        // Given
        val page1 = listOf(
            Product("1", "محصول 1", 100.0, "", "", "cat1", 1),
            Product("2", "محصول 2", 100.0, "", "", "cat1", 1)
        )
        val page2 = listOf(
            Product("3", "محصول 3", 100.0, "", "", "cat1", 1)
        )

        coEvery { getProductsUseCase(page = 1, categoryId = null) } returns page1
        coEvery { getProductsUseCase(page = 2, categoryId = null) } returns page2

        // When - صفحه اول
        viewModel.loadProducts()
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertThat(state.products).hasSize(2)

        // When - صفحه بعدی
        viewModel.loadMoreProducts()
        advanceUntilIdle()

        // Then
        state = viewModel.uiState.value
        assertThat(state.products).hasSize(3)
    }
}
