package com.ch000se.ninjauser.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface StateContainer<STATE> {
    val state: StateFlow<STATE>
    fun StateContainer<STATE>.setState(newState: STATE)
    fun StateContainer<STATE>.updateState(reducer: STATE.() -> STATE)
    fun setup(scope: CoroutineScope, onStart: () -> Unit = {})
}


class DefaultStateContainer<STATE>(
    initialState: STATE
) : StateContainer<STATE> {

    private val _state = MutableStateFlow(initialState)
    private var scope: CoroutineScope? = null
    private var onStart: (() -> Unit)? = null
    override val state: StateFlow<STATE> by lazy {
        scope?.let { coroutineScope ->
            onStart?.let { startAction ->
                _state
                    .onStart {
                        startAction()
                    }
                    .stateIn(
                        coroutineScope,
                        SharingStarted.WhileSubscribed(5000),
                        initialState
                    )
            }
        } ?: _state.asStateFlow()
    }

    override fun setup(scope: CoroutineScope, onStart: () -> Unit) {
        this.scope = scope
        this.onStart = onStart
    }

    override fun StateContainer<STATE>.setState(newState: STATE) {
        _state.value = newState
    }

    override fun StateContainer<STATE>.updateState(reducer: STATE.() -> STATE) {
        _state.update { it.reducer() }
    }
}


fun <T> T.onStartState(
    onStart: () -> Unit = {}
) where T : ViewModel, T : StateContainer<*> {
    setup(scope = viewModelScope, onStart = onStart)
}