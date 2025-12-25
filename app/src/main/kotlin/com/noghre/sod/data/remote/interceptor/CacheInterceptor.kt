package com.noghre.sod.data.remote.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * OkHttp interceptor for intelligent response caching.
 * Applies cache headers based on request type and endpoint.
 */
class CacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // Apply cache control based on endpoint
        return when {
            // Cache product listings for 30 minutes
            originalRequest.url.encodedPath.contains("/products") -> {
                response.newBuilder()
                    .header("Cache-Control", "max-age=${30 * 60}")
                    .build()
                    .also {
                        Timber.d("Product list cached for 30 minutes")
                    }
            }
            // Cache featured products for 1 hour
            originalRequest.url.encodedPath.contains("/featured") -> {
                response.newBuilder()
                    .header("Cache-Control", "max-age=${60 * 60}")
                    .build()
                    .also {
                        Timber.d("Featured products cached for 1 hour")
                    }
            }
            // Cache categories for 2 hours
            originalRequest.url.encodedPath.contains("/categories") -> {
                response.newBuilder()
                    .header("Cache-Control", "max-age=${2 * 60 * 60}")
                    .build()
                    .also {
                        Timber.d("Categories cached for 2 hours")
                    }
            }
            // Don't cache user profile data (30 seconds)
            originalRequest.url.encodedPath.contains("/user/profile") -> {
                response.newBuilder()
                    .header("Cache-Control", "max-age=30")
                    .build()
                    .also {
                        Timber.d("User profile cached for 30 seconds")
                    }
            }
            // Don't cache cart and order data
            originalRequest.url.encodedPath.contains("/cart") ||
                    originalRequest.url.encodedPath.contains("/orders") -> {
                response.newBuilder()
                    .header("Cache-Control", "no-cache, no-store")
                    .build()
                    .also {
                        Timber.d("Cart/Order data not cached")
                    }
            }
            // Default: cache for 15 minutes
            else -> {
                response.newBuilder()
                    .header("Cache-Control", "max-age=${15 * 60}")
                    .build()
                    .also {
                        Timber.d("Response cached for 15 minutes")
                    }
            }
        }
    }
}

/**
 * HTTP request interceptor for adding cache control to requests.
 * Forces network or cache based on network availability.
 */
class RequestCacheInterceptor(private val isNetworkAvailable: () -> Boolean) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()

        val cacheControl = if (isNetworkAvailable()) {
            // Network available: prefer fresh data but allow cache if needed
            CacheControl.Builder()
                .maxAge(0, TimeUnit.SECONDS)
                .build()
        } else {
            // Network unavailable: allow stale cache
            CacheControl.Builder()
                .maxStale(30, TimeUnit.DAYS)
                .build()
        }

        return chain.proceed(
            requestBuilder.cacheControl(cacheControl).build()
        ).also {
            Timber.d("Request cache control applied: network=${isNetworkAvailable()}")
        }
    }
}
