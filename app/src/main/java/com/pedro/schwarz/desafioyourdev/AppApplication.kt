package com.pedro.schwarz.desafioyourdev

import android.app.Application
import com.pedro.schwarz.desafioyourdev.di.module.daoModule
import com.pedro.schwarz.desafioyourdev.di.module.retrofitModule
import com.pedro.schwarz.desafioyourdev.di.module.uiModule
import com.pedro.schwarz.desafioyourdev.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(retrofitModule, daoModule, uiModule, viewModelModule)
        }
    }
}