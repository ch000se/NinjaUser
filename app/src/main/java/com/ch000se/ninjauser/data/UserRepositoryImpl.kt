package com.ch000se.ninjauser.data

import com.ch000se.ninjauser.data.local.UserDao
import com.ch000se.ninjauser.data.mapper.toDbModelList
import com.ch000se.ninjauser.data.mapper.toUser
import com.ch000se.ninjauser.data.mapper.toUserList
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.User
import com.ch000se.ninjauser.domain.UserRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val ninjaApiService: NinjaApiService
) : UserRepository {

    private val usersCache = ConcurrentHashMap<String, User>()

    override suspend fun getUsers(): List<User> {
        return userDao.getUsers().toUserList()
    }

    override suspend fun getUserById(userId: String): User? {
        return usersCache[userId] ?: userDao.getUser(userId)?.toUser()
    }

    override suspend fun fetchUsers(count: Int): Result<List<User>> {
        return try {
            val response = ninjaApiService.getUsers(count)
            val users = response.toDbModelList()
            userDao.replaceAllUsers(users)
            val domainUsers = users.toUserList()
            domainUsers.forEach { usersCache[it.id] = it }
            Result.success(domainUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}