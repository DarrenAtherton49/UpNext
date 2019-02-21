package com.atherton.upnext.presentation.features.movies.detail

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.presentation.features.MovieDetail.detail.MovieDetailState
import com.atherton.upnext.presentation.features.MovieDetail.detail.MovieDetailViewModelFactory
import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
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
    modules = [MainModule::class, MovieDetailModule::class]
)
interface MovieDetailComponent : MainComponent {

    fun inject(movieDetailFragment: MovieDetailFragment)
}


@Module
class MovieDetailModule(private val initialState: MovieDetailState?) {

    @Provides
    @Named(MovieDetailViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(schedulers: RxSchedulers): ViewModelProvider.Factory {
        return MovieDetailViewModelFactory(initialState, schedulers)
    }
}
