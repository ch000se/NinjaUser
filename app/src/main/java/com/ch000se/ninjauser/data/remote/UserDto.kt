package com.ch000se.ninjauser.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
    val email: String,
    val avatar: String,
    val phone: String?,
    val city: String?,
    val country: String?
)
