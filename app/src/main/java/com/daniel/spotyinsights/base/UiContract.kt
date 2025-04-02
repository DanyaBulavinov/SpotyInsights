package com.daniel.spotyinsights.base

/**
 * Base interface for UI states in MVI architecture.
 * Represents the current state of a screen.
 */
interface UiState

/**
 * Base interface for UI events in MVI architecture.
 * Represents user actions or system events that can trigger state changes.
 */
interface UiEvent

/**
 * Base interface for UI effects in MVI architecture.
 * Represents one-time side effects like navigation or showing snackbars.
 */
interface UiEffect