package com.atherton.upnext.presentation.features.settings

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
    modules = [MainModule::class, SettingsModule::class]
)
interface SettingsComponent : MainComponent {

    fun inject(settingsFragment: SettingsFragment)
}


@Module
class SettingsModule(private val initialState: SettingsState?) {

    @Provides
    @Named(SettingsViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(schedulers: RxSchedulers): ViewModelProvider.Factory {
        return SettingsViewModelFactory(initialState, schedulers)
    }
}
