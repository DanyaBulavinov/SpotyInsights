package com.daniel.spotyinsights.auth.interceptor

import com.daniel.spotyinsights.auth.data.AuthDataStore
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepository
import com.daniel.spotyinsights.domain.model.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val authRepository: SpotifyAuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth header for token endpoints
        if (originalRequest.url.encodedPath.contains("/api/token")) {
            return chain.proceed(originalRequest)
        }

        // Check if token is expired before making the request
        val isExpired = runBlocking { authDataStore.isTokenExpired() }
        if (isExpired) {
            val refreshToken = runBlocking { authDataStore.refreshToken.first() }
            if (refreshToken != null) {
                val refreshResult = runBlocking {
                    authRepository.refreshAccessToken(refreshToken)
                }

                when (refreshResult) {
                    is Result.Success -> {
                        runBlocking {
                            authDataStore.saveTokens(
                                refreshResult.data.accessToken,
                                refreshResult.data.refreshToken ?: refreshToken,
                                refreshResult.data.expiresIn
                            )
                        }
                    }
                    is Result.Error -> {
                        runBlocking { authDataStore.clearTokens() }
                        // Let the request proceed without token, it will fail with 401
                    }
                }
            }
        }

        val accessToken = runBlocking { authDataStore.accessToken.first() }
        val response = chain.proceed(addAuthHeader(originalRequest, accessToken))

        // Handle unexpected 401 errors (should be rare now with proactive refresh)
        if (response.code == 401) {
            response.close()
            
            val refreshToken = runBlocking { authDataStore.refreshToken.first() }
            if (refreshToken != null) {
                val refreshResult = runBlocking {
                    authRepository.refreshAccessToken(refreshToken)
                }

                when (refreshResult) {
                    is Result.Success -> {
                        runBlocking {
                            authDataStore.saveTokens(
                                refreshResult.data.accessToken,
                                refreshResult.data.refreshToken ?: refreshToken,
                                refreshResult.data.expiresIn
                            )
                        }
                        return chain.proceed(addAuthHeader(originalRequest, refreshResult.data.accessToken))
                    }
                    is Result.Error -> {
                        runBlocking { authDataStore.clearTokens() }
                    }
                }
            }
        }

        return response
    }

    private fun addAuthHeader(request: Request, token: String?): Request {
        return if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
    }
} 