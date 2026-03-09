package com.ch000se.ninjauser.data.cache

import com.ch000se.ninjauser.domain.User

interface UserCache {
    fun get(userId: String): User?
    fun putAll(users: List<User>)

}