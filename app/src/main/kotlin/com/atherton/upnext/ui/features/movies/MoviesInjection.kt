package com.atherton.upnext.ui.features.movies

import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MoviesModule::class]
)
interface MoviesComponent {

    fun inject(moviesFragment: MoviesFragment)
}


@Module
class MoviesModule
