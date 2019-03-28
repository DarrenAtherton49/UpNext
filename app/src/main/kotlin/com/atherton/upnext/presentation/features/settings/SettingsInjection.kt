package com.atherton.upnext.presentation.features.settings

import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class, SettingsModule::class]
)
interface SettingsComponent : MainComponent {

    fun inject(settingsFragment: SettingsFragment)
}


@Module
class SettingsModule
