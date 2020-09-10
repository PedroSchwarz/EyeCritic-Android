package com.pedro.schwarz.desafioyourdev

import android.app.Application
import com.pedro.schwarz.desafioyourdev.di.module.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(databaseModule, retrofitModule, daoModule, uiModule, viewModelModule)
        }
    }
}