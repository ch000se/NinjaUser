package com.ch000se.ninjauser.domain

import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): User? = repository.getUserById(userId)
}