package com.atherton.upnext.presentation.features.movies

import androidx.lifecycle.ViewModelProvider
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
    modules = [MainModule::class, MoviesModule::class]
)
interface MoviesComponent : MainComponent {

    fun inject(moviesFragment: MoviesFragment)
}


@Module
class MoviesModule(private val initialState: MoviesState?) {

    @Provides
    @Named(MoviesViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        movieRepository: MovieRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return MoviesViewModelFactory(
            initialState,
            movieRepository,
            appStringProvider,
            schedulers)
    }
}
