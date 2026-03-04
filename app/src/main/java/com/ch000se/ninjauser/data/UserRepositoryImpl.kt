package com.ch000se.ninjauser.data

import com.ch000se.ninjauser.data.local.UserDao
import com.ch000se.ninjauser.data.mapper.toDbModelList
import com.ch000se.ninjauser.data.mapper.toEntity
import com.ch000se.ninjauser.data.mapper.toEntityList
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.User
import com.ch000se.ninjauser.domain.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val ninjaApiService: NinjaApiService
) : UserRepository {
    override suspend fun getUsers(): List<User> {
        return userDao.getUsers().toEntityList()
    }

    override suspend fun getUserById(userId: String): User? {
        return userDao.getUser(userId)?.toEntity()
    }

    override suspend fun fetchNewUsers(): Result<List<User>> {
        return try {
            val response = ninjaApiService.getUsers()
            val users = response.toDbModelList()
            userDao.replaceAllUsers(users)
            Result.success(users.toEntityList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}