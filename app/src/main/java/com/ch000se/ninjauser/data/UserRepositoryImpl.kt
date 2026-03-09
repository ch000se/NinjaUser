package com.ch000se.ninjauser.data

import com.ch000se.ninjauser.data.cache.UserCache
import com.ch000se.ninjauser.data.local.UserDao
import com.ch000se.ninjauser.data.mapper.toDbModelList
import com.ch000se.ninjauser.data.mapper.toUserFromDb
import com.ch000se.ninjauser.data.mapper.toUserListFromDb
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.User
import com.ch000se.ninjauser.domain.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val ninjaApiService: NinjaApiService,
    private val userCache: UserCache
) : UserRepository {

    override suspend fun getUsers(): List<User> {
        return userDao.getUsers().toUserListFromDb()
    }

    override suspend fun getUserById(userId: String): User? {
        return userCache.get(userId) ?: userDao.getUser(userId)?.toUserFromDb()
    }

    override suspend fun fetchUsers(count: Int): Result<List<User>> {
        return try {
            val response = ninjaApiService.getUsers(count)
            val users = response.toDbModelList()
            userDao.replaceAllUsers(users)
            val domainUsers = users.toUserListFromDb()
            userCache.putAll(domainUsers)
            Result.success(domainUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
