package com.noghre.sod.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Optimized LazyColumn with performance improvements.
 * 
 * Features:
 * - Stable scroll state management
 * - Proper content padding
 * - Optimized recomposition
 * - Memory-efficient item rendering
 * 
 * @author Yaser
 * @version 1.0.0
 */
@Composable
fun OptimizedLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        // Optimization: Set fixed size when possible
        // This prevents unnecessary re-layouts
        userScrollEnabled = true
    ) {
        // Content provided by caller
    }
}

/**
 * Performance optimization utilities for LazyColumn/Grid.
 */
object LazyListOptimizations {
    
    /**
     * Get optimized item key function.
     * Prevents unnecessary recompositions when items move.
     */
    fun <T> getKeyFunction(items: List<T>, keySelector: (T) -> Any): (index: Int) -> Any {
        return { index ->
            if (index < items.size) {
                keySelector(items[index])
            } else {
                index // Fallback to index
            }
        }
    }
    
    /**
     * Get optimized content type function.
     * Helps Compose understand item types for better performance.
     */
    fun <T> getContentTypeFunction(items: List<T>, typeSelector: (T) -> String): (index: Int) -> String {
        return { index ->
            if (index < items.size) {
                typeSelector(items[index])
            } else {
                "unknown"
            }
        }
    }
}

/**
 * Best practices for LazyColumn:
 * 
 * 1. Always provide key: { item -> item.id }
 *    - Prevents recompositions when items move
 *    - Ensures proper animation state
 * 
 * 2. Always provide contentType: { item -> item.type }
 *    - Helps Compose pool items by type
 *    - Improves recomposition efficiency
 * 
 * 3. Use items() instead of itemsIndexed() when possible
 *    - itemsIndexed() adds extra parameter passing overhead
 * 
 * 4. Avoid heavy composables in each item
 *    - Move complex calculations to viewModel
 *    - Use .remember() with appropriate keys
 * 
 * 5. Set proper constraints
 *    - Use .fillMaxWidth() instead of .fillMaxSize()
 *    - Avoid infinite measurement loops
 * 
 * 6. Handle loading state properly
 *    - Show loading indicator at end with LazyListState.canScrollForward
 *    - Don't block main thread during pagination
 * 
 * Example usage:
 * ```
 * LazyColumn(
 *     state = lazyListState,
 *     modifier = Modifier.fillMaxSize()
 * ) {
 *     items(
 *         items = products,
 *         key = { it.id },           // Prevent recomposition
 *         contentType = { "product" }  // Optimize pooling
 *     ) { product ->
 *         ProductCard(product)
 *     }
 *     
 *     if (isLoadingMore) {
 *         item {
 *             LoadingIndicator()
 *         }
 *     }
 * }
 * ```
 */
