package com.morphingcoffee.credit_donut

import android.app.Application
import com.morphingcoffee.credit_donut.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/** Custom application class enabling Koin dependency injection within the app **/
class DonutApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        performDI()
    }

    private fun performDI() {
        startKoin {
            androidContext(applicationContext)
            modules(allModules())
        }
    }
}