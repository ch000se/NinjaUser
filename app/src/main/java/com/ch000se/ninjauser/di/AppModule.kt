package com.ch000se.ninjauser.di

import androidx.room.Room
import com.ch000se.ninjauser.data.UserRepositoryImpl
import com.ch000se.ninjauser.data.cache.UserCache
import com.ch000se.ninjauser.data.cache.UserCacheImpl
import com.ch000se.ninjauser.data.local.NinjaDatabase
import com.ch000se.ninjauser.data.remote.AuthInterceptor
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.FetchNewUserUseCase
import com.ch000se.ninjauser.domain.GetUserUseCase
import com.ch000se.ninjauser.domain.GetUsersUseCase
import com.ch000se.ninjauser.domain.UserRepository
import com.ch000se.ninjauser.presentation.screens.detail.DetailViewModel
import com.ch000se.ninjauser.presentation.screens.home.HomeViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

val appModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        get<Json>().asConverterFactory("application/json".toMediaType())
    }

    singleOf(::AuthInterceptor)

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(get())
            .addConverterFactory(get())
            .build()
    }

    single<NinjaApiService> {
        get<Retrofit>().create()
    }

    single {
        Room.databaseBuilder(
            context = get(),
            klass = NinjaDatabase::class.java,
            name = "ninja.db"
        ).build()
    }

    single { get<NinjaDatabase>().userDao() }

    singleOf(::UserCacheImpl) bind UserCache::class

    singleOf(::UserRepositoryImpl) bind UserRepository::class

    singleOf(::GetUserUseCase)
    singleOf(::GetUsersUseCase)
    singleOf(::FetchNewUserUseCase)

    viewModelOf(::HomeViewModel)

    viewModel { params ->
        DetailViewModel(
            getUserUseCase = get(),
            userId = params.get()
        )
    }
}
