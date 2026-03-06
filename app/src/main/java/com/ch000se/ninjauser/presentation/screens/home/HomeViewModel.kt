package com.ch000se.ninjauser.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.ninjauser.core.domain.util.NetworkError
import com.ch000se.ninjauser.core.domain.util.toNetworkError
import com.ch000se.ninjauser.core.presentation.LazyStateContainer
import com.ch000se.ninjauser.domain.FetchNewUserUseCase
import com.ch000se.ninjauser.domain.GetUsersUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchNewUserUseCase: FetchNewUserUseCase
) : ViewModel() {

    private var isLoading = false

    private val container = LazyStateContainer<HomeScreenState>(
        initialState = HomeScreenState.Loading,
        scope = viewModelScope,
        onStart = { loadInitial() }
    )

    val state = container.state

    private fun loadInitial() {
        viewModelScope.launch {


            isLoading = true

            val cachedUsers = getUsersUseCase()
            val result = fetchNewUserUseCase(PAGE_SIZE)

            container.setState(
                result.fold(
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
            )
            isLoading = false
        }
    }

    fun loadNextPage() {
        if (isLoading) return
        val currentState = container.state.value
        if (currentState !is HomeScreenState.Success) return

        isLoading = true
        container.setState(currentState.copy(isLoadingMore = true))

        viewModelScope.launch {
            fetchNewUserUseCase(PAGE_SIZE)
                .onSuccess { newUsers ->
                    container.setState(
                        HomeScreenState.Success(
                            users = currentState.users + newUsers,
                            isLoadingMore = false
                        )
                    )
                }
                .onFailure {
                    container.setState(currentState.copy(isLoadingMore = false))
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