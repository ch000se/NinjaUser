package com.ch000se.ninjauser.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ch000se.ninjauser.domain.FetchNewUserUseCase
import com.ch000se.ninjauser.domain.GetUsersUseCase
import com.ch000se.ninjauser.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchNewUserUseCase: FetchNewUserUseCase,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val usersPager = fetchNewUserUseCase()
        .cachedIn(viewModelScope)

    private var requiresLoading = true

    private val _cachedUsers = MutableStateFlow<List<User>>(emptyList())
    val cachedUsers: StateFlow<List<User>> = _cachedUsers
        .onStart {
            if (requiresLoading) {
                loadCachedUsers()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private suspend fun loadCachedUsers() {
        requiresLoading = false
        _cachedUsers.value = getUsersUseCase()
    }
}