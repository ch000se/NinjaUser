package com.ch000se.ninjauser.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.presentation.LazyStateContainer
import com.ch000se.ninjauser.domain.GetUserUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId")
        ?: throw IllegalArgumentException("itemId is required")

    private val container = LazyStateContainer<DetailScreenState>(
        initialState = DetailScreenState.Loading,
        scope = viewModelScope,
        onStart = { loadUser() }
    )

    val state = container.state

    private fun loadUser() {
        viewModelScope.launch {
            val user = getUserUseCase(userId)
            container.setState(
                if (user != null) {
                    DetailScreenState.Success(user)
                } else {
                    DetailScreenState.Error("Користувача не знайдено")
                }
            )
        }
    }
}

sealed interface DetailScreenState {
    data object Loading : DetailScreenState
    data class Success(val user: User) : DetailScreenState
    data class Error(val message: String) : DetailScreenState
}