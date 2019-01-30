package com.atherton.tmdb.ui.features.movies

import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
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
