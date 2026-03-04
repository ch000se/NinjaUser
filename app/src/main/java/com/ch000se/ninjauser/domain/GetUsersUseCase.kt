package com.ch000se.ninjauser.domain

import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> = repository.getUsers()
}