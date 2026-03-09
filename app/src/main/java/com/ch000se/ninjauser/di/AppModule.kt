package com.ch000se.ninjauser.di

import android.content.Context
import androidx.room.Room
import com.ch000se.ninjauser.data.UserRepositoryImpl
import com.ch000se.ninjauser.data.cache.UserCache
import com.ch000se.ninjauser.data.cache.UserCacheImpl
import com.ch000se.ninjauser.data.local.NinjaDatabase
import com.ch000se.ninjauser.data.remote.AuthInterceptor
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindUserCache(impl: UserCacheImpl): UserCache

    companion object {

        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(json: Json): Converter.Factory =
            json.asConverterFactory("application/json".toMediaType())

        @Provides
        @Singleton
        fun provideOkHttpClient(
            authInterceptor: AuthInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

        @Provides
        @Singleton
        fun providesRetrofit(
            okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.api-ninjas.com/")
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(
            retrofit: Retrofit
        ): NinjaApiService {
            return retrofit.create()
        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): NinjaDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = NinjaDatabase::class.java,
                name = "ninja.db"
            ).build()
        }

        @Singleton
        @Provides
        fun provideUserDao(database: NinjaDatabase) = database.userDao()

    }
}