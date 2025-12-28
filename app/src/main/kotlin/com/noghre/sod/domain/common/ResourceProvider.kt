package com.noghre.sod.domain.common

import androidx.annotation.StringRes

/**
 * Interface for accessing application resources from domain layer.
 * Allows UseCases to access string resources for error messages
 * without depending on Android context directly.
 * 
 * This is injected via Hilt and implemented in core/util/ResourceProviderImpl.kt
 */
interface ResourceProvider {
    
    /**
     * Gets a string resource by ID.
     * 
     * @param resId String resource ID
     * @return Localized string value
     */
    fun getString(@StringRes resId: Int): String
    
    /**
     * Gets a string resource by ID with arguments.
     * 
     * @param resId String resource ID with format specifiers (%1$s, %2$d, etc.)
     * @param args Arguments to substitute in the string
     * @return Formatted localized string
     */
    fun getString(@StringRes resId: Int, vararg args: Any): String
    
    /**
     * Gets plural string resource.
     * 
     * @param resId Plural string resource ID
     * @param quantity Quantity to determine singular/plural
     * @param formatArgs Arguments for formatting
     * @return Localized plural string
     */
    fun getQuantityString(
        @StringRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String
}
