package com.github.catomon.kagamin

import android.app.Application
import com.github.catomon.kagamin.di.appModule
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)
        }
    }
}