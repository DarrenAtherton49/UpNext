package com.atherton.upnext.presentation.features.movies.content

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class, MovieListModule::class]
)
interface MovieListComponent : MainComponent {

    fun inject(movieListFragment: MovieListFragment)
}


@Module
class MovieListModule(private val initialState: MovieListState?) {

    @Provides
    @Named(MovieListViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        movieRepository: MovieRepository,
        configRepository: ConfigRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return MovieListViewModelFactory(
            initialState,
            movieRepository,
            configRepository,
            appStringProvider,
            schedulers
        )
    }
}
