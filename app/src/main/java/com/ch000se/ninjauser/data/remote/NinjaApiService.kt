package com.ch000se.ninjauser.data.remote

import com.ch000se.ninjauser.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


private const val API_KEY = BuildConfig.API_KEY
interface NinjaApiService {

    @GET("v2/randomuser")
    suspend fun getUsers(
        @Header("X-Api-Key") apiKey: String = API_KEY ,
        @Query("count") count: Int = 10
    ): List<UserDto>
}