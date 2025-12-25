# 🌐 Network Layer Security Guide - NoghreSod Android

**Last Updated**: 2025-12-25  
**Status**: ✅ Enterprise Production-Ready  
**Security Level**: 🔴 Critical / Enterprise  

---

## 📋 Table of Contents

1. [Certificate Pinning](#certificate-pinning)
2. [Token Management](#token-management)
3. [Error Handling](#error-handling)
4. [API Configuration](#api-configuration)
5. [Security Checklist](#security-checklist)

---

## 🔐 Certificate Pinning

### Problem ❌

**MITM (Man-In-The-Middle) Attack Scenario**:
```
📱 User on public WiFi
   ↓
🚨 Hacker intercepts traffic
   ↓
💳 Steals payment info, tokens, passwords
```

### Solution ✅

**Multi-Layer Certificate Pinning**:

#### 1. Network Security Config (XML)
```xml
<!-- app/src/main/res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.noghresod.ir</domain>
        
        <!-- Pin certificates -->
        <pin-set expiration="2026-12-31">
            <pin digest="SHA-256">AAAA...=</pin>  <!-- Primary -->
            <pin digest="SHA-256">BBBB...=</pin>  <!-- Backup -->
        </pin-set>
        
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </domain-config>
</network-security-config>
```

#### 2. CertificatePinningInterceptor (Code)
```kotlin
class CertificatePinningInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        
        // Verify certificate pins
        verifyCertificatePinning(response.handshake?.peerCertificates)
        
        return response
    }
}
```

#### 3. Extract Certificate Pins

```bash
# Get pin from production server
openssl s_client -servername api.noghresod.ir -connect api.noghresod.ir:443 | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  base64

# Output: sha256/AAAA...=
```

---

## 🔑 Token Management

### Problem ❌

**Plain-Text Token Storage**:
```kotlin
// ❌ DANGEROUS - tokens visible to root apps
private fun getAccessToken(): String {
    return sharedPreferences.getString("access_token", "")
}
```

### Solution ✅

#### 1. Encrypted Storage (AES-256-GCM)
```kotlin
class SecurePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setRequestStrongBoxBacked(true)  // Hardware-backed
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveAccessToken(token: String) {
        encryptedPrefs.edit().putString("access_token", token).apply()
    }
    
    fun getAccessToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }
}
```

#### 2. Thread-Safe Token Refresh
```kotlin
@Singleton
class TokenManager @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val authApi: AuthApiService
) {
    private val refreshLock = Mutex()
    
    suspend fun refreshTokenSynchronized(): String? {
        refreshLock.withLock {
            // Only one refresh at a time
            val refreshToken = securePreferences.getRefreshToken()
            
            val response = authApi.refreshToken(
                RefreshTokenRequestDto(refreshToken)
            )
            
            if (response.isSuccessful) {
                val tokens = response.body()!!.data
                securePreferences.saveAccessToken(tokens.accessToken)
                securePreferences.saveRefreshToken(tokens.refreshToken)
                return tokens.accessToken
            } else {
                logoutUser()
                return null
            }
        }
    }
}
```

#### 3. Automatic Token Attachment
```kotlin
class AuthInterceptor @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for public endpoints
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        // Attach token
        val token = securePreferences.getAccessToken()
        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        var response = chain.proceed(request)
        
        // Handle 401 - refresh token
        if (response.code == 401) {
            response.close()
            return handleTokenRefresh(chain, originalRequest)
        }
        
        return response
    }
}
```

---

## ⚠️ Error Handling

### Problem ❌

**Unhandled Network Errors**:
```kotlin
// ❌ Direct API call without error handling
try {
    val result = apiService.getProducts()
} catch (e: Exception) {
    // App crashes or unknown state
}
```

### Solution ✅

#### 1. NetworkResult Sealed Class
```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(
        val exception: Throwable,
        val errorType: ErrorType,
        val code: Int?,
        val message: String?
    ) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
    data class Empty(val message: String) : NetworkResult<Nothing>()
}
```

#### 2. Error Types
```kotlin
enum class ErrorType {
    NETWORK,           // No internet
    TIMEOUT,           // Request timeout
    UNAUTHORIZED,      // 401 - Invalid token
    FORBIDDEN,         // 403 - No permission
    NOT_FOUND,         // 404
    SERVER_ERROR,      // 5xx
    VALIDATION_ERROR,  // 422
    SSL_ERROR,         // Certificate issues
    UNKNOWN
}
```

#### 3. Safe API Call Wrapper
```kotlin
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> Response<ResponseDto<T>>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        
        if (response.isSuccessful && response.body()?.success == true) {
            NetworkResult.Success(response.body()!!.data)
        } else if (response.body()?.success == false) {
            NetworkResult.Error(
                exception = Exception(response.body()?.message),
                errorType = ErrorType.UNKNOWN,
                message = response.body()?.message
            )
        } else {
            NetworkResult.Error(...)
        }
    } catch (e: Exception) {
        NetworkErrorHandler.handleException(e)
    }
}
```

#### 4. Usage in Repository
```kotlin
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProducts(): NetworkResult<List<Product>> {
        return safeApiCall {
            apiService.getAllProducts()
        }
    }
}
```

#### 5. Usage in ViewModel
```kotlin
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    fun loadProducts() {
        viewModelScope.launch {
            when (val result = repository.getProducts()) {
                is NetworkResult.Success -> {
                    _products.value = result.data
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    // Log security events
                    if (result.errorType == ErrorType.UNAUTHORIZED) {
                        // Trigger logout
                    }
                }
                is NetworkResult.Loading -> {
                    _isLoading.value = true
                }
                is NetworkResult.Empty -> {
                    _emptyMessage.value = "No data found"
                }
            }
        }
    }
}
```

---

## 🔧 API Configuration

### Retrofit + OkHttp Setup

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        headerInterceptor: HeaderInterceptor,
        certificatePinningInterceptor: CertificatePinningInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Authentication
            .addInterceptor(authInterceptor)
            
            // Common headers
            .addInterceptor(headerInterceptor)
            
            // Certificate pinning
            .addNetworkInterceptor(certificatePinningInterceptor)
            .certificatePinner(certificatePinner)
            
            // Logging (debug only)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
            })
            
            // Timeouts
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

---

## ✅ Security Checklist

- ✅ Certificate pinning enabled (primary + backup)
- ✅ HTTPS only (no cleartext traffic)
- ✅ Token encryption (AES-256-GCM)
- ✅ Hardware-backed keys (StrongBox when available)
- ✅ Thread-safe token refresh
- ✅ Automatic 401 handling
- ✅ Comprehensive error handling
- ✅ Request/response logging (debug only)
- ✅ Timeout configuration (30s)
- ✅ SSL/TLS verification
- ✅ Auth interceptor for token injection
- ✅ Header interceptor for metadata
- ✅ Network security config XML
- ✅ Separate auth API (no auth loop)

---

## 🔗 References

1. [OkHttp Certificate Pinning](https://square.github.io/okhttp/https/)
2. [Android Network Security Config](https://developer.android.com/training/articles/security-config)
3. [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
4. [Retrofit Documentation](https://square.github.io/retrofit/)
5. [OWASP Mobile Security Testing Guide](https://owasp.org/www-project-mobile-security-testing-guide/)

---

**Status**: ✅ Production Ready  
**Last Review**: 2025-12-25  
**Next Review**: 2025-06-25
