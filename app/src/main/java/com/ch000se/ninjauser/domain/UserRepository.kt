package com.ch000se.ninjauser.domain

interface UserRepository {
    suspend fun getUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    suspend fun fetchNewUsers(): Result<List<User>>
}