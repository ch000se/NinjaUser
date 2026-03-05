package com.ch000se.ninjauser.domain

interface UserRepository {
    suspend fun getUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    suspend fun fetchUsers(count: Int): Result<List<User>>
}