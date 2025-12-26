package com.noghre.sod.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.theme.PersianFontFamily
import com.noghre.sod.presentation.theme.PersianFormatter

/**
 * Advanced search screen with intelligent features:
 * - Real-time autocomplete (2-3 characters)
 * - Fuzzy matching for typos
 * - Recent/trending searches
 * - Advanced filters (price, purity, weight)
 * - Product preview cards
 *
 * @author Yaser
 * @version 1.0.0
 */

data class SearchFilter(
    val searchQuery: String = "",
    val minPrice: Long = 0,
    val maxPrice: Long = Long.MAX_VALUE,
    val purity: String = "", // "750", "900", "925", "999"
    val minWeight: Double = 0.0,
    val maxWeight: Double = Double.MAX_VALUE,
    val inStock: Boolean? = null,
    val sortBy: SortOption = SortOption.RELEVANCE
)

enum class SortOption(val persianName: String) {
    RELEVANCE("مرتبط"),
    NEWEST(جدیدتر"),
    PRICE_LOW_TO_HIGH(قيمت: پایین به بالا"),
    PRICE_HIGH_TO_LOW(قيمت: بالا به پایین"),
    RATING(امتياز"),
    POPULARITY(پرطلبالترین")
}

@Composable
fun SearchAndFilterScreen(
    onSearchResultClick: (Product) -> Unit = {},
    onFilterChange: (SearchFilter) -> Unit = {},
    searchResults: List<Product> = emptyList(),
    recentSearches: List<String> = emptyList(),
    trendingSearches: List<String> = emptyList(),
    suggestionsLoading: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(SearchFilter()) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            // Trigger autocomplete search
            showSuggestions = true
            // TODO: Call ViewModel to get fuzzy-matched suggestions
        } else {
            showSuggestions = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with search bar
        SearchHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = { newQuery ->
                searchQuery = newQuery
                selectedFilter = selectedFilter.copy(searchQuery = newQuery)
                onFilterChange(selectedFilter)
            },
            onFilterClick = { showFilters = true },
            onClearClick = {
                searchQuery = ""
                selectedFilter = SearchFilter()
                onFilterChange(selectedFilter)
            },
            showSuggestions = showSuggestions
        )
        
        // Main content
        if (showFilters) {
            // Advanced filters panel
            AdvancedFiltersPanel(
                currentFilter = selectedFilter,
                onFilterChange = { newFilter ->
                    selectedFilter = newFilter
                    onFilterChange(newFilter)
                },
                onDismiss = { showFilters = false }
            )
        } else if (showSuggestions && searchQuery.isNotEmpty()) {
            // Autocomplete suggestions
            SuggestionsPanel(
                suggestions = suggestions,
                recentSearches = recentSearches.filter { it.contains(searchQuery, ignoreCase = true) },
                trendingSearches = trendingSearches.take(5),
                isLoading = suggestionsLoading,
                onSuggestionClick = { suggestion ->
                    searchQuery = suggestion
                    selectedFilter = selectedFilter.copy(searchQuery = suggestion)
                    onFilterChange(selectedFilter)
                    showSuggestions = false
                }
            )
        } else if (searchResults.isNotEmpty()) {
            // Search results
            SearchResultsPanel(
                results = searchResults,
                onResultClick = onSearchResultClick
            )
        } else if (searchQuery.isEmpty()) {
            // Initial state: recent + trending
            InitialSearchState(
                recentSearches = recentSearches,
                trendingSearches = trendingSearches,
                onRecentClick = { recent ->
                    searchQuery = recent
                    selectedFilter = selectedFilter.copy(searchQuery = recent)
                    onFilterChange(selectedFilter)
                },
                onTrendingClick = { trending ->
                    searchQuery = trending
                    selectedFilter = selectedFilter.copy(searchQuery = trending)
                    onFilterChange(selectedFilter)
                }
            )
        } else {
            // No results state
            NoResultsState(searchQuery = searchQuery)
        }
    }
}

// ==================== SEARCH HEADER ====================

@Composable
private fun SearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onClearClick: () -> Unit,
    showSuggestions: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Search input with icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(
                    text = "جستجو...",
                    fontFamily = PersianFontFamily
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "جستجو"
                )
            },
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearClick, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "لغو کردن"
                            )
                        }
                    }
                    IconButton(onClick = onFilterClick, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "فیلتر"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                fontFamily = PersianFontFamily,
                fontSize = 14.sp
            )
        )
        
        // Hints
        if (searchQuery.length in 1..1) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "بیشتر تایپ کنید...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = PersianFontFamily
            )
        }
    }
}

// ==================== SUGGESTIONS PANEL ====================

@Composable
private fun SuggestionsPanel(
    suggestions: List<String>,
    recentSearches: List<String>,
    trendingSearches: List<String>,
    isLoading: Boolean,
    onSuggestionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Autocomplete suggestions
        if (isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        } else if (suggestions.isNotEmpty()) {
            item {
                Text(
                    text = "پیشنهادها",
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = PersianFontFamily
                )
            }
            items(suggestions) { suggestion ->
                SuggestionItem(
                    text = suggestion,
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }
        
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                Text(
                    text = "جستجوهای اخیر",
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = PersianFontFamily
                )
            }
            items(recentSearches) { recent ->
                SuggestionItem(
                    text = recent,
                    onClick = { onSuggestionClick(recent) }
                )
            }
        }
        
        // Trending searches
        if (trendingSearches.isNotEmpty()) {
            item {
                Text(
                    text = "جستجوهای محبوب",
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = PersianFontFamily
                )
            }
            items(trendingSearches) { trending ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(trending) }
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trending,
                            fontFamily = PersianFontFamily,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = "محبوب"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            fontFamily = PersianFontFamily,
            fontSize = 14.sp
        )
    }
}

// ==================== SEARCH RESULTS PANEL ====================

@Composable
private fun SearchResultsPanel(
    results: List<Product>,
    onResultClick: (Product) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { product ->
            SearchResultItem(
                product = product,
                onClick = { onResultClick(product) }
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Surface(
                modifier = Modifier
                    .size(80.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                // TODO: Load product image
            }
            
            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    fontFamily = PersianFontFamily
                )
                Text(
                    text = PersianFormatter.formatCurrency(product.price.toLong()),
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = PersianFontFamily
                )
            }
        }
    }
}

// ==================== ADVANCED FILTERS PANEL ====================

@Composable
private fun AdvancedFiltersPanel(
    currentFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: Implement advanced filter panel
}

// ==================== INITIAL STATE ====================

@Composable
private fun InitialSearchState(
    recentSearches: List<String>,
    trendingSearches: List<String>,
    onRecentClick: (String) -> Unit,
    onTrendingClick: (String) -> Unit
) {
    // TODO: Implement initial state with recent and trending
}

// ==================== NO RESULTS STATE ====================

@Composable
private fun NoResultsState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "نتیجه ای برای 'شاخص:$searchQuery' نیست",
            fontSize = 14.sp,
            fontFamily = PersianFontFamily,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
