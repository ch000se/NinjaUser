package com.ch000se.ninjauser.domain

import javax.inject.Inject

class FetchNewUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> = repository.fetchNewUsers()
}