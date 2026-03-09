package com.ch000se.ninjauser.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.presentation.DefaultStateContainer
import com.ch000se.ninjauser.core.presentation.StateContainer
import com.ch000se.ninjauser.core.presentation.onStartState
import com.ch000se.ninjauser.domain.GetUserUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateContainer<DetailScreenState> by DefaultStateContainer(
    initialState = DetailScreenState.Loading,
) {
    private val userId: String = savedStateHandle.get<String>("userId")
        ?: throw IllegalArgumentException("itemId is required")

    init {
        onStartState(::loadUser)
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = getUserUseCase(userId)
            setState(
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