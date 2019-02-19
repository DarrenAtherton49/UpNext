package com.atherton.upnext.presentation.features.discover.search

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.*
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
    modules = [MainModule::class, SearchModule::class]
)
interface SearchComponent : MainComponent {

    fun inject(searchFragment: SearchFragment)
}


@Module
class SearchModule(private val initialState: SearchState?) {

    @Provides
    @Named(SearchViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
        getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
        searchMultiUseCase: SearchMultiUseCase,
        getConfigUseCase: GetConfigUseCase,
        popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return SearchViewModelFactory(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            searchMultiUseCase,
            popularMoviesTvUseCase,
            getConfigUseCase,
            schedulers)
    }
}
