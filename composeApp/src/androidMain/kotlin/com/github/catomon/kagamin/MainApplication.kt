package com.github.catomon.kagamin

import android.app.Application
import android.content.Context
import com.github.catomon.kagamin.di.androidModule
import com.github.catomon.kagamin.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

var appContext: (() -> Context)? = null

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = { this@MainApplication }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule, androidModule)
        }
    }
}