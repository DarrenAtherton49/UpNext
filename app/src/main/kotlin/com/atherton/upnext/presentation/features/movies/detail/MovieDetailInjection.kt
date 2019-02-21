package com.atherton.upnext.presentation.features.movies.detail

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetMovieDetailUseCase
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
    @PerView internal fun provideViewModelFactory(
        getMovieDetailUseCase: GetMovieDetailUseCase,
        getConfigUseCase: GetConfigUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return MovieDetailViewModelFactory(initialState, getMovieDetailUseCase, getConfigUseCase, schedulers)
    }
}
