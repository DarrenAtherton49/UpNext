package com.atherton.tmdb.ui.main

import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
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