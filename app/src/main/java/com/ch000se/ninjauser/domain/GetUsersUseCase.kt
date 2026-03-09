package com.ch000se.ninjauser.domain

class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> = repository.getUsers()
}
