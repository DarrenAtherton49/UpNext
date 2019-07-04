package com.atherton.upnext.presentation.common.addtolists

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
    modules = [MainModule::class, AddToListsModule::class]
)
interface AddToListsComponent : MainComponent {

    fun inject(addToListsDialogFragment: AddToListsDialogFragment)
}


@Module
class AddToListsModule(private val initialState: AddToListsState?) {

    @Provides
    @Named(AddToListsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        movieRepository: MovieRepository,
        tvShowRepository: TvShowRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return AddToListsViewModelFactory(
            initialState,
            movieRepository,
            tvShowRepository,
            appStringProvider,
            schedulers
        )
    }
}
