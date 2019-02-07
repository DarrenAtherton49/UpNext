package com.atherton.upnext.presentation.features.discover.search

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.SearchMultiUseCase
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
interface SearchResultsComponent : MainComponent {

    fun inject(searchResultsFragment: SearchResultsFragment)
}


@Module
class SearchModule(private val initialState: SearchResultsState?) {

    @Provides
    @Named(SearchResultsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        searchMultiUseCase: SearchMultiUseCase,
        getConfigUseCase: GetConfigUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return SearchResultsViewModelFactory(initialState, searchMultiUseCase, getConfigUseCase, schedulers)
    }
}
