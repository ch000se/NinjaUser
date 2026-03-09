package com.ch000se.ninjauser.domain

class FetchNewUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(count: Int): Result<List<User>> = repository.fetchUsers(count)
}
