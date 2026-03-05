package com.ch000se.ninjauser.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NinjaApiService {

    @GET("v2/randomuser")
    suspend fun getUsers(
        @Query("count") count: Int = 10
    ): List<UserDto>
}