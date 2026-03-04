package com.ch000se.ninjauser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserDbModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String,
    val phone: String?,
    val city: String?,
    val country: String?
)