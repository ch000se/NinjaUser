package com.ch000se.ninjauser

import android.app.Application
import com.ch000se.ninjauser.di.dataModule
import com.ch000se.ninjauser.di.domainModule
import com.ch000se.ninjauser.di.networkModule
import com.ch000se.ninjauser.di.viewModelModule
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            analytics()
            modules(
                listOf(
                    networkModule,
                    dataModule,
                    domainModule,
                    viewModelModule
                )
            )
        }
    }
}
