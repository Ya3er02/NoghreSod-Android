package com.noghre.sod.domain.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.noghre.sod.data.local.SecurePreferences
import com.noghre.sod.data.remote.api.AuthApiService
import com.noghre.sod.data.remote.dto.request.RefreshTokenRequestDto
import com.noghre.sod.presentation.ui.auth.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Token Manager
 * 
 * Handles JWT token lifecycle:
 * - Token storage and retrieval
 * - Token refresh (thread-safe)
 * - Token expiration checking
 * - Automatic logout on token failure
 * 
 * Security features:
 * - Thread-safe refresh using Mutex
 * - Prevents multiple simultaneous refresh requests
 * - Automatic cleanup on failure
 * - Hardware-backed key storage via SecurePreferences
 * 
 * @since 1.0.0
 */
@Singleton
class TokenManager @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val authApi: AuthApiService,
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val TAG = "TokenManager"
    }
    
    /**
     * Mutex for thread-safe token refresh
     * Ensures only one refresh request at a time
     */
    private val refreshLock = Mutex()
    private var refreshInProgress = false
    
    /**
     * Thread-safe token refresh
     * 
     * Only one request can refresh at a time.
     * Other waiting requests will wait and use the refreshed token.
     * 
     * @return New access token or null if refresh fails
     */
    suspend fun refreshTokenSynchronized(): String? = withContext(Dispatchers.IO) {
        refreshLock.withLock {
            // If another thread is already refreshing, wait
            if (refreshInProgress) {
                Log.d(TAG, "Token refresh in progress, waiting...")
                // Wait a bit then return current token
                kotlinx.coroutines.delay(100)
                return@withLock securePreferences.getAccessToken()
            }
            
            refreshInProgress = true
            
            try {
                val refreshToken = securePreferences.getRefreshToken()
                
                if (refreshToken.isNullOrEmpty()) {
                    Log.w(TAG, "❌ Refresh token not found")
                    logoutUser()
                    return@withLock null
                }
                
                Log.d(TAG, "🔄 Attempting to refresh token...")
                
                // Call refresh endpoint
                val response = authApi.refreshToken(
                    RefreshTokenRequestDto(refreshToken)
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val tokens = response.body()!!.data
                    
                    // Save new tokens (encrypted)
                    securePreferences.saveAccessToken(tokens.accessToken)
                    securePreferences.saveRefreshToken(tokens.refreshToken)
                    
                    Log.d(TAG, "✅ Token refreshed successfully")
                    tokens.accessToken
                    
                } else {
                    // Refresh failed
                    Log.e(TAG, "❌ Token refresh failed: ${response.code()}")
                    logoutUser()
                    null
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "🚨 Token refresh error: ${e.message}", e)
                logoutUser()
                null
                
            } finally {
                refreshInProgress = false
            }
        }
    }
    
    /**
     * Check if access token is expired
     * 
     * Decodes JWT and checks expiration time.
     * 
     * @return true if token is expired or invalid
     */
    fun isTokenExpired(): Boolean {
        val token = securePreferences.getAccessToken() ?: return true
        
        return try {
            val jwt = JWT.decode(token)
            val expiresAt = jwt.expiresAt ?: return true
            
            // Check if expiration is before current time
            expiresAt.before(Date())
            
        } catch (e: JWTDecodeException) {
            Log.e(TAG, "Invalid JWT token: ${e.message}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking token expiration: ${e.message}")
            true
        }
    }
    
    /**
     * Get current access token
     * 
     * @return Access token or null
     */
    fun getAccessToken(): String? {
        return securePreferences.getAccessToken()
    }
    
    /**
     * Get current refresh token
     * 
     * @return Refresh token or null
     */
    fun getRefreshToken(): String? {
        return securePreferences.getRefreshToken()
    }
    
    /**
     * Check if user is authenticated
     * 
     * @return true if both tokens exist
     */
    fun isAuthenticated(): Boolean {
        return securePreferences.hasTokens()
    }
    
    /**
     * Get JWT claims (subject, email, etc.)
     * 
     * @return JWT claims or null if token invalid
     */
    fun getTokenClaims(): com.auth0.jwt.interfaces.Payload? {
        val token = securePreferences.getAccessToken() ?: return null
        
        return try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            Log.e(TAG, "Cannot decode JWT: ${e.message}")
            null
        }
    }
    
    /**
     * Logout user
     * 
     * - Clear all tokens
     * - Clear user data
     * - Navigate to login
     */
    fun logoutUser() {
        Log.i(TAG, "🚶 Logging out user...")
        
        // Clear all sensitive data
        securePreferences.clearAllSensitiveData()
        
        // Navigate to login activity
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
    
    /**
     * Clear tokens (without logging out user)
     * Used in token refresh failure scenarios
     */
    fun clearTokens() {
        Log.d(TAG, "Clearing tokens...")
        securePreferences.clearAuthTokens()
    }
    
    /**
     * Check if token needs refresh
     * Returns true if expiration is within 5 minutes
     */
    fun shouldRefreshToken(): Boolean {
        val token = securePreferences.getAccessToken() ?: return false
        
        return try {
            val jwt = JWT.decode(token)
            val expiresAt = jwt.expiresAt ?: return false
            
            // Check if expiration is within 5 minutes
            val fiveMinutesFromNow = Date(System.currentTimeMillis() + 5 * 60 * 1000)
            expiresAt.before(fiveMinutesFromNow)
            
        } catch (e: Exception) {
            false
        }
    }
}
