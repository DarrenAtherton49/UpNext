package com.atherton.upnext.util.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.App
import com.atherton.upnext.BuildConfig
import com.atherton.upnext.data.api.TmdbApiKeyInterceptor
import com.atherton.upnext.data.api.TmdbMultiSearchResponseAdapter
import com.atherton.upnext.data.api.TmdbSearchService
import com.atherton.upnext.data.preferences.LocalStorage
import com.atherton.upnext.data.preferences.Storage
import com.atherton.upnext.data.repository.search.CachingSearchRepository
import com.atherton.upnext.data.repository.search.SearchRepository
import com.atherton.upnext.util.network.manager.AndroidNetworkManager
import com.atherton.upnext.util.network.manager.NetworkManager
import com.atherton.upnext.util.network.retrofit.KotlinRxJava2CallAdapterFactory
import com.atherton.upnext.util.threading.RxSchedulers
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
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
    fun searchRepository(): SearchRepository
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
            .add(TmdbMultiSearchResponseAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton internal fun provideTmdbRetrofit(moshi: Moshi): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TmdbApiKeyInterceptor(BuildConfig.TMDB_API_KEY, TMDB_API_HOST))
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(TMDB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(KotlinRxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton internal fun provideTmdbSearchService(retrofit: Retrofit): TmdbSearchService =
        retrofit.create(TmdbSearchService::class.java)

    @Provides
    @Singleton internal fun provideStorage(localStorage: LocalStorage): Storage = localStorage

    @Provides
    @Singleton internal fun provideSearchRepository(searchService: TmdbSearchService): SearchRepository {
        return CachingSearchRepository(searchService)

    }

    companion object {
        private const val TMDB_API_VERSION = 3
        private const val TMDB_API_HOST = "api.themoviedb.org"
        private const val TMDB_API_URL = "https://$TMDB_API_HOST/$TMDB_API_VERSION/"
    }
}
