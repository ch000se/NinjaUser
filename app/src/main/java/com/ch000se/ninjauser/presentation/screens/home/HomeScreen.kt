package com.ch000se.ninjauser.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ch000se.ninjauser.core.domain.util.toNetworkError
import com.ch000se.ninjauser.core.presentation.util.asString
import com.ch000se.ninjauser.domain.User

@Composable
fun HomeScreen(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val users = viewModel.usersPager.collectAsLazyPagingItems()
    val cachedUsers by viewModel.cachedUsers.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarShown = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(users.loadState.refresh) {
        if (users.loadState.refresh is LoadState.Error && !snackbarShown.value) {
            snackbarShown.value = true
            val error = (users.loadState.refresh as LoadState.Error).error.toNetworkError()
            snackbarHostState.showSnackbar(error.asString(context))
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
            when {
                users.loadState.refresh is LoadState.Loading && users.itemCount == 0 -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                users.loadState.refresh is LoadState.Error && cachedUsers.isEmpty() -> {
                    val error = (users.loadState.refresh as LoadState.Error).error.toNetworkError()
                    Text(
                        text = error.asString(context),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                users.loadState.refresh is LoadState.Error && cachedUsers.isNotEmpty() -> {
                    UserList(
                        users = cachedUsers,
                        onUserClick = onUserClick
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = users.itemCount,
                            key = { index -> users[index]?.id ?: index }
                        ) { index ->
                            users[index]?.let { user ->
                                UserItem(
                                    user = user,
                                    onClick = { onUserClick(user.id) }
                                )
                            }
                        }

                        if (users.loadState.append is LoadState.Loading) {
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
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<User>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(users, key = { it.id }) { user ->
            UserItem(
                user = user,
                onClick = { onUserClick(user.id) }
            )
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