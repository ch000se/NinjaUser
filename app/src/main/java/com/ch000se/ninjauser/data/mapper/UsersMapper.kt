package com.ch000se.ninjauser.data.mapper

import com.ch000se.ninjauser.data.local.UserDbModel
import com.ch000se.ninjauser.data.remote.UserDto
import com.ch000se.ninjauser.domain.User

fun UserDto.toDbModel(): UserDbModel = UserDbModel(
    id = id,
    name = name,
    username = username,
    fullName = fullName,
    email = email,
    avatarUrl = avatar,
    phone = phone,
    city = city,
    country = country
)

fun UserDbModel.toUser(): User = User(
    id = id,
    name = name,
    username = username,
    fullName = fullName,
    email = email,
    avatarUrl = avatarUrl,
    phone = phone,
    city = city,
    country = country
)

fun List<UserDto>.toDbModelList(): List<UserDbModel> = map { it.toDbModel() }

fun List<UserDbModel>.toUserList(): List<User> = map { it.toUser() }
