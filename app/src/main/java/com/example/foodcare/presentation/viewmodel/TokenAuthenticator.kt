package com.example.foodcare.presentation.viewmodel

import com.example.foodcare.api.RefreshApi
import com.example.foodcare.api.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val refreshApi: RefreshApi,
    private val userPreferences: UserPreferences
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {

        // если это уже refresh — выходим, чтобы не зациклиться
        if (response.request.url.encodedPath.contains("/refresh")) {
            runBlocking { userPreferences.clearTokens() }
            return null
        }

        // защита от бесконечного цикла
        if (responseCount(response) >= 2) return null

        return runBlocking {
            refreshMutex.withLock {

                val refreshToken = userPreferences.getRefreshToken()
                    ?: return@withLock null

                val refreshCall = refreshApi.refreshSync(
                    RefreshRequest(refresh_token = refreshToken)
                )

                val refreshResponse = refreshCall.execute()
                if (!refreshResponse.isSuccessful) {
                    userPreferences.clearTokens()
                    return@withLock null
                }

                val body = refreshResponse.body()
                    ?: return@withLock null

                val newAccess = body.access_token
                val newRefresh = body.refresh_token

                if (newAccess.isNullOrEmpty() || newRefresh.isNullOrEmpty()) {
                    userPreferences.clearTokens()
                    return@withLock null
                }

                userPreferences.saveTokens(newAccess, newRefresh)

                response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
