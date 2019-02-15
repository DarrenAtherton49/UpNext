package com.atherton.upnext.presentation.main

import androidx.lifecycle.ViewModelProvider
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
    modules = [MainModule::class]
)
interface MainComponent {

    fun inject(mainActivity: MainActivity)
}


@Module
class MainModule(private val initialState: MainState?) {

    @Provides
    @Named(MainViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory = MainViewModelFactory(initialState, schedulers)
}
