package com.daniel.spotyinsights.data.network.model

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()

    companion object {
        fun <T> success(data: T): ApiResponse<T> = Success(data)
        fun error(code: Int, message: String): ApiResponse<Nothing> = Error(code, message)
        fun loading(): ApiResponse<Nothing> = Loading
    }
}