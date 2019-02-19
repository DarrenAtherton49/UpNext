package com.atherton.upnext.presentation.features.discover

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.usecase.GetDiscoverViewModeUseCase
import com.atherton.upnext.domain.usecase.ToggleDiscoverViewModeUseCase
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
    modules = [MainModule::class, DiscoverTabsModule::class]
)
interface DiscoverTabsComponent : MainComponent {

    fun inject(discoverFragment: DiscoverTabsFragment)
}


@Module
class DiscoverTabsModule(private val initialState: DiscoverTabsState?) {

    @Provides
    @Named(DiscoverTabsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
        getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return DiscoverTabsViewModelFactory(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            schedulers
        )
    }
}
