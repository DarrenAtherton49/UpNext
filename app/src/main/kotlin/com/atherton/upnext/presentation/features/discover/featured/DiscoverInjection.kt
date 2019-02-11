package com.atherton.upnext.presentation.features.discover.featured

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetFeaturedMoviesTvUseCase
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
    modules = [MainModule::class, DiscoverModule::class]
)
interface DiscoverComponent : MainComponent {

    fun inject(discoverFragment: DiscoverFragment)
}


@Module
class DiscoverModule(
    private val initialState: DiscoverState?,
    private val discoverStringProvider: DiscoverStringProvider
) {

    @Provides
    @Named(DiscoverViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        getFeaturedMoviesTvUseCase: GetFeaturedMoviesTvUseCase,
        getConfigUseCase: GetConfigUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return DiscoverViewModelFactory(
            initialState,
            getFeaturedMoviesTvUseCase,
            getConfigUseCase,
            schedulers,
            discoverStringProvider
        )
    }
}
