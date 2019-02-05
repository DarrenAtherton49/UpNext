package com.atherton.upnext.presentation.features.discover.featured

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
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
class DiscoverModule(private val initialState: DiscoverState?) {

    @Provides
    @Named(DiscoverViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(): ViewModelProvider.Factory {
        return DiscoverViewModelFactory(initialState)
    }
}
