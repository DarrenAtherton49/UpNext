package com.atherton.tmdb.ui.features.shows

import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [ShowsModule::class]
)
interface ShowsComponent {

    fun inject(showsFragment: ShowsFragment)
}


@Module
class ShowsModule
