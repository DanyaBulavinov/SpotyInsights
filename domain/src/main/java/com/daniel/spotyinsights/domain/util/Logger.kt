package com.daniel.spotyinsights.domain.util

import android.util.Log

object Logger {
    private const val TAG = "SpotyInsights"

    fun d(message: String, tag: String = TAG) {
        // Since we're in domain module, we can't access BuildConfig
        // We'll always log in debug for now
        Log.d(tag, message)
    }

    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    fun w(message: String, throwable: Throwable? = null, tag: String = TAG) {
        Log.w(tag, message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        Log.e(tag, message, throwable)
    }

    fun auth(message: String, throwable: Throwable? = null) {
        val authTag = "$TAG:Auth"
        Log.i(authTag, message)
        throwable?.let { Log.e(authTag, "Error details:", it) }
    }

    fun api(message: String, throwable: Throwable? = null) {
        val apiTag = "$TAG:API"
        Log.i(apiTag, message)
        throwable?.let { Log.e(apiTag, "Error details:", it) }
    }

    fun state(message: String, tag: String = TAG) {
        Log.d("$TAG:State:$tag", message)
    }
} 