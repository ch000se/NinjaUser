package com.ch000se.ninjauser.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ch000se.ninjauser.data.local.UserDao
import com.ch000se.ninjauser.data.mapper.toUserFromDb
import com.ch000se.ninjauser.data.mapper.toUserListFromDb
import com.ch000se.ninjauser.data.paging.UsersPagingSource
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.User
import com.ch000se.ninjauser.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val ninjaApiService: NinjaApiService
) : UserRepository {

    private val usersCache = ConcurrentHashMap<String, User>()

    override suspend fun getUsers(): List<User> {
        return userDao.getUsers().toUserListFromDb()
    }

    override suspend fun getUserById(userId: String): User? {
        return usersCache[userId] ?: userDao.getUser(userId)?.toUserFromDb()
    }

    override fun fetchNewUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 30)
        ) {
            UsersPagingSource(
                api = ninjaApiService,
                dao = userDao,
                onUsersLoaded = { users -> cacheUsers(users) }
            )
        }.flow
    }

    private fun cacheUsers(users: List<User>) {
        users.forEach { usersCache[it.id] = it }
    }
}