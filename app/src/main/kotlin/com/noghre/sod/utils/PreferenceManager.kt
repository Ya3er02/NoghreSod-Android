package com.noghre.sod.utils

import android.content.Context
import android.content.SharedPreferences
import com.noghre.sod.utils.Constants.PREFERENCE_NAME
import com.noghre.sod.utils.Constants.AUTH_TOKEN_KEY
import com.noghre.sod.utils.Constants.USER_ID_KEY

/**
 * Manager for SharedPreferences operations.
 */
object PreferenceManager {

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(context: Context, token: String) {
        getPreferences(context).edit().putString(AUTH_TOKEN_KEY, token).apply()
    }

    fun getAuthToken(context: Context): String? {
        return getPreferences(context).getString(AUTH_TOKEN_KEY, null)
    }

    fun clearAuthToken(context: Context) {
        getPreferences(context).edit().remove(AUTH_TOKEN_KEY).apply()
    }

    fun saveUserId(context: Context, userId: String) {
        getPreferences(context).edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(USER_ID_KEY, null)
    }

    fun clearAll(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
