package com.example.foodcare.presentation.viewmodel

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = runBlocking {
            val token = userPreferences.getAccessToken()

            val originalRequest = chain.request()

            if (token.isNullOrEmpty()) {
                originalRequest
            } else {
                originalRequest
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            }
        }

        return chain.proceed(request)
    }
}
