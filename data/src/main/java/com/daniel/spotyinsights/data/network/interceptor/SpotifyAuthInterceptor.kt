package com.daniel.spotyinsights.data.network.interceptor

import com.daniel.spotyinsights.domain.repository.TokenRepository
import com.daniel.spotyinsights.domain.util.Logger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyAuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val accessToken = runBlocking {
            tokenRepository.accessToken.first()
        }

        return if (accessToken != null) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        } else {
            Logger.e("No access token available for request: ${originalRequest.url}")
            chain.proceed(originalRequest)
        }
    }
} 