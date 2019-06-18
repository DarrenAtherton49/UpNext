package com.atherton.upnext.util.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.ConnectivityManager
import com.atherton.upnext.App
import com.atherton.upnext.BuildConfig
import com.atherton.upnext.data.db.RoomDb
import com.atherton.upnext.data.db.dao.*
import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.data.local.FallbackConfigStore
import com.atherton.upnext.data.local.SharedPreferencesStorage
import com.atherton.upnext.data.network.TmdbApiKeyInterceptor
import com.atherton.upnext.data.network.service.*
import com.atherton.upnext.data.repository.*
import com.atherton.upnext.domain.repository.*
import com.atherton.upnext.presentation.util.AndroidAppStringProvider
import com.atherton.upnext.presentation.util.AppStringProvider
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
    modules = [AppModule::class, RepositoryModule::class, ServiceModule::class, DatabaseModule::class]
)
interface AppComponent {

    @ApplicationContext fun context(): Context
    fun schedulers(): RxSchedulers
    fun settings(): AppSettings
    fun appStringProvider(): AppStringProvider
    fun tvShowRepository(): TvShowRepository
    fun movieRepository(): MovieRepository
    fun personRepository(): PersonRepository
    fun searchRepository(): SearchRepository
    fun configRepository(): ConfigRepository
    fun settingsRepository(): SettingsRepository
    fun filterRepository(): FilterRepository
}


@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton @ApplicationContext
    internal fun provideApplicationContext(): Context = application

    @Provides
    @Singleton internal fun provideApplication(): App = application as App

    @Provides
    @Singleton internal fun provideSharedPrefs(): SharedPreferences =
        application.getSharedPreferences("com.atherton.upnext_preferences", Context.MODE_PRIVATE)

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
    @Singleton internal fun provideSettings(sharedPreferences: SharedPreferencesStorage): AppSettings = sharedPreferences

    @Provides
    @Singleton internal fun provideAppStringProvider(
        androidAppStringProvider: AndroidAppStringProvider
    ): AppStringProvider = androidAppStringProvider

    @Provides
    @Singleton internal fun provideResources(): Resources = application.resources

    companion object {
        private const val TMDB_API_VERSION = 3
        private const val TMDB_API_HOST = "api.themoviedb.org"
        private const val TMDB_API_URL = "https://$TMDB_API_HOST/$TMDB_API_VERSION/"
    }
}

@Module
class RepositoryModule {

    @Provides
    @Singleton internal fun provideTvShowRepository(
        tvShowDao: TvShowDao,
        tvShowService: TmdbTvShowService
    ): TvShowRepository {
        return CachingTvShowRepository(tvShowDao, tvShowService)
    }

    @Provides
    @Singleton internal fun provideMovieRepository(
        movieDao: MovieDao,
        listDao: ListDao,
        movieService: TmdbMovieService
    ): MovieRepository {
        return CachingMovieRepository(movieDao, listDao, movieService)
    }

    @Provides
    @Singleton internal fun provideSearchRepository(
        searchResultDao: SearchResultDao,
        searchService: TmdbSearchService
    ): SearchRepository {
        return CachingSearchRepository(searchResultDao, searchService)
    }

    @Provides
    @Singleton internal fun providePersonRepository(
        personDao: PersonDao,
        personService: TmdbPersonService
    ): PersonRepository  {
        return CachingPersonRepository(personDao, personService)
    }

    @Provides
    @Singleton internal fun provideConfigRepository(
        configDao: ConfigDao,
        configService: TmdbConfigService,
        localConfigStore: FallbackConfigStore
    ): ConfigRepository {
        return CachingConfigRepository(configDao, configService, localConfigStore)
    }

    @Provides
    @Singleton internal fun provideSettingsRepository(appSettings: AppSettings): SettingsRepository {
        return CachingSettingsRepository(appSettings)
    }

    @Provides
    @Singleton internal fun provideFilterRepository(): FilterRepository = CachingFilterRepository()
}

@Module
class ServiceModule {

    @Provides
    @Singleton internal fun provideTvShowService(retrofit: Retrofit): TmdbTvShowService =
        retrofit.create(TmdbTvShowService::class.java)

    @Provides
    @Singleton internal fun provideMovieService(retrofit: Retrofit): TmdbMovieService =
        retrofit.create(TmdbMovieService::class.java)

    @Provides
    @Singleton internal fun provideTmdbSearchService(retrofit: Retrofit): TmdbSearchService =
        retrofit.create(TmdbSearchService::class.java)

    @Provides
    @Singleton internal fun provideTmdbConfigService(retrofit: Retrofit): TmdbConfigService =
        retrofit.create(TmdbConfigService::class.java)

    @Provides
    @Singleton internal fun provideTmdbPersonService(retrofit: Retrofit): TmdbPersonService =
        retrofit.create(TmdbPersonService::class.java)
}

@Module
class DatabaseModule {

    @Provides
    @Singleton internal fun provideRoomDb(@ApplicationContext context: Context): RoomDb {
        return RoomDb.getInstance(context = context, useInMemory = false)
    }

    @Provides
    @Singleton internal fun provideSearchResultDao(roomDb: RoomDb): SearchResultDao {
        return roomDb.getSearchResultDao()
    }

    @Provides
    @Singleton internal fun provideMovieDao(roomDb: RoomDb): MovieDao {
        return roomDb.getMovieDao()
    }

    @Provides
    @Singleton internal fun provideTvShowDao(roomDb: RoomDb): TvShowDao {
        return roomDb.getTvShowDao()
    }

    @Provides
    @Singleton internal fun providePersonDao(roomDb: RoomDb): PersonDao {
        return roomDb.getPersonDao()
    }

    @Provides
    @Singleton internal fun provideListDao(roomDb: RoomDb): ListDao {
        return roomDb.getListDao()
    }

    @Provides
    @Singleton internal fun provideConfigDao(roomDb: RoomDb): ConfigDao {
        return roomDb.getConfigDao()
    }
}
