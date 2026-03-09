package com.ch000se.ninjauser.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.domain.util.NetworkError
import com.ch000se.ninjauser.core.domain.util.toNetworkError
import com.ch000se.ninjauser.core.presentation.DefaultStateContainer
import com.ch000se.ninjauser.core.presentation.StateContainer
import com.ch000se.ninjauser.core.presentation.onStartState
import com.ch000se.ninjauser.domain.FetchNewUserUseCase
import com.ch000se.ninjauser.domain.GetUsersUseCase
import com.ch000se.ninjauser.domain.User
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchNewUserUseCase: FetchNewUserUseCase,
) : ViewModel(), StateContainer<HomeScreenState> by DefaultStateContainer(
    initialState = HomeScreenState.Loading,
) {

    init {
        onStartState(::loadInitial)
    }

    private var isLoading = false

    private fun loadInitial() {
        viewModelScope.launch {
            isLoading = true

            val result = fetchNewUserUseCase(PAGE_SIZE)

            setState(
                result.fold(
                    onSuccess = { users ->
                        HomeScreenState.Success(users = users)
                    },
                    onFailure = { error ->
                        val cachedUsers = getUsersUseCase()
                        if (cachedUsers.isEmpty()) {
                            HomeScreenState.Error(error.toNetworkError())
                        } else {
                            HomeScreenState.Offline(cachedUsers, error.toNetworkError())
                        }
                    }
                )
            )
            isLoading = false
        }
    }

    fun loadNextPage() {
        if (isLoading) return
        val currentState = state.value

        if (currentState !is HomeScreenState.Success) return

        isLoading = true
        setState(currentState.copy(isLoadingMore = true))

        viewModelScope.launch {
            fetchNewUserUseCase(PAGE_SIZE)
                .onSuccess { newUsers ->
                    setState(
                        HomeScreenState.Success(
                            users = currentState.users + newUsers,
                            isLoadingMore = false,
                        )
                    )
                }
                .onFailure {
                    setState(currentState.copy(isLoadingMore = false))
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
