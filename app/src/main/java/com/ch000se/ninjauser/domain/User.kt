package com.ch000se.ninjauser.domain

data class User(
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