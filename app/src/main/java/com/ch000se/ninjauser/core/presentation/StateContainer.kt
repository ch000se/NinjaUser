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
    fun setState(newState: STATE)
    fun updateState(reducer: STATE.() -> STATE)
    fun setup(scope: CoroutineScope, onStart: () -> Unit = {})
}

class LazyStateContainer<STATE>(
    private val initialState: STATE
) : StateContainer<STATE> {

    private val _state = MutableStateFlow(initialState)
    private lateinit var _stateFlow: StateFlow<STATE>
    private var isInitialized = false

    override val state: StateFlow<STATE> get() = _stateFlow

    override fun setup(scope: CoroutineScope, onStart: () -> Unit) {
        _stateFlow = _state
            .onStart {
                if (!isInitialized) {
                    onStart()
                    isInitialized = true
                }
            }
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(5000),
                initialState
            )
    }

    override fun setState(newState: STATE) {
        _state.value = newState
    }

    override fun updateState(reducer: STATE.() -> STATE) {
        _state.update { it.reducer() }
    }
}