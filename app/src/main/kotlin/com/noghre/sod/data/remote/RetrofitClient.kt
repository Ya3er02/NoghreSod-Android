package com.noghre.sod.data.remote

import android.content.Context
from retrofit2.Retrofit
from retrofit2.converter.gson.GsonConverterFactory
from com.squareup.okhttp3.OkHttpClient
from com.squareup.okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Factory for creating Retrofit instances.
 */
object RetrofitClient {

    private const val REQUEST_TIMEOUT = 30L

    fun createRetrofit(
        context: Context,
        baseUrl: String,
        isDebug: Boolean = false
    ): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)

        // Add logging interceptor for debug builds
        if (isDebug) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(loggingInterceptor)
        }

        // Add custom interceptors
        httpClient.addInterceptor(AuthInterceptor(context))
        httpClient.addInterceptor(ErrorHandlingInterceptor())

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
