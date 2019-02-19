package com.atherton.upnext.presentation.features.discover.featured

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverMoviesTvUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverViewModeUseCase
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
    modules = [MainModule::class, DiscoverTabModule::class]
)
interface DiscoverTabComponent : MainComponent {

    fun inject(discoverTabFragment: DiscoverTabFragment)
}


@Module
class DiscoverTabModule(private val initialState: DiscoverTabState?) {

    @Provides
    @Named(DiscoverTabViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
        getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
        getConfigUseCase: GetConfigUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return DiscoverTabViewModelFactory(
            initialState,
            getDiscoverViewModeUseCase,
            getDiscoverMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        )
    }
}
