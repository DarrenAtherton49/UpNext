package com.atherton.upnext.presentation.features.shows.content

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.TvShowRepository
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
    modules = [MainModule::class, ShowListModule::class]
)
interface ShowListComponent : MainComponent {

    fun inject(showListFragment: ShowListFragment)
}


@Module
class ShowListModule(private val initialState: ShowListState?) {

    @Provides
    @Named(ShowListViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        tvShowRepository: TvShowRepository,
        configRepository: ConfigRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return ShowListViewModelFactory(
            initialState,
            tvShowRepository,
            configRepository,
            appStringProvider,
            schedulers
        )
    }
}
