package com.atherton.upnext.presentation.main

import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class]
)
interface MainComponent {

    fun inject(mainActivity: MainActivity)
}


@Module
class MainModule