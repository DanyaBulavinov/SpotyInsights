package com.daniel.spotyinsights.domain.repository

enum class TimeRange {
    SHORT_TERM,
    MEDIUM_TERM,
    LONG_TERM;

    fun toApiValue(): String = when (this) {
        SHORT_TERM -> "short_term"
        MEDIUM_TERM -> "medium_term"
        LONG_TERM -> "long_term"
    }

    companion object {
        fun fromApiValue(value: String): TimeRange = when (value) {
            "short_term" -> SHORT_TERM
            "medium_term" -> MEDIUM_TERM
            "long_term" -> LONG_TERM
            else -> throw IllegalArgumentException("Unknown time range value: $value")
        }
    }
} 