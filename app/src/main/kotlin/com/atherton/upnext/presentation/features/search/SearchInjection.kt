package com.atherton.upnext.presentation.features.search

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.SearchRepository
import com.atherton.upnext.domain.repository.SettingsRepository
import com.atherton.upnext.domain.usecase.GetPopularMoviesTvUseCase
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
        settingsRepository: SettingsRepository,
        searchRepository: SearchRepository,
        configRepository: ConfigRepository,
        popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return SearchViewModelFactory(
            initialState,
            settingsRepository,
            searchRepository,
            popularMoviesTvUseCase,
            configRepository,
            appStringProvider,
            schedulers)
    }
}
