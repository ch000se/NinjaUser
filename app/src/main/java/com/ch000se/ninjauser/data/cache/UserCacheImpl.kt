package com.ch000se.ninjauser.data.cache

import com.ch000se.ninjauser.domain.User
import java.util.concurrent.ConcurrentHashMap

class UserCacheImpl : UserCache {

    private val cache = ConcurrentHashMap<String, User>()

    override fun get(userId: String): User? = cache[userId]

    override fun putAll(users: List<User>) {
        users.forEach { cache[it.id] = it }
    }

}
