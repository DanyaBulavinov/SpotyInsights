package com.daniel.spotyinsights.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class implementing MVI (Model-View-Intent) architecture pattern.
 *
 * @param State The type of UI state this ViewModel will handle
 * @param Event The type of UI events this ViewModel will process
 * @param Effect The type of side effects this ViewModel can emit
 */
abstract class BaseViewModel<State : UiState, Event : UiEvent, Effect : UiEffect> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }

    /**
     * Create the initial UI state for this ViewModel.
     * This will be called once when the ViewModel is created.
     */
    abstract fun createInitialState(): State

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event> = _event.asSharedFlow()

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle UI events emitted by the View.
     * This should be implemented by child ViewModels to process events and update state accordingly.
     */
    abstract fun handleEvent(event: Event)

    /**
     * Update the UI state using a reducer function.
     * The reducer receives the current state and returns a new state.
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = _uiState.value.reduce()
        _uiState.value = newState
    }

    /**
     * Emit a one-time side effect.
     * Effects are used for one-off events like navigation or showing snackbars.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.emit(effectValue) }
    }


    /**
     * Process a new UI event.
     * Events represent user actions or system events that can trigger state changes.
     */
    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }
}
