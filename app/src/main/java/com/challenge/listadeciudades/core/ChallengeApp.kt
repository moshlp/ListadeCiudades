package com.challenge.listadeciudades.core

import android.app.Application
import com.challenge.listadeciudades.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ChallengeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChallengeApp)
            modules(appModule)
        }
    }
}