package com.atherton.upnext

import android.app.Application
import com.atherton.upnext.util.extensions.onAndroidPieOrLater
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.AppModule
import com.atherton.upnext.util.injection.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import com.ww.roxie.Roxie
import timber.log.Timber

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

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Roxie.enableLogging(object : Roxie.Logger {
            override fun log(msg: String) {
                Timber.tag("Roxie").d(msg)
            }
        })

        initInjection()
    }

    private fun initInjection() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
