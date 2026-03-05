package com.ch000se.ninjauser.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    fun fetchNewUsers(): Flow<PagingData<User>>
}