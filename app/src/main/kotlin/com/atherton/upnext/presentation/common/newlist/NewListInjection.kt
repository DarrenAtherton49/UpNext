package com.atherton.upnext.presentation.common.newlist

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.MovieRepository
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
    modules = [MainModule::class, NewListModule::class]
)
interface NewListComponent : MainComponent {

    fun inject(newListDialogFragment: NewListDialogFragment)
}


@Module
class NewListModule(private val initialState: NewListState?) {

    @Provides
    @Named(NewListViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        movieRepository: MovieRepository,
        tvShowRepository: TvShowRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return NewListViewModelFactory(
            initialState,
            movieRepository,
            tvShowRepository,
            appStringProvider,
            schedulers
        )
    }
}
