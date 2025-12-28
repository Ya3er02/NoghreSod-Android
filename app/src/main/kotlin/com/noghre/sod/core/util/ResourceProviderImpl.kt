package com.noghre.sod.core.util

import android.content.Context
import androidx.annotation.StringRes
import com.noghre.sod.domain.common.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ResourceProvider using Android Context.
 * Provides access to application resources from domain layer.
 * 
 * Injected as singleton via Hilt.
 */
@Singleton
class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
    
    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
    
    override fun getQuantityString(
        @StringRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String {
        return context.resources.getQuantityString(resId, quantity, *formatArgs)
    }
}
