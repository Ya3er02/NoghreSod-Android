package com.noghre.sod.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * RTL-aware Carousel/Pager component
 * Automatically reverses scroll direction for RTL
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RTLHorizontalPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(),
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    // For RTL, reverse the page content order
    HorizontalPager(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        reverseLayout = layoutDirection == LayoutDirection.Rtl,
        pageContent = { page ->
            // Reverse the page index for RTL
            val displayPage = when (layoutDirection) {
                LayoutDirection.Rtl -> (state.pageCount - 1 - page)
                LayoutDirection.Ltr -> page
            }
            pageContent(this, displayPage)
        }
    )
}

/**
 * RTL-aware Carousel with fixed dimensions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RTLCarouselItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    Box(
        modifier = modifier,
        content = { content() }
    )
}
