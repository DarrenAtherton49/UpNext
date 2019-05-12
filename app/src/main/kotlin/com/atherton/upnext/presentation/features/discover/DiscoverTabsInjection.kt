package com.atherton.upnext.presentation.features.discover

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.FilterRepository
import com.atherton.upnext.domain.repository.SettingsRepository
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
        settingsRepository: SettingsRepository,
        filterRepository: FilterRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return DiscoverTabsViewModelFactory(
            initialState,
            settingsRepository,
            filterRepository,
            appStringProvider,
            schedulers
        )
    }
}
