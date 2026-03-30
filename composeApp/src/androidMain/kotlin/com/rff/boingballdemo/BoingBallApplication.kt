package com.rff.boingballdemo

import android.app.Application
import com.rff.boingballdemo.data.local.initPreferencesDataStore
import com.rff.boingballdemo.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class BoingBallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BoingBallApplication)
        }

        // or in MainActivity ???
        initPreferencesDataStore(this)
    }
}
