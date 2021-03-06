package com.atherton.upnext.presentation.features.discover.content

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.SettingsRepository
import com.atherton.upnext.domain.usecase.GetDiscoverItemsForFilterUseCase
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
    modules = [MainModule::class, DiscoverContentModule::class]
)
interface DiscoverContentComponent : MainComponent {

    fun inject(discoverContentFragment: DiscoverContentFragment)
}


@Module
class DiscoverContentModule(private val initialState: DiscoverContentState?) {

    @Provides
    @Named(DiscoverContentViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        settingsRepository: SettingsRepository,
        getDiscoverItemsForFilterUseCase: GetDiscoverItemsForFilterUseCase,
        configRepository: ConfigRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return DiscoverContentViewModelFactory(
            initialState,
            settingsRepository,
            getDiscoverItemsForFilterUseCase,
            configRepository,
            appStringProvider,
            schedulers
        )
    }
}
