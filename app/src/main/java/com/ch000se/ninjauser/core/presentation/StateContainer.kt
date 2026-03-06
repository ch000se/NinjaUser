package com.ch000se.ninjauser.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface StateContainer<STATE> {
    val state: StateFlow<STATE>
    fun updateState(reducer: (STATE) -> STATE)
    fun setState(newState: STATE)
}

// Якщо не треба буде onStart()
class StateContainerDelegate<STATE>(
    private val initialState: STATE
) : StateContainer<STATE> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<STATE> = _state

    override fun updateState(reducer: (STATE) -> STATE) {
        _state.update(reducer)
    }

    override fun setState(newState: STATE) {
        _state.value = newState
    }
}

// З onStart()
class LazyStateContainer<STATE>(
    private val initialState: STATE,
    private val scope: CoroutineScope,
    onStart: () -> Unit
) : StateContainer<STATE> {

    private val _state = MutableStateFlow(initialState)
    private var isInitialized = false

    override val state: StateFlow<STATE> = _state
        .onStart {
            if (!isInitialized) {
                onStart()
                isInitialized = true
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialState
        )

    override fun updateState(reducer: (STATE) -> STATE) {
        _state.update(reducer)
    }

    override fun setState(newState: STATE) {
        _state.value = newState
    }
}