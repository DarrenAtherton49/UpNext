package com.atherton.upnext.presentation.features.shows

import androidx.fragment.app.FragmentManager
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
    modules = [MainModule::class, ShowsModule::class]
)
interface ShowsComponent : MainComponent {

    fun inject(showsFragment: ShowsFragment)
}


@Module
class ShowsModule(private val initialState: ShowsState?, private val fragmentManager: FragmentManager) {

    @Provides
    @Named(ShowsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ShowsViewModelFactory(initialState)
    }

    @Provides
    @PerView internal fun provideViewPagerAdapter(): ShowsViewPagerAdapter {
        return ShowsViewPagerAdapter(fragmentManager)
    }
}
