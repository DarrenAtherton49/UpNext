package com.atherton.tmdb.util.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import com.atherton.tmdb.App
import com.atherton.tmdb.data.preferences.LocalStorage
import com.atherton.tmdb.data.preferences.Storage
import com.atherton.tmdb.util.network.AndroidNetworkManager
import com.atherton.tmdb.util.network.NetworkManager
import com.atherton.tmdb.util.threading.RxSchedulers
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AppModule::class]
)
interface AppComponent {

    @ApplicationContext
    fun context(): Context
    fun application(): App
    fun sharedPreferences(): SharedPreferences
    fun networkManager(): NetworkManager
    fun schedulers(): RxSchedulers
    fun storage(): Storage
    fun viewModelFactory(): ViewModelProvider.Factory
}


@Module(
        includes = [ViewModelModule::class]
)
class AppModule(private val application: Application) {

    @Provides
    @Singleton @ApplicationContext
    internal fun provideApplicationContext(): Context = application

    @Provides
    @Singleton internal fun provideApplication(): App = application as App

    @Provides
    @Singleton internal fun provideSharedPrefs(): SharedPreferences =
            application.getSharedPreferences("com.atherton.movies_preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton internal fun provideNetworkManager(): NetworkManager {
        val manager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return AndroidNetworkManager(manager)
    }

    @Provides
    @Singleton internal fun provideSchedulers(): RxSchedulers = RxSchedulers(
            io = Schedulers.io(),
            main = AndroidSchedulers.mainThread()
    )

    @Provides
    @Singleton internal fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Provides
    @Singleton internal fun provideStorage(localStorage: LocalStorage): Storage = localStorage
}
