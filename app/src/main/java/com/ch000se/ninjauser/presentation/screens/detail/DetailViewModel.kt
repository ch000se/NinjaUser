package com.ch000se.ninjauser.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.domain.GetUserUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId")
        ?: throw IllegalArgumentException("itemId is required")

    private val _state = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val state = _state
        .onStart { loadUser() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailScreenState.Loading
        )

    private fun loadUser() {
        viewModelScope.launch {
            val user = getUserUseCase(userId)
            _state.value = if (user != null) {
                DetailScreenState.Success(user)
            } else {
                DetailScreenState.Error("Користувача не знайдено")
            }
        }
    }
}

sealed interface DetailScreenState {
    data object Loading : DetailScreenState
    data class Success(val user: User) : DetailScreenState
    data class Error(val message: String) : DetailScreenState
}