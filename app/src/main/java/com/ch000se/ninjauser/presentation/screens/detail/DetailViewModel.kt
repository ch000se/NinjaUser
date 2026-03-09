package com.ch000se.ninjauser.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.presentation.DefaultStateContainer
import com.ch000se.ninjauser.core.presentation.StateContainer
import com.ch000se.ninjauser.core.presentation.onStartState
import com.ch000se.ninjauser.domain.GetUserUseCase
import com.ch000se.ninjauser.domain.User
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val userId: String
) : ViewModel(), StateContainer<DetailScreenState> by DefaultStateContainer(
    initialState = DetailScreenState.Loading,
) {

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
