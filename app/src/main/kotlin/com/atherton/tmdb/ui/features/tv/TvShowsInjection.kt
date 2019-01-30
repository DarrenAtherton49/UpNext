package com.atherton.tmdb.ui.features.tv

import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [TvShowsModule::class]
)
interface TvShowsComponent {

    fun inject(tvShowsFragment: TvShowsFragment)
}


@Module
class TvShowsModule
