package com.ch000se.ninjauser.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNewUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<PagingData<User>> = repository.fetchNewUsers()
}