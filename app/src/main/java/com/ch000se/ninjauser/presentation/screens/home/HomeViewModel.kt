package com.ch000se.ninjauser.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.domain.util.NetworkError
import com.ch000se.ninjauser.core.domain.util.toNetworkError
import com.ch000se.ninjauser.domain.FetchNewUserUseCase
import com.ch000se.ninjauser.domain.GetUsersUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchNewUserUseCase: FetchNewUserUseCase
) : ViewModel() {

    private var requiresLoading = true

    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state = _state
        .onStart {
            if (requiresLoading) {
                loadUser()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeScreenState.Loading
        )

    private fun loadUser() {
        viewModelScope.launch {
            requiresLoading = false

            val result = fetchNewUserUseCase()
            val users = getUsersUseCase()

            _state.value = when {
                users.isEmpty() && result.isFailure ->
                    HomeScreenState.Error(result.exceptionOrNull().toNetworkError())

                result.isFailure ->
                    HomeScreenState.Offline(users, result.exceptionOrNull().toNetworkError())

                else -> HomeScreenState.Success(users)
            }
        }
    }
}

sealed interface HomeScreenState {
    data class Success(val users: List<User>) : HomeScreenState
    data class Error(val error: NetworkError) : HomeScreenState
    data class Offline(val users: List<User>, val error: NetworkError) : HomeScreenState
    data object Loading : HomeScreenState
}