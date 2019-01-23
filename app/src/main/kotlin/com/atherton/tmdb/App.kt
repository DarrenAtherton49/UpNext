package com.atherton.tmdb

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.AppModule
import com.atherton.tmdb.util.injection.DaggerAppComponent

class App : Application() {

    internal lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        initInjection()
    }

    private fun initInjection() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}