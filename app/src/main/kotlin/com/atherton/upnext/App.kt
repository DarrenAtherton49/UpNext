package com.atherton.upnext

import android.app.Application
import com.atherton.upnext.util.extensions.onAndroidPieOrLater
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.AppModule
import com.atherton.upnext.util.injection.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary

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

        /*
         * Only using LeakCanary on devices running lower than Android P due to a bug in AOSP causing
         * excessive leaks. See https://github.com/square/leakcanary/issues/1081.
         */
        if (!onAndroidPieOrLater()) {
            LeakCanary.install(this)
        }

        initInjection()
    }

    private fun initInjection() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}