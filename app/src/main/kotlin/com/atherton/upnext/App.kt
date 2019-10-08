package com.atherton.upnext

import android.app.Application
import android.os.Looper
import com.atherton.upnext.util.extension.ioThread
import com.atherton.upnext.util.extension.onAndroidPieOrLater
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.AppModule
import com.atherton.upnext.util.injection.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import com.ww.roxie.Roxie
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class App : Application() {

    //todo check TMDB terms of service and display 'powered by TMDB' logo
    //todo cut down models that we use in fragments/adapters to only contain the fields we need (e.g. instead of whole movie)

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

        // https://www.zacsweers.dev/rxandroids-new-async-api/
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }

        initInjection()

        startDatabase()
    }

    private fun initInjection() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun startDatabase() {
        ioThread {
            // we call a function on one of the room Dao's so that the database gets created and the pre-populated
            // data is inserted. Note that it doesn't matter which Dao function gets called, we just need to create
            // the database before navigating to the shows or movies screen so that the initial data is there.
            appComponent.roomDb().getConfigDao().getConfig()
        }
    }
}
