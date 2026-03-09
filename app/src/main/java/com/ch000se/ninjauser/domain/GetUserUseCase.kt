package com.ch000se.ninjauser.domain

class GetUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): User? = repository.getUserById(userId)
}
