package com.ch000se.ninjauser.patterns.structural.decorator


data class User(
    val id: Int,
    val name: String
)


interface UserRepository {
    fun getUser(id: Int): User
}

class ApiUserRepository : UserRepository {
    override fun getUser(id: Int): User {
        println("API: downloading user $id...")
        return User(id, "User $id")
    }
}

class CacheDecorator(
    private val repository: UserRepository
) : UserRepository by repository {

    private val cache = mutableMapOf<Int, User>()

    override fun getUser(id: Int): User {
        return cache.getOrPut(id) {
            println("Cache: miss")
            repository.getUser(id)
        }.also { println("Cache: hit") }
    }
}

class LoggingDecorator(
    private val repository: UserRepository
) : UserRepository by repository {

    override fun getUser(id: Int): User {
        println("Log: getUser($id)")
        return repository.getUser(id)
    }
}


fun main() {
    val repository: UserRepository =
        LoggingDecorator(
            CacheDecorator(
                ApiUserRepository()
            )
        )

    println("First request")
    repository.getUser(1)

    println("\nSecond request")
    repository.getUser(1)
}