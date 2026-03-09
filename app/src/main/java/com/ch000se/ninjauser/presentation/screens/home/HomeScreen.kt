package com.ch000se.ninjauser.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.ch000se.ninjauser.core.presentation.util.asString
import com.ch000se.ninjauser.domain.User

private const val PREFETCH_DISTANCE = 10

@Composable
fun HomeScreen(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val snackbarShown = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is HomeScreenState.Offline && !snackbarShown.value) {
            snackbarShown.value = true
            val errorMessage = (state as HomeScreenState.Offline).error.asString(context)
            snackbarHostState.showSnackbar(errorMessage)
            snackbarShown.value = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val currentState = state) {
                is HomeScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HomeScreenState.Error -> {
                    Text(
                        text = currentState.error.asString(context),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HomeScreenState.Success -> {
                    UserList(
                        users = currentState.users,
                        isLoadingMore = currentState.isLoadingMore,
                        onLoadMore = viewModel::loadNextPage,
                        onUserClick = onUserClick
                    )
                }

                is HomeScreenState.Offline -> {
                    UserList(
                        users = currentState.users,
                        isLoadingMore = false,
                        onLoadMore = {},
                        onUserClick = onUserClick
                    )
                }
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<User>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(users, key = { _, user -> user.id }) { index, user ->
            UserItem(
                user = user,
                onClick = { onUserClick(user.id) }
            )

            if (index == users.size - PREFETCH_DISTANCE) {
                LaunchedEffect(users.size) {
                    onLoadMore()
                }
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}