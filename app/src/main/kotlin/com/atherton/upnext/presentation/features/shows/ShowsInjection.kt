package com.atherton.upnext.presentation.features.shows

import androidx.lifecycle.ViewModelProvider
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
    modules = [MainModule::class, ShowsModule::class]
)
interface ShowsComponent : MainComponent {

    fun inject(showsFragment: ShowsFragment)
}


@Module
class ShowsModule(private val initialState: ShowsState?) {

    @Provides
    @Named(ShowsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(schedulers: RxSchedulers): ViewModelProvider.Factory {
        return ShowsViewModelFactory(initialState, schedulers)
    }
}
