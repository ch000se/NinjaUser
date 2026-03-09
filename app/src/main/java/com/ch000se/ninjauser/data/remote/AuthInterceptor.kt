package com.ch000se.ninjauser.data.remote

import com.ch000se.ninjauser.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}
