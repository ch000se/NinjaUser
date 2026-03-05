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
    private var isLoading = false

    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state = _state
        .onStart {
            if (requiresLoading) {
                loadInitial()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeScreenState.Loading
        )

    private fun loadInitial() {
        viewModelScope.launch {
            requiresLoading = false
            isLoading = true

            val cachedUsers = getUsersUseCase()
            val result = fetchNewUserUseCase(PAGE_SIZE)

            _state.value = result.fold(
                onSuccess = { users ->
                    HomeScreenState.Success(users = users)
                },
                onFailure = { error ->
                    if (cachedUsers.isEmpty()) {
                        HomeScreenState.Error(error.toNetworkError())
                    } else {
                        HomeScreenState.Offline(cachedUsers, error.toNetworkError())
                    }
                }
            )
            isLoading = false
        }
    }

    fun loadNextPage() {
        if (isLoading) return
        val currentState = _state.value
        if (currentState !is HomeScreenState.Success) return

        isLoading = true
        _state.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            fetchNewUserUseCase(PAGE_SIZE)
                .onSuccess { newUsers ->
                    _state.value = HomeScreenState.Success(
                        users = currentState.users + newUsers,
                        isLoadingMore = false
                    )
                }
                .onFailure {
                    _state.value = currentState.copy(isLoadingMore = false)
                }
            isLoading = false
        }
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}

sealed interface HomeScreenState {
    data class Success(
        val users: List<User>,
        val isLoadingMore: Boolean = false
    ) : HomeScreenState

    data class Error(val error: NetworkError) : HomeScreenState
    data class Offline(val users: List<User>, val error: NetworkError) : HomeScreenState
    data object Loading : HomeScreenState
}